package com.chirko.onLine.services;

import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.dto.response.img.FullImgDto;
import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.entities.*;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.LikeRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LikeService {
    private final CommentService commentService;
    private final ImgService imgService;
    private final PostService postService;
    private final TagScoresService tagScoresService;
    private final LikeRepo likeRepo;

    public CommentDto likeComment(UUID commentId, User user) {
        if (likeRepo.existsByUserIdAndCommentId(user.getId(), commentId)) {
            throw new OnLineException(ErrorCause.ALREADY_LIKED, HttpStatus.BAD_REQUEST);
        }
        user.setImages(imgService.findUserImages(user));
        Like like = buildBaseLike(user);
        Comment comment = commentService.getCommentWithUserImagesAndLikes(commentId);
        like.setComment(comment);
        comment.getLikes().add(like);
        likeRepo.save(like);
        return commentService.toDto(comment);
    }

    public FullImgDto likeImg(UUID imgId, User user) {
        if (likeRepo.existsByUserIdAndImgId(user.getId(), imgId)) {
            throw new OnLineException(ErrorCause.ALREADY_LIKED, HttpStatus.BAD_REQUEST);
        }
        user.setImages(imgService.findUserImages(user));
        Like like = buildBaseLike(user);
        Img img = imgService.getFullImgById(imgId);
        img.getLikes().forEach(l -> {
                            User u = l.getUser();
                            u.setImages(imgService.findUserImages(u));
        });
        like.setImg(img);
        img.getLikes().add(like);
        likeRepo.save(like);
        return imgService.toFullImgDto(img);
    }

    public BasePostDto likePost(UUID postId, User user) {
        if (likeRepo.existsByUserIdAndPostId(user.getId(), postId)) {
            throw new OnLineException(ErrorCause.ALREADY_LIKED, HttpStatus.BAD_REQUEST);
        }
        user.setImages(imgService.findUserImages(user));
        Like like = buildBaseLike(user);
        Post post = postService.findPostWithAllDependencies(postId);
        like.setPost(post);
        post.getLikes().add(like);
        likeRepo.save(like);
        tagScoresService.writeDownThatPostLiked(user, post);
        return postService.toBasePostDto(post);
    }

    @Transactional
    public void unlike(UUID id, User user) {
        postService.getByIdWithTags(id).ifPresent(post -> tagScoresService.writeDownThatPostUnliked(user, post));
        likeRepo.deleteByUserIdAndParentId(id, user.getId());
    }

    private Like buildBaseLike(User user) {
        return Like.builder()
                .user(user)
                .build();
    }
}
