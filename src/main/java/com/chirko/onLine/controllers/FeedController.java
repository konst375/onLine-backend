package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.FeedService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<Set<BasePostDto>> getFeed(@AuthenticationPrincipal User user) {
        Set<BasePostDto> posts = feedService.getFeed(user);
        return ResponseEntity.ok(posts);
    }
}
