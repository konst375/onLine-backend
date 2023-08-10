package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.RQPostDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.post.CommunityPostDto;
import com.chirko.onLine.dto.response.post.UserPostDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<UserPostDto> createUserPost(RQPostDto dto, @AuthenticationPrincipal User user) {
        UserPostDto response = postService.createUserPost(user, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create/{communityId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<CommunityPostDto> createCommunityPost(RQPostDto dto, @PathVariable UUID communityId) {
        CommunityPostDto response = postService.createCommunityPost(communityId, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<BasePostDto> getPost(@PathVariable UUID postId) {
        BasePostDto response = postService.getBasePostDtoById(postId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<BasePostDto> updatePost(RQPostDto dto,
                                                  @PathVariable UUID postId,
                                                  @AuthenticationPrincipal User user) {
        BasePostDto response = postService.updatePost(postId, user, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable UUID postId, @AuthenticationPrincipal User user) {
        postService.deletePost(user, postId);
        return ResponseEntity.ok("Successful deleted");
    }
}
