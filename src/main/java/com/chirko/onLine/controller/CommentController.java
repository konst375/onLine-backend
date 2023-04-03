package com.chirko.onLine.controller;

import com.chirko.onLine.dto.request.RequestCommentDto;
import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/post/{id}")
    public ResponseEntity<String> addPostComment(@PathVariable(name = "id") UUID postId, Principal principal,
                                                 @RequestBody RequestCommentDto dto) {
        commentService.addPostComment(postId, principal.getName(), dto);
        return ResponseEntity.ok("Comment added");
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<Set<CommentDto>> getPostComments(@PathVariable(name = "id") UUID postId) {
        Set<CommentDto> comments = commentService.getPostComments(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping("/img/{id}")
    public ResponseEntity<String> addImgComment(@PathVariable(name = "id") UUID imageId, Principal principal,
                                                @RequestBody RequestCommentDto dto) {
        commentService.addImgComment(imageId, principal.getName(), dto);
        return ResponseEntity.ok("Comment added");
    }

    @GetMapping("/img/{id}")
    public ResponseEntity<Set<CommentDto>> getImgComments(@PathVariable(name = "id") UUID imgId) {
        Set<CommentDto> comments = commentService.getImgComments(imgId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable(name = "id") UUID commentId, Principal principal,
                                                @RequestBody RequestCommentDto dto) {
        commentService.updateComment(commentId, principal.getName(), dto);
        return ResponseEntity.ok("Comment successful updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable(name = "id") UUID commentId, Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.ok("Comment successful deleted");
    }
}
