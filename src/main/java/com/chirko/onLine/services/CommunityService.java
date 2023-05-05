package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.CommunityMapper;
import com.chirko.onLine.dto.mappers.PostMapper;
import com.chirko.onLine.dto.mappers.UserMapper;
import com.chirko.onLine.dto.request.community.RQRegisterCommunityDto;
import com.chirko.onLine.dto.response.community.BaseCommunityDto;
import com.chirko.onLine.dto.response.community.CommunityPageDto;
import com.chirko.onLine.dto.response.post.CommunityPostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.Community;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.Role;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.CommunityRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommunityService {
    private final ImgService imgService;
    private final TagService tagService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommunityMapper communityMapper;
    private final CommunityRepo communityRepo;

    @Transactional
    public BaseCommunityDto createCommunity(User user, RQRegisterCommunityDto dto) {
        Community community = Community.builder()
                .name(dto.getName())
                .subject(dto.getSubject())
                .admin(user)
                .build();
        community.setTags(getTags(dto, community));
        if (dto.getAvatar() != null) {
            community.setImages(Set.of(imgService.buildCommunityAvatar(dto.getAvatar(), community)));
        }
        communityRepo.save(community);
        user.setRole(Role.ADMIN);
        return communityMapper.toBaseDto(community);
    }

    public CommunityPageDto getCommunityPage(UUID communityId) {
        Community community = communityRepo.findByIdAndFetchAllDependencies(communityId)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        Set<CommunityPostDto> postsDto = postMapper.toCommunityPostsDto(community.getPosts());
        return communityMapper.toCommunityPageDto(community, postsDto);
    }

    public Community getCommunity(UUID communityId) {
        return communityRepo.findByIdAndFetchImagesEagerly(communityId)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public CommunityPageDto updateAvatar(UUID communityId, MultipartFile avatar, User user) {
        Community community = getCommunityAndCheckUserAccess(communityId, user);
        byte[] img;
        if (avatar != null) {
            img = imgService.getBytes(avatar);
            if (community.getAvatar() != null) {
                community.getAvatar().setImg(img);
            } else {
                community.getImages().add(Img.builder()
                        .img(img)
                        .isAvatar(true)
                        .community(community)
                        .build());
            }
        }
        Set<CommunityPostDto> posts = postMapper.toCommunityPostsDto(community.getPosts());
        return communityMapper.toCommunityPageDto(community, posts);
    }

    @Transactional
    public CommunityPageDto updateCover(UUID communityId, MultipartFile cover, User user) {
        Community community = getCommunityAndCheckUserAccess(communityId, user);
        byte[] img;
        if (cover != null) {
            img = imgService.getBytes(cover);
            if (community.getCover() != null) {
                community.getCover().setImg(img);
            } else {
                community.getImages().add(Img.builder()
                        .img(img)
                        .isCover(true)
                        .community(community)
                        .build());
            }
        }
        Set<CommunityPostDto> posts = postMapper.toCommunityPostsDto(community.getPosts());
        return communityMapper.toCommunityPageDto(community, posts);
    }

    public void deleteCommunity(UUID communityId, User user) {
        Community community = communityRepo.findById(communityId)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        Set<Community> communities = communityRepo.findByAdmin(user)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (communities.contains(community)) {
            communityRepo.delete(community);
        } else {
            throw new OnLineException("Access denied, communityId: " + communityId + " userId: " + user.getId(),
                    ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public CommunityPageDto subscribe(UUID communityId, User user) {
        Community community = communityRepo.findByIdAndFetchAllDependencies(communityId)
                .orElseThrow(() -> new OnLineException("Community not found, communityId: " + communityId,
                        ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        community.getFollowers().add(user);
        Set<CommunityPostDto> posts = postMapper.toCommunityPostsDto(community.getPosts());
        return communityMapper.toCommunityPageDto(community, posts);
    }

    @Transactional
    public CommunityPageDto unsubscribe(UUID communityId, User user) {
        Community community = communityRepo.findByIdAndFetchAllDependencies(communityId)
                .orElseThrow(() -> new OnLineException("Community not found, communityId: " + communityId,
                        ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        community.getFollowers().remove(user);
        Set<CommunityPostDto> posts = postMapper.toCommunityPostsDto(community.getPosts());
        return communityMapper.toCommunityPageDto(community, posts);
    }

    public Set<BaseUserDto> getFollowers(UUID communityId) {
        Set<User> followers = communityRepo.findFollowersByCommunityId(communityId)
                .orElseThrow(() -> new OnLineException("Community not found, communityId: " + communityId,
                        ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        followers.forEach(user -> user.setImages(imgService.findUserImages(user)));
        return userMapper.toBaseUsersDto(followers);
    }

    private Set<Tag> getTags(RQRegisterCommunityDto dto, Community community) {
        String tags = dto.getTags();
        if (StringUtils.isAllBlank(tags)) {
            return Collections.emptySet();
        }
        return Arrays.stream(tags.split("#"))
                .filter(text -> !text.isEmpty())
                .map(tagName -> tagService.createCommunityTag(community, tagName))
                .collect(Collectors.toSet());
    }

    private Community getCommunityAndCheckUserAccess(UUID communityId, User user) {
        Community community = communityRepo.findByIdAndFetchAllDependencies(communityId)
                .orElseThrow(() -> new OnLineException(
                        "Community not found, communityId: " + communityId,
                        ErrorCause.COMMUNITY_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        if (community.getAdmin().equals(user)) {
            return community;
        } else {
            throw new OnLineException(
                    "Access denied, userId: " + user.getId() + " communityId: " + communityId,
                    ErrorCause.ACCESS_DENIED,
                    HttpStatus.FORBIDDEN);
        }
    }
}
