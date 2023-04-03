package com.chirko.onLine.controller;

import com.chirko.onLine.dto.request.UserPostDto;
import com.chirko.onLine.dto.response.PostDto;
import com.chirko.onLine.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<String> createUserPost(UserPostDto dto, Principal principal) {
        postService.createUserPost(principal.getName(), dto);
        return new ResponseEntity<>("Post created", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable(name = "id") UUID postId) {
        PostDto foundPostDto = postService.findPostWithEagerlyImagesById(postId);
        return new ResponseEntity<>(foundPostDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePost(@PathVariable(name = "id") UUID postId, UserPostDto dto,
                                             Principal principal) {
        postService.updatePost(principal.getName(), postId, dto);
        return ResponseEntity.ok("Successful updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") UUID postId, Principal principal) {
        postService.deletePost(principal.getName(), postId);
        return ResponseEntity.ok("Successful deleted");
    }
}
