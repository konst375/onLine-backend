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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class PostService {
    private final TagService tagService;
    private final ImgService imgService;
    private final CommunityService communityService;
    private final PostMapper postMapper;
    private final PostRepo postRepo;

    public UserPostDto createUserPost(User user, RQPostDto dto) {
        Post post = buildPost(dto);
        if (post.getImages() != null) {
            post.getImages().forEach(img -> img.setPost(post));
        }
        user.setImages(imgService.findUserImages(user));
        post.setUser(user);
        return postMapper.toUserPostDto(postRepo.save(post));
    }

    public CommunityPostDto createCommunityPost(UUID communityId, RQPostDto dto) {
        Post post = buildPost(dto);
        if (post.getImages() != null) {
            post.getImages().forEach(img -> img.setPost(post));
        }
        Community community = communityService.getCommunity(communityId);
        post.setCommunity(community);
        return postMapper.toCommunityPostDto(postRepo.save(post));
    }

    public Post getById(UUID postId) {
        return postRepo.findById(postId)
                .orElseThrow(() -> new OnLineException(
                        "Post not found, postId: " + postId,
                        ErrorCause.POST_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    public Post findPostWithAllDependencies(UUID postId) {
        Post post = postRepo.findByIdWithAllDependencies(postId)
                .orElseThrow(() -> new OnLineException(
                        "Post not found, postId: " + postId,
                        ErrorCause.POST_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
        post.getLikes().forEach(like -> {
            User user = like.getUser();
            user.setImages(imgService.findUserImages(user));
        });
        post.setImages(Lists.newArrayList(Sets.newLinkedHashSet(post.getImages())));
        return post;
    }

    public Set<Post> findPostsWithAllDependencies(Set<UUID> postsIds) {
        return postRepo.findAllByIdsWithAllDependencies(postsIds).orElseThrow(() -> new OnLineException(
                ErrorCause.POST_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    public BasePostDto getBasePostDtoById(UUID postId) {
        return postMapper.toBasePostDto(findPostWithAllDependencies(postId));
    }

    public void deletePost(User user, UUID postId) {
        Post post = getById(postId);
        checkUserAccess(user, post);
        postRepo.delete(post);
    }

    public BasePostDto toBasePostDto(Post post) {
        return postMapper.toBasePostDto(post);
    }

    public Set<BasePostDto> toBasePostsDto(Set<Post> posts) {
        return postMapper.toBasePostsDto(posts);
    }

    public Set<BasePostDto> getBasePostsDtoForUser(User user) {
        return postMapper.toBasePostsDto(postRepo.findAllByAdminWithTagsImagesAndLikes(user)
                .orElseGet(Collections::emptySet));
    }

    @Transactional
    public BasePostDto updatePost(UUID postId, User user, RQPostDto dto) {
        Post post = findPostWithAllDependencies(postId);
        checkUserAccess(user, post);
        post.setText(dto.getText());
        post.setTags(tagService.createTags(dto.getText()));
        List<Img> oldImages = post.getImages();
        post.setImages(Optional.ofNullable(imgService.createImages(dto.getImages()))
                .orElseGet(Collections::emptyList).stream()
                .map(newImg -> oldImages.stream()
                        .filter(oldImg -> Arrays.equals(oldImg.getImg(), newImg.getImg()))
                        .findFirst()
                        .orElse(newImg))
                .peek(img -> img.setPost(post))
                .toList());
        imgService.deleteImages(oldImages.stream()
                .filter(oldImg -> !post.getImages().contains(oldImg))
                .toList());
        return postMapper.toBasePostDto(post);
    }

    private Post buildPost(RQPostDto dto) {
        return Post.builder()
                .text(dto.getText())
                .tags(tagService.createTags(dto.getText()))
                .images(imgService.createImages(dto.getImages()))
                .likes(new HashSet<>())
                .comments(new HashSet<>())
                .build();
    }

    private void checkUserAccess(User user, Post post) {
        OnLineException exception = new OnLineException(
                "Post editing permission denied, userId: " + user.getId(),
                ErrorCause.ACCESS_DENIED,
                HttpStatus.FORBIDDEN);
        switch (post.getOwner()) {
            case COMMUNITY -> {
                if (!post.getCommunity().getAdmin().equals(user)
                        && !communityService.getModerators(post.getCommunity().getId()).contains(user)) {
                    throw exception;
                }
            }
            case USER -> {
                if (!post.getUser().equals(user)) {
                    throw exception;
                }
            }
        }
    }

    public Set<Post> getCommunityPosts(Set<Community> communities, Date startPoint) {
        return postRepo.findCommunitiesPostsAfterStartDate(communities, startPoint)
                .orElseThrow(() -> new OnLineException(
                        "Communities posts not found",
                        ErrorCause.POST_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    public Set<Post> getFriendsPosts(Set<User> friends, Date startPoint) {
        return postRepo.findUsersPostsAfterStartDate(friends, startPoint).orElseThrow(() -> new OnLineException(
                "Users posts not found",
                ErrorCause.POST_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    public Set<Post> findRecommendations(Set<Tag> tags, Date startPoint) {
        return postRepo.findRecommendations(tags, startPoint)
                .orElseThrow(() -> new OnLineException(
                        "Tags posts not found",
                        ErrorCause.POST_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    public Optional<Post> getByIdWithTags(UUID id) {
        if (postRepo.existsById(id)) {
            return Optional.of(postRepo.findByIdWithTags(id).orElseThrow(() -> new OnLineException(
                    "Post not found, postId: " + id,
                    ErrorCause.POST_NOT_FOUND,
                    HttpStatus.NOT_FOUND)));
        }
        return Optional.empty();
    }
}
