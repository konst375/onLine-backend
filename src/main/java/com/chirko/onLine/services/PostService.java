package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.PostMapper;
import com.chirko.onLine.dto.request.RQPostDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.dto.response.post.CommunityPostDto;
import com.chirko.onLine.dto.response.post.UserPostDto;
import com.chirko.onLine.entities.*;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.PostRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final TagService tagService;
    private final ImgService imgService;
    private final CommunityService communityService;
    private final PostMapper postMapper;
    private final PostRepo postRepo;

    public UserPostDto createUserPost(User user, RQPostDto dto) {
        user.setImages(imgService.findUserImages(user));
        Post post = Post.builder()
                .user(user)
                .text(dto.getText())
                .build();
        post.setImages(getImages(dto, post));
        post.setTags(getTags(dto, post));
        postRepo.save(post);
        return postMapper.toUserPostDto(post);
    }

    public CommunityPostDto createCommunityPost(UUID communityId, RQPostDto dto) {
        Community community = communityService.getCommunity(communityId);
        Post post = Post.builder()
                .community(community)
                .text(dto.getText())
                .build();
        post.setImages(getImages(dto, post));
        post.setTags(getTags(dto, post));
        postRepo.save(post);
        return postMapper.toCommunityPostDto(post);
    }

    public Post getById(UUID postId) {
        return postRepo.findById(postId)
                .orElseThrow(() -> new OnLineException(
                        "Post not found, postId: " + postId,
                        ErrorCause.POST_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    public UserPostDto findPostByIdAndFetchImagesAndTagsEagerly(UUID postId) {
        Post post = postRepo.findByIdAndFetchTagsAndImagesEagerly(postId)
                .orElseThrow(() -> new OnLineException("Post not found, postId: " + postId, ErrorCause.POST_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        post.getUser().setImages(postRepo.findUserImagesByPost(postId).orElse(null));
        return postMapper.toUserPostDto(post);
    }

    public void deletePost(User user, UUID postId) {
        Post post = getPostAndCheckUserAccess(user, postId);
        postRepo.delete(post);
    }

    private Set<Tag> getTags(RQPostDto dto, Post post) {
        String tags = dto.getTags();
        if (StringUtils.isAllBlank(tags)) {
            return Collections.emptySet();
        }
        return Arrays.stream(tags.split("#"))
                .filter(text -> !text.isEmpty())
                .map(tagName -> tagService.createPostTag(post, tagName))
                .collect(Collectors.toSet());
    }

    private Post getPostAndCheckUserAccess(User user, UUID postId) {
        Post post = getById(postId);
        if (!post.getUser().equals(user)) {
            throw new OnLineException("Post editing permission denied, userId: " + user.getId(),
                    ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
        return post;
    }

    private Set<Img> getImages(RQPostDto dto, Post post) {
        if (dto.getImages() == null) {
            return Collections.emptySet();
        }
        return dto.getImages()
                .stream()
                .map(imgService::getBytes)
                .map(bytes -> (Img) Img.builder()
                        .post(post)
                        .img(bytes)
                        .build())
                .collect(Collectors.toSet());
    }

    public BasePostDto toDto(Post post) {
        return postMapper.toBasePostDto(post);
    }
}
