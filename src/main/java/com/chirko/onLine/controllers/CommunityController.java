package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.RegisterCommunityRequestDto;
import com.chirko.onLine.dto.response.community.BaseCommunityDto;
import com.chirko.onLine.dto.response.community.CommunityPageDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.CommunityService;
import com.chirko.onLine.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/community")
public class CommunityController {
    private final CommunityService communityService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Set<BaseCommunityDto>> getCommunities(@AuthenticationPrincipal User user) {
        Set<BaseCommunityDto> response = communityService.getAllCommunities(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<BaseCommunityDto> createCommunity(RegisterCommunityRequestDto dto,
                                                            @AuthenticationPrincipal User user) {
        BaseCommunityDto response = communityService.createCommunity(user, dto);
        userService.giveAdmin(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityPageDto> getCommunityPage(@PathVariable UUID communityId) {
        CommunityPageDto response = communityService.getCommunityPage(communityId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{communityId}/followers")
    ResponseEntity<Set<BaseUserDto>> getFollowers(@PathVariable UUID communityId) {
        Set<BaseUserDto> response = communityService.getFollowers(communityId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{communityId}/avatar/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityPageDto> updateAvatar(@PathVariable UUID communityId,
                                                         @RequestParam("image") MultipartFile avatar,
                                                         @AuthenticationPrincipal User user) {
        CommunityPageDto response = communityService.updateAvatar(communityId, avatar, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{communityId}/cover/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityPageDto> updateCover(@PathVariable UUID communityId,
                                                        @RequestParam("image") MultipartFile cover,
                                                        @AuthenticationPrincipal User user) {
        CommunityPageDto response = communityService.updateCover(communityId, cover, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{communityId}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCommunity(@PathVariable UUID communityId, @AuthenticationPrincipal User user) {
        communityService.deleteCommunity(communityId, user);
        return ResponseEntity.ok("deleted successful");
    }

    @PutMapping("/{communityId}/subscribe")
    public ResponseEntity<CommunityPageDto> subscribe(@PathVariable UUID communityId,
                                                      @AuthenticationPrincipal User user) {
        CommunityPageDto response = communityService.subscribe(communityId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{communityId}/unsubscribe")
    public ResponseEntity<CommunityPageDto> unsubscribe(@PathVariable UUID communityId,
                                                        @AuthenticationPrincipal User user) {
        CommunityPageDto response = communityService.unsubscribe(communityId, user);
        return ResponseEntity.ok(response);
    }
}
