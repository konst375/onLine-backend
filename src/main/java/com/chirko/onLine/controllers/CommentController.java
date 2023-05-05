package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.RQCommentDto;
import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post/{id}")
    public ResponseEntity<CommentDto> addPostComment(@PathVariable(name = "id") UUID postId,
                                                     @RequestBody RQCommentDto dto,
                                                     @AuthenticationPrincipal User user) {
        CommentDto response = commentService.addPostComment(postId, user, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<Set<CommentDto>> getPostComments(@PathVariable(name = "id") UUID postId) {
        Set<CommentDto> response = commentService.getPostComments(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/img/{id}")
    public ResponseEntity<CommentDto> addImgComment(@PathVariable(name = "id") UUID imgId,
                                                    @RequestBody RQCommentDto dto,
                                                    @AuthenticationPrincipal User user) {
        CommentDto response = commentService.addImgComment(imgId, user, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/img/{id}")
    public ResponseEntity<Set<CommentDto>> getImgComments(@PathVariable(name = "id") UUID imgId) {
        Set<CommentDto> response = commentService.getImgComments(imgId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable(name = "id") UUID commentId,
                                                @RequestBody RQCommentDto dto,
                                                @AuthenticationPrincipal User user) {
        CommentDto response = commentService.updateComment(commentId, user, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable(name = "id") UUID commentId,
                                                @AuthenticationPrincipal User user) {
        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok("Comment successful deleted");
    }
}
