package com.chirko.onLine.service;

import com.chirko.onLine.entity.Post;
import com.chirko.onLine.exception.ErrorCause;
import com.chirko.onLine.exception.OnLineException;
import com.chirko.onLine.repo.PostRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class PostUtilsService {
    private final PostRepo postRepo;

    Post findPostByIdAndFetchImagesEagerly(UUID postId) {
        return postRepo.findByIdAndFetchImagesEagerly(postId)
                .orElseThrow(() -> new OnLineException("Post not found, postId: " + postId.toString(),
                        ErrorCause.POST_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
