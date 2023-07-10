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

    @PostMapping("/create/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<CommunityPostDto> createCommunityPost(RQPostDto dto,
                                                                @PathVariable(name = "id") UUID communityId) {
        CommunityPostDto response = postService.createCommunityPost(communityId, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasePostDto> getPost(@PathVariable(name = "id") UUID postId) {
        BasePostDto response = postService.getBasePostDtoById(postId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BasePostDto> updatePost(RQPostDto dto,
                                                  @PathVariable(name = "id") UUID postId,
                                                  @AuthenticationPrincipal User user) {
        BasePostDto response = postService.updatePost(postId, user, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") UUID postId,
                                             @AuthenticationPrincipal User user) {
        postService.deletePost(user, postId);
        return ResponseEntity.ok("Successful deleted");
    }
}
