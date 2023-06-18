package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService likeService;
    @PostMapping("/comment/{id}")
    public ResponseEntity<CommentDto> likeComment(@PathVariable(name = "id") UUID commentId,
                                              @AuthenticationPrincipal User user) {
        CommentDto response = likeService.likeComment(commentId, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/img/{id}")
    public ResponseEntity<ImgDto> likeImg(@PathVariable(name = "id") UUID imgId,
                                          @AuthenticationPrincipal User user) {
        ImgDto response = likeService.likeImg(imgId, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/post/{id}")
    public ResponseEntity<BasePostDto> likePost(@PathVariable(name = "id") UUID postId,
                                              @AuthenticationPrincipal User user) {
        BasePostDto response = likeService.likePost(postId, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> unlike(@PathVariable(name = "id") UUID id) {
        likeService.unlike(id);
        return ResponseEntity.ok("Successful unliked");
    }
}
