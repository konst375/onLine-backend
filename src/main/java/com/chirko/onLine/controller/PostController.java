package com.chirko.onLine.controller;

import com.chirko.onLine.dto.request.CreateUserPostDto;
import com.chirko.onLine.dto.response.PostDto;
import com.chirko.onLine.exception.UserEmailNotFoundException;
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
    public ResponseEntity<String> createUserPost(
            CreateUserPostDto createUserPostDto,
            Principal principal
    ) throws UserEmailNotFoundException {

        postService.createUserPost(principal.getName(), createUserPostDto);

        return new ResponseEntity<>("Post created", HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable(name = "id") UUID id) throws Exception {

        PostDto foundPostDto = postService.findPost(id);

        return new ResponseEntity<>(foundPostDto, HttpStatus.OK);
    }
}

