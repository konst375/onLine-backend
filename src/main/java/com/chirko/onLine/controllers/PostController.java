package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.RQPostDto;
import com.chirko.onLine.dto.response.post.UserPostDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        UserPostDto userPostDto = postService.createUserPost(user, dto);
        return new ResponseEntity<>(userPostDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPostDto> getPost(@PathVariable(name = "id") UUID postId) {
        UserPostDto post = postService.findPostByIdAndFetchImagesAndTagsEagerly(postId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") UUID postId,
                                             @AuthenticationPrincipal User user) {
        postService.deletePost(user, postId);
        return ResponseEntity.ok("Successful deleted");
    }
}
