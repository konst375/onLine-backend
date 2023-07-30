package com.chirko.onLine.services;

import com.chirko.onLine.dto.request.communication.RQMessageDto;
import com.chirko.onLine.entities.Chat;
import com.chirko.onLine.entities.Message;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.MessageRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageService {
    private final MessageRepo messageRepo;
    private final ChatService chatService;
    private final UserService userService;
    private final PostService postService;
    private final ImgService imgService;
    private final TagScoresService tagScoresService;

    public Message createMessage(RQMessageDto rqMessageDto, UUID userId) {
        User user = userService.findByIdWithImages(userId);
        Message message = buidMessage(rqMessageDto, user);
        return messageRepo.save(message);
    }

    @Transactional
    public Message editMessage(UUID messageId, RQMessageDto rqMessageDto, User user) {
        Message message = messageRepo.findById(messageId).orElseThrow(() -> new OnLineException(
                ErrorCause.MESSAGES_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        if (!message.getSender().equals(user)) {
            throw new OnLineException(
                    "Message editing permission denied, userId: " + user.getId(),
                    ErrorCause.ACCESS_DENIED,
                    HttpStatus.FORBIDDEN);
        }
        message.setText(rqMessageDto.getText());
        message.getSender().setImages(imgService.findUserImages(user));
        return message;
    }

    public Message sharePost(UUID postId, RQMessageDto rqMessageDto, UUID userId) {
        Post post = postService.findPostWithAllDependencies(postId);
        User user = userService.findByIdWithImages(userId);
        Message message = buidMessage(rqMessageDto, user);
        tagScoresService.writeDownThatPostShared(user, post);
        return message;
    }

    private Message buidMessage(RQMessageDto rqMessageDto, User user) {
        return Message.builder()
                .sender(user)
                .text(rqMessageDto.getText())
                .chat(rqMessageDto.getChat() == null
                        ? chatService.createPersonalChat(user, userService.findById(rqMessageDto.getRecipient()))
                        : chatService.findByIdWithAllDependenciesAndCheckUserAccess(rqMessageDto.getChat(), user))
                .build();
    }

    public Chat deleteMessage(UUID messageId, User user) {
        Message message = messageRepo.findById(messageId).orElseThrow(() -> new OnLineException(
                ErrorCause.MESSAGES_NOT_FOUND,
                HttpStatus.NOT_FOUND));
        Chat chat = message.getChat();
        if (!message.getSender().equals(user)) {
            throw new OnLineException("Message editing permission denied, userId: " + user.getId(),
                    ErrorCause.ACCESS_DENIED,
                    HttpStatus.FORBIDDEN);
        }
        messageRepo.delete(message);
        return chat;
    }
}
