package com.chirko.onLine.services;

import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.entities.TagScores;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.InterestPoints;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.TagScoresRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.chirko.onLine.entities.enums.InterestPoints.*;

@Service
@AllArgsConstructor
public class TagScoresService {
    private final TagScoresRepo tagScoresRepo;

    public Map<Tag, Integer> rateTags(User user) {
        List<TagScores> tagScoresList = tagScoresRepo.findAllByUserId(user.getId()).orElseThrow(
                () -> new OnLineException(ErrorCause.TAG_SCORES_NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR));
        return tagScoresList.stream()
                .collect(Collectors.toMap(TagScores::getTag, TagScores::getScores, Integer::sum));
    }

    public void writeDownThatPostLiked(User user, Post post) {
        List<TagScores> tagScores = post.getTags().stream()
                .map(tag -> buildTagScores(user, tag, LIKE_POINTS))
                .collect(Collectors.toList());
        tagScoresRepo.saveAll(tagScores);
    }

    public void writeDownThatPostCommented(User user, Post post) {
        List<TagScores> tagScores = post.getTags().stream()
                .map(tag -> buildTagScores(user, tag, COMMENT_POINTS))
                .collect(Collectors.toList());
        tagScoresRepo.saveAll(tagScores);
    }

    public void writeDownThatPostShared(User user, Post post) {
        List<TagScores> tagScores = post.getTags().stream()
                .map(tag -> buildTagScores(user, tag, SHARE_POINTS))
                .collect(Collectors.toList());
        tagScoresRepo.saveAll(tagScores);
    }

    public void writeDownThatPostUnliked(User user, Post post) {
        List<TagScores> tagScores = post.getTags().stream()
                .map(tag -> buildTagScores(user, tag, UNLIKE_POINTS))
                .collect(Collectors.toList());
        tagScoresRepo.saveAll(tagScores);
    }

    private TagScores buildTagScores(User user, Tag tag, InterestPoints interestPoints) {
        return TagScores.builder()
                .user(user)
                .tag(tag)
                .scores(interestPoints.getPoints())
                .build();
    }
}
