package com.chirko.onLine.service;

import com.chirko.onLine.dto.mapper.CommentMapper;
import com.chirko.onLine.dto.request.RequestCommentDto;
import com.chirko.onLine.dto.response.CommentDto;
import com.chirko.onLine.entity.Comment;
import com.chirko.onLine.entity.Img;
import com.chirko.onLine.entity.Post;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.exception.ErrorCause;
import com.chirko.onLine.exception.OnLineException;
import com.chirko.onLine.repo.CommentRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepo commentRepo;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final PostService postService;
    private final ImgService imgService;

    public void addPostComment(UUID postId, String email, RequestCommentDto dto) {
        Comment comment = buildAndGetComment(email, dto);
        Post post = postService.findPostById(postId);
        comment.setPost(post);
        commentRepo.save(comment);
    }

    public void addImgComment(UUID imageId, String email, RequestCommentDto dto) {
        Comment comment = buildAndGetComment(email, dto);
        Img img = imgService.findImgById(imageId);
        comment.setImg(img);
        commentRepo.save(comment);
    }

    public Set<CommentDto> getPostComments(UUID postId) {
        Post post = postService.findPostByIdAndFetchCommentsEagerly(postId);
        post.setComments(post.getComments()
                .stream()
                .map(comment -> commentRepo.findByIdFetchUserImages(comment.getId()))
                .collect(Collectors.toSet()));
        return commentMapper.commentsToCommentsDto(post.getComments());
    }

    public Set<CommentDto> getImgComments(UUID imgId) {
        Img img = imgService.findImgByIdAndFetchCommentsEagerly(imgId);
        img.setComments(img.getComments()
                .stream()
                .map(comment -> commentRepo.findByIdFetchUserImages(comment.getId()))
                .collect(Collectors.toSet()));
        return commentMapper.commentsToCommentsDto(img.getComments());
    }

    @Transactional
    public void updateComment(UUID commentId, String email, RequestCommentDto dto) {
        Comment comment = getComment(commentId);
        checkUserAccess(comment, email);
        comment.setText(dto.getText());
    }

    public void deleteComment(UUID commentId, String email) {
        Comment comment = getComment(commentId);
        User user = comment.getPost().getUser();
        if (!user.getEmail().equals(email)) {
            checkUserAccess(comment, email);
        }
        commentRepo.delete(comment);
    }

    private void checkUserAccess(Comment comment, String email) {
        User user = comment.getUser();
        if (!user.getEmail().equals(email)) {
            throw new OnLineException("Comment updating permission denied, userId: " + user.getId(),
                    ErrorCause.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    private Comment getComment(UUID commentId) {
        return commentRepo.findById(commentId).orElseThrow(() -> new OnLineException(
                "Comment not found, commentId: " + commentId, ErrorCause.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private Comment buildAndGetComment(String email, RequestCommentDto dto) {
        User user = userService.findUserByEmail(email);
        return Comment.builder()
                .user(user)
                .text(dto.getText())
                .build();
    }
}
