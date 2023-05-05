package com.chirko.onLine.services;

import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.entities.*;
import com.chirko.onLine.repos.LikeRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LikeService {
    private final CommentService commentService;
    private final ImgService imgService;
    private final PostService postService;
    private final LikeRepo likeRepo;

    public CommentDto likeComment(UUID commentId, User user) {
        Like like = buildBaseLike(user);
        Comment comment = commentService.getComment(commentId);
        like.setComment(comment);
        likeRepo.save(like);
        return commentService.toDto(comment);
    }

    public ImgDto likeImg(UUID imgId, User user) {
        Like like = buildBaseLike(user);
        Img img = imgService.getById(imgId);
        like.setImg(img);
        likeRepo.save(like);
        return imgService.toDto(img);
    }

    public BasePostDto likePost(UUID postId, User user) {
        Like like = buildBaseLike(user);
        Post post = postService.getById(postId);
        like.setPost(post);
        likeRepo.save(like);
        return postService.toDto(post);
    }

    @Transactional
    public void deleteParentId(UUID id) {
        likeRepo.deleteByParentId(id);
    }

    private Like buildBaseLike(User user) {
        return Like.builder()
                .user(user)
                .build();
    }
}
