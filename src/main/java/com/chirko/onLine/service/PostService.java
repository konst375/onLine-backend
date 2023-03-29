package com.chirko.onLine.service;

import com.chirko.onLine.dto.mapper.PostMapper;
import com.chirko.onLine.dto.request.UserPostDto;
import com.chirko.onLine.dto.response.PostDto;
import com.chirko.onLine.entity.Img;
import com.chirko.onLine.entity.Post;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.exception.ErrorCause;
import com.chirko.onLine.exception.OnLineException;
import com.chirko.onLine.repo.PostRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepo postRepo;
    private final UserService userService;
    private final PostUtilsService postUtilsService;
    private final PostMapper postMapper;

    public void createUserPost(String email, UserPostDto userPostDto) {
        Post createdPost = new Post();
        createdPost.setUser(userService.findUserByEmail(email));
        createdPost.setText(userPostDto.getText());

        if (userPostDto.getImages() != null) {
            createdPost.setImages(getImagesFromMultipartFileLis(userPostDto, createdPost));
        }

        postRepo.save(createdPost);
    }

    public PostDto findPost(UUID postId) {
        Post foundPost = postUtilsService.findPostByIdAndFetchImagesEagerly(postId);
        User user = userService.findUserAndFetchImagesEagerlyByPost(postId);
        foundPost.setUser(user);
        return postMapper.postToPostDto(foundPost);
    }

    public void deletePost(String email, UUID postId) {
        Post post = getPostAndCheckUserAccess(email, postId);
        postRepo.delete(post);
    }

    @Transactional
    public void updatePost(String email, UUID postId, UserPostDto userPostDto) {
        Post post = getPostAndCheckUserAccess(email, postId);
        post.setText(userPostDto.getText());
        List<Img> dtoImages = getImagesFromMultipartFileLis(userPostDto, post);
        dtoImages.removeAll(post.getImages());
        post.getImages().addAll(dtoImages);
    }

    private Post getPostAndCheckUserAccess(String email, UUID postId) {
        Post foundPost = postRepo.findById(postId)
                .orElseThrow(() -> new OnLineException("Post not found, postId: " + postId.toString(),
                        ErrorCause.POST_NOT_FOUND, HttpStatus.NOT_FOUND));
        User user = foundPost.getUser();
        if (!user.getEmail().equals(email)) {
            throw new OnLineException("Post editing permission denied, userId: " + user.getId().toString(),
                    ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
        return foundPost;
    }

    private List<Img> getImagesFromMultipartFileLis(UserPostDto userPostDto, Post post) {
        return userPostDto.getImages()
                .stream()
                .map(file -> {
                    try {
                        return file.getBytes();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                })
                .map(bytes -> (Img) Img.builder()
                        .post(post)
                        .img(bytes)
                        .build())
                .collect(Collectors.toList());
    }
}
