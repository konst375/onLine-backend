package com.chirko.onLine.services;

import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.entities.enums.Owner;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static com.chirko.onLine.entities.enums.InterestPoints.FRIENDSHIP_POINTS;


@Service
@AllArgsConstructor
public class FeedService {
    private final UserService userService;
    private final PostService postService;
    private final TagScoresService tagScoresService;
    private final ActivityService activityService;
    private final FriendshipService friendshipService;

    public Set<BasePostDto> getSubscriptionFeed(UUID userId) {
        User user = userService.findByIdWithInterestIndicators(userId);
        Date startPoint = activityService.getStartDateForUser(userId);
        // find all posts
        Set<Post> communitiesPosts = postService.getCommunityPosts(user.getCommunities(), startPoint);
        Set<Post> friendsPost = postService.getFriendsPosts(friendshipService.getFriends(user), startPoint);
        // delete viewed posts and merge communities and friends posts
        Sets.SetView<Post> posts = Sets.union(communitiesPosts, friendsPost);
        HashSet<Post> reducedPostsSet = Sets.newHashSet(Sets.difference(posts, user.getViewedPosts()));
        // order by scores
        Map<Tag, Integer> ratedTagsMap = tagScoresService.rateTags(user);
        Set<Post> ratedPosts = ratePosts(reducedPostsSet, ratedTagsMap, user);
        return postService.toBasePostsDto(ratedPosts);
    }

    public Set<BasePostDto> getRecommendationFeed(UUID userId) {
        User user = userService.findByIdWithInterestIndicators(userId);
        Date startPoint = activityService.getStartDateForUser(userId);
        // find all posts
        Map<Tag, Integer> ratedTagsMap = tagScoresService.rateTags(user);
        Set<Post> recommendations = postService.findRecommendations(ratedTagsMap.keySet(), startPoint);
        // delete viewed posts
        Set<Post> reducedPostsSet = Sets.difference(recommendations, user.getViewedPosts()).stream()
                .filter(post -> !friendshipService.getFriends(user).contains(post.getUser()))
                .filter(post -> !user.getCommunities().contains(post.getCommunity()))
                .collect(Collectors.toSet());
        // delete ...
        // order by scores
        Set<Post> ratedPosts = ratePosts(reducedPostsSet, ratedTagsMap, user);
        return postService.toBasePostsDto(ratedPosts);
    }

    private Set<Post> ratePosts(Set<Post> posts, Map<Tag, Integer> ratedTags, User user) {
        posts.forEach(post -> post.getTags().forEach(tag -> {
            int scores = post.getScores();
            Integer tagScore = ratedTags.get(tag);
            if (!ratedTags.isEmpty() && tagScore != null) {
                scores += tagScore;
            }
            if (post.getOwner() == Owner.USER && friendshipService.getFriends(user).contains(post.getUser())) {
                scores += FRIENDSHIP_POINTS.getPoints();
            }
            post.setScores(scores);
        }));
        return posts.stream()
                .sorted(Comparator.comparingInt(Post::getScores).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
