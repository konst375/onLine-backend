package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.ChatMapper;
import com.chirko.onLine.dto.request.communication.ChatRequestDto;
import com.chirko.onLine.dto.response.communication.ChatDto;
import com.chirko.onLine.entities.Chat;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.postgres.ChatRepo;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepo chatRepo;
    private final ChatMapper chatMapper;
    private final UserService userService;
    private final ImgService imgService;

    public Chat createPersonalChat(User... users) {
        return chatRepo.save(
                Chat.builder()
                        .participants(Set.of(users))
                        .build());
    }

    public ChatDto createChat(ChatRequestDto dto, User user) {
        Set<User> participants = userService.findAllByIdWithImages(dto.getParticipants());
        Chat chat = Chat.builder()
                .name(dto.getName() == null
                        ? participants.stream()
                        .map(participant -> participant.getName().concat(" ").concat(participant.getSurname()))
                        .collect(Collectors.toSet()).toString()
                        : dto.getName())
                .admin(user.getId())
                .participants(participants)
                .avatar(imgService.createAvatar(dto.getAvatar()))
                .build();
        chat.getAvatar().setChat(chat);
        chatRepo.save(chat);
        return chatMapper.toDto(chat);
    }

    public Chat findByIdWithAllDependenciesAndCheckUserAccess(UUID chatId, User user) {
        Chat chat = chatRepo.findByIdWithAllDependencies(chatId).orElseThrow(() -> new OnLineException(
                "Chat not found, chatId: " + chatId,
                ErrorCause.CHAT_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        if (!chat.getParticipants().contains(user)) {
            throw new OnLineException(ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
        return chat;
    }

    public ChatDto getDtoByIdWithAllDependencies(UUID chatId, User user) {
        return chatMapper.toDto(findByIdWithAllDependenciesAndCheckUserAccess(chatId, user));
    }

    public Set<ChatDto> findAllByUserId(UUID userId) {
        User user = userService.findByIdWithChats(userId);
        Set<Chat> chats = chatRepo.findAllByIdWithAllDependencies(user.getChats())
                .orElseThrow(() -> new OnLineException(
                        "Chats not found, userId: " + userId,
                        ErrorCause.CHAT_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        return chatMapper.toDtoSet(chats);
    }

    public Set<ChatDto> leaveChat(UUID chatId, UUID userId) {
        Chat chat = chatRepo.findByIdWithAllDependencies(chatId).orElseThrow(() -> new OnLineException(
                "Chat not found, chatId: " + chatId,
                ErrorCause.CHAT_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        chat.getParticipants().remove(userService.findById(userId));
        chatRepo.save(chat);
        return findAllByUserId(userId);
    }

    public ChatDto editChat(UUID chatId, ChatRequestDto rqMessageDto, User user) {
        Chat chat = chatRepo.findByIdWithAllDependencies(chatId).orElseThrow(() -> new OnLineException(
                "Chat not found, chatId: " + chatId,
                ErrorCause.CHAT_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        if (!chat.getAdmin().equals(user.getId())) {
            throw new OnLineException("Chat editing permission denied", ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
        chat.setName(rqMessageDto.getName());
        if (chat.getAvatar() != null) {
            chat.getAvatar().setImg(imgService.getBytes(rqMessageDto.getAvatar()));
        } else {
            chat.setAvatar(imgService.createAvatar(rqMessageDto.getAvatar()));
            chat.getAvatar().setChat(chat);
        }
        Set<UUID> oldParticipants = chat.getParticipants().stream().map(User::getId).collect(Collectors.toSet());
        Set<UUID> newParticipants = rqMessageDto.getParticipants();
        // find which users should be deleted and added
        Set<UUID> usersIdsToDelete = Sets.difference(oldParticipants, newParticipants);
        Set<UUID> usersIdsToAdd = Sets.difference(newParticipants, oldParticipants);
        Set<User> usersToDelete = userService.findAllById(usersIdsToDelete);
        Set<User> usersToAdd = userService.findAllByIdWithImages(usersIdsToAdd);
        // deleting and adding users
        chat.getParticipants().removeAll(usersToDelete);
        chat.getParticipants().addAll(usersToAdd);
        chatRepo.save(chat);
        return chatMapper.toDto(chat);
    }

    public void deleteChat(UUID chatId, User user) {
        Chat chat = chatRepo.findById(chatId).orElseThrow(() -> new OnLineException(
                ErrorCause.CHAT_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        if (!chat.getAdmin().equals(user.getId())) {
            throw new OnLineException(
                    "Chat editing permission denied, chatId: " + chatId,
                    ErrorCause.ACCESS_DENIED,
                    HttpStatus.FORBIDDEN);
        }
        chatRepo.delete(chat);
    }
}
