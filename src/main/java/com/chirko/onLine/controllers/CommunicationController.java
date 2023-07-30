package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.communication.RQChatDto;
import com.chirko.onLine.dto.request.communication.RQMessageDto;
import com.chirko.onLine.dto.response.communication.ChatDto;
import com.chirko.onLine.entities.Chat;
import com.chirko.onLine.entities.Message;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.ChatService;
import com.chirko.onLine.services.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/communication")
public class CommunicationController {
    private final MessageService messageService;
    private final ChatService chatService;


    @PostMapping("/create")
    public ResponseEntity<ChatDto> createMessage(@RequestBody RQMessageDto rqMessageDto,
                                                 @AuthenticationPrincipal User user) {
        Message message = messageService.createMessage(rqMessageDto, user.getId());
        ChatDto response = chatService.getDtoByIdWithAllDependencies(message.getChat().getId(), user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/share/{postId}")
    public ResponseEntity<ChatDto> sharePost(@PathVariable UUID postId,
                                             @RequestBody RQMessageDto rqMessageDto,
                                             @AuthenticationPrincipal User user) {
        Message message = messageService.sharePost(postId, rqMessageDto, user.getId());
        ChatDto response = chatService.getDtoByIdWithAllDependencies(message.getChat().getId(), user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-chat")
    public ResponseEntity<ChatDto> createChat(@ModelAttribute RQChatDto rqChatDto, @AuthenticationPrincipal User user) {
        ChatDto response = chatService.createChat(rqChatDto, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/chats/{chatId}")
    public ResponseEntity<ChatDto> getChat(@PathVariable UUID chatId, @AuthenticationPrincipal User user) {
        ChatDto response = chatService.getDtoByIdWithAllDependencies(chatId, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/chats")
    public ResponseEntity<Set<ChatDto>> getChats(@AuthenticationPrincipal User user) {
        Set<ChatDto> response = chatService.findAllByUserId(user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-message/{messageId}")
    public ResponseEntity<ChatDto> editMessage(@PathVariable UUID messageId,
                                               @RequestBody RQMessageDto rqMessageDto,
                                               @AuthenticationPrincipal User user) {
        Message message = messageService.editMessage(messageId, rqMessageDto, user);
        ChatDto response = chatService.getDtoByIdWithAllDependencies(message.getChat().getId(), user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-chat/{chatId}")
    public ResponseEntity<ChatDto> editChat(@PathVariable UUID chatId,
                                            @ModelAttribute RQChatDto rqMessageDto,
                                            @AuthenticationPrincipal User user) {
        ChatDto response = chatService.editChat(chatId, rqMessageDto, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/chats/{chatId}/leave")
    public ResponseEntity<Set<ChatDto>> leaveChat(@PathVariable UUID chatId, @AuthenticationPrincipal User user) {
        Set<ChatDto> response = chatService.leaveChat(chatId, user.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-message/{messageId}")
    public ResponseEntity<ChatDto> deleteMessage(@PathVariable UUID messageId, @AuthenticationPrincipal User user) {
        Chat chat = messageService.deleteMessage(messageId, user);
        ChatDto response = chatService.getDtoByIdWithAllDependencies(chat.getId(), user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-chat/{chatId}")
    public ResponseEntity<Set<ChatDto>> deleteChat(@PathVariable UUID chatId, @AuthenticationPrincipal User user) {
        chatService.deleteChat(chatId, user);
        Set<ChatDto> response = chatService.findAllByUserId(user.getId());
        return ResponseEntity.ok(response);
    }
}
