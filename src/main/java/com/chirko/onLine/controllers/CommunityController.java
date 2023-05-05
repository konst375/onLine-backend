package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.request.community.RQRegisterCommunityDto;
import com.chirko.onLine.dto.response.community.BaseCommunityDto;
import com.chirko.onLine.dto.response.community.CommunityPageDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.services.CommunityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/create")
    public ResponseEntity<BaseCommunityDto> createCommunity(RQRegisterCommunityDto dto,
                                                            @AuthenticationPrincipal User user) {
        BaseCommunityDto response = communityService.createCommunity(user, dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityPageDto> getCommunityPage(@PathVariable(name = "id") UUID communityId) {
        CommunityPageDto response = communityService.getCommunityPage(communityId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/followers")
    ResponseEntity<Set<BaseUserDto>> getFollowers(@PathVariable(name = "id") UUID communityId) {
        Set<BaseUserDto> response = communityService.getFollowers(communityId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/avatar/update")
    public ResponseEntity<CommunityPageDto> updateAvatar(@PathVariable(name = "id") UUID communityId,
                                               @RequestParam("image") MultipartFile avatar,
                                               @AuthenticationPrincipal User user) {
        CommunityPageDto response = communityService.updateAvatar(communityId, avatar, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cover/update")
    public ResponseEntity<CommunityPageDto> updateCover(@PathVariable(name = "id") UUID communityId,
                                              @RequestParam("image") MultipartFile cover,
                                              @AuthenticationPrincipal User user) {
        CommunityPageDto response = communityService.updateCover(communityId, cover, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteCommunity(@PathVariable(name = "id") UUID communityId,
                                                  @AuthenticationPrincipal User user) {
        communityService.deleteCommunity(communityId, user);
        return ResponseEntity.ok("deleted successful");
    }

    @PutMapping("/{id}/subscribe")
    public ResponseEntity<CommunityPageDto> subscribe(@PathVariable(name = "id") UUID communityId,
                                            @AuthenticationPrincipal User user) {
        CommunityPageDto response = communityService.subscribe(communityId, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}/unsubscribe")
    public ResponseEntity<CommunityPageDto> unsubscribe(@PathVariable(name = "id") UUID communityId,
                                              @AuthenticationPrincipal User user) {
        CommunityPageDto response = communityService.unsubscribe(communityId, user);
        return ResponseEntity.ok(response);
    }
}
