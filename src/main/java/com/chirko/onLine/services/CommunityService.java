package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.CommunityMapper;
import com.chirko.onLine.dto.mappers.PostMapper;
import com.chirko.onLine.dto.mappers.UserMapper;
import com.chirko.onLine.dto.request.community.RQRegisterCommunityDto;
import com.chirko.onLine.dto.response.community.BaseCommunityDto;
import com.chirko.onLine.dto.response.community.CommunityPageDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.user.BaseUserDto;
import com.chirko.onLine.entities.Community;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.Role;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.CommunityRepo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CommunityService {
    private final ImgService imgService;
    private final TagService tagService;
    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommunityMapper communityMapper;
    private final CommunityRepo communityRepo;

    //    @Transactional
    public BaseCommunityDto createCommunity(User user, RQRegisterCommunityDto dto) {
        Community community = Community.builder()
                .name(dto.getName())
                .subject(dto.getSubject())
                .admin(user)
                .images(dto.getAvatar() == null ? null : List.of(imgService.createAvatar(dto.getAvatar())))
                .tags(tagService.createTags(dto.getTags()))
                .build();
        if (community.getAvatar() != null) {
            community.getImages().forEach(img -> img.setCommunity(community));
        }
        Community savedCommunity = communityRepo.save(community);
        user.setRole(Role.ADMIN);
        return communityMapper.toBaseDto(savedCommunity);
    }

    public CommunityPageDto getCommunityPage(UUID communityId) {
        Community community = communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(communityId)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        // reduce duplicates images at Community.images collection
        community.setImages(Lists.newArrayList(Sets.newLinkedHashSet(community.getImages())));

        Set<BasePostDto> postsDto = getPostsDtoSet(communityId, community);
        return communityMapper.toCommunityPageDto(community, postsDto);
    }

    public Community getCommunity(UUID communityId) {
        return communityRepo.findByIdWithTagsAndImages(communityId)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public CommunityPageDto updateAvatar(UUID communityId, @NonNull MultipartFile avatar, User user) {
        Community community = getCommunityAndCheckUserAccess(communityId, user);
        if (community.getAvatar() != null) {
            community.getAvatar().setImg(imgService.getBytes(avatar));
        } else {
            community.getImages().add(imgService.createAvatar(avatar));
        }
        Set<BasePostDto> postsDto = getPostsDtoSet(communityId, community);
        return communityMapper.toCommunityPageDto(community, postsDto);
    }

    @Transactional
    public CommunityPageDto updateCover(UUID communityId, @NonNull MultipartFile cover, User user) {
        Community community = getCommunityAndCheckUserAccess(communityId, user);
        if (community.getCover() != null) {
            community.getCover().setImg(imgService.getBytes(cover));
        } else {
            community.getImages().add(imgService.createCover(cover));
        }
        Set<BasePostDto> postsDto = getPostsDtoSet(communityId, community);
        return communityMapper.toCommunityPageDto(community, postsDto);
    }

    public void deleteCommunity(UUID communityId, User user) {
        Community community = communityRepo.findById(communityId)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        Set<Community> communities = communityRepo.findByAdmin(user)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (communities.contains(community)) {
            communityRepo.delete(community);
        } else {
            throw new OnLineException(
                    "Access denied, communityId: " + communityId + " userId: " + user.getId(),
                    ErrorCause.ACCESS_DENIED,
                    HttpStatus.FORBIDDEN);
        }
    }

    @Transactional
    public CommunityPageDto subscribe(UUID communityId, User user) {
        Community community = communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(communityId)
                .orElseThrow(() -> new OnLineException(
                        "Community not found, communityId: " + communityId,
                        ErrorCause.COMMUNITY_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        community.getFollowers().add(user);
        Set<BasePostDto> postsDto = postMapper.toBasePostsDto(community.getPosts());
        return communityMapper.toCommunityPageDto(community, postsDto);
    }

    @Transactional
    public CommunityPageDto unsubscribe(UUID communityId, User user) {
        Community community = communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(communityId)
                .orElseThrow(() -> new OnLineException(
                        "Community not found, communityId: " + communityId,
                        ErrorCause.COMMUNITY_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        community.getFollowers().remove(user);
        Set<BasePostDto> Dto = postMapper.toBasePostsDto(community.getPosts());
        return communityMapper.toCommunityPageDto(community, Dto);
    }

    public Set<BaseUserDto> getFollowers(UUID communityId) {
        Set<User> followers = communityRepo.findFollowersByCommunityId(communityId)
                .orElseThrow(() -> new OnLineException(
                        "Community not found, communityId: " + communityId,
                        ErrorCause.COMMUNITY_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        followers.forEach(user -> user.setImages(imgService.findUserImages(user)));
        return userMapper.toBaseUsersDto(followers);
    }

    public Set<User> getModerators(UUID communityId) {
        return communityRepo.findModeratorsById(communityId)
                .orElseThrow(() -> new OnLineException(
                        "Community not found, communityId: " + communityId,
                        ErrorCause.COMMUNITY_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    private Community getCommunityAndCheckUserAccess(UUID communityId, User user) {
        Community community = communityRepo.findByIdAndFetchAllDependenciesWithoutPosts(communityId)
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

    private Set<BasePostDto> getPostsDtoSet(UUID communityId, Community community) {
        Community communityWithPosts = communityRepo.findCommunityWithPostsAndTheirImagesAndTags(communityId)
                .orElseThrow(() -> new OnLineException(ErrorCause.COMMUNITY_NOT_FOUND, HttpStatus.NOT_FOUND));
        Set<Post> posts = communityWithPosts.getPosts();
        // reduce duplicate images at Community.posts.images collection
        posts.forEach(post -> {
            post.setImages(Lists.newArrayList(Sets.newLinkedHashSet(post.getImages())));
            post.setCommunity(community);
        });
        Set<BasePostDto> postsDto = postMapper.toBasePostsDto(posts);
        community.setPosts(posts);
        return postsDto;
    }
}
