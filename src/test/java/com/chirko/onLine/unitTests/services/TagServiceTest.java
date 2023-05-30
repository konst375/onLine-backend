package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.dto.request.RQPostDto;
import com.chirko.onLine.dto.request.community.RQRegisterCommunityDto;
import com.chirko.onLine.entities.Community;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.repos.TagRepo;
import com.chirko.onLine.services.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock
    private TagRepo tagRepo;
    @InjectMocks
    private TagService tagService;

    @Test
    void ifCreatePostTagThatNotYetExists() {
        // given
        UUID postId = UUID.randomUUID();
        Post expectedPost = Post.builder().id(postId).build();
        String textPostText = "This is the text for #Test post.";
        String tagName = "#Test";
        when(tagRepo.notExistsByTagName(tagName)).thenReturn(true);
        RQPostDto rqPostDto = new RQPostDto(textPostText, Collections.emptySet());
        Set<String> expectedTags = Set.of(tagName);
        when(tagRepo.save(any(Tag.class)))
                .thenReturn(Tag.builder()
                        .posts(Set.of(expectedPost))
                        .tagName(tagName)
                        .build());
        // when
        Set<Tag> actualTags = tagService.getPostTags(expectedPost, rqPostDto);
        // then
        // test that created tag has only one post
        Optional<Tag> optionalTag = actualTags.stream().findFirst();
        assertTrue(optionalTag.isPresent());
        Set<Post> actualTagPosts = optionalTag.get().getPosts();
        assertEquals(1, actualTagPosts.size());
        assertTrue(actualTagPosts.contains(expectedPost));
        // test that tag found correct
        Set<String> actualTagNames = actualTags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toSet());
        assertEquals(expectedTags, actualTagNames);
    }

    @Test
    void ifCreatePostTagThatAlreadyExists() {
        // given
        UUID postId = UUID.randomUUID();
        Post expectedPost = Post.builder().id(postId).build();
        String textPostText = "This is the text for #Test post.";
        String tagName = "#Test";
        when(tagRepo.notExistsByTagName(tagName)).thenReturn(false);
        RQPostDto rqPostDto = new RQPostDto(textPostText, Collections.emptySet());
        Set<String> expectedTags = Set.of(tagName);
        Set<Post> postSet = new HashSet<>();
        postSet.add(expectedPost);
        Tag existedTag = Tag.builder()
                .posts(postSet)
                .tagName(tagName)
                .build();
        when(tagRepo.findByTagNameAndFetchPostsEagerly(tagName))
                .thenReturn(Optional.of(existedTag));
        // when
        Set<Tag> actualTags = tagService.getPostTags(expectedPost, rqPostDto);
        // then
        // test that created tag has only one post
        Optional<Tag> optionalTag = actualTags.stream().findFirst();
        assertTrue(optionalTag.isPresent());
        Set<Post> actualTagPosts = optionalTag.get().getPosts();
        assertEquals(1, actualTagPosts.size());
        assertTrue(actualTagPosts.contains(expectedPost));
        // test that tag found correct
        Set<String> actualTagNames = actualTags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toSet());
        assertEquals(expectedTags, actualTagNames);
    }

    @Test
    void ifCreatePostWithoutText() {
        // given
        // when
        Set<Tag> actualTags = tagService.getPostTags(
                Post.builder().build(),
                new RQPostDto(null, null));
        // then
        assertEquals(Collections.emptySet(), actualTags);
    }

    @Test
    void ifCreateCommunityTagThatNotYetExists() {
        // given
        UUID communityId = UUID.randomUUID();
        Community expectedCommunity = Community.builder().id(communityId).build();
        String communityTags = "#Test";
        String tagName = "#Test";
        when(tagRepo.notExistsByTagName(tagName)).thenReturn(true);
        RQRegisterCommunityDto rqRegisterCommunityDto =
                new RQRegisterCommunityDto(null, null, null, communityTags);
        Set<String> expectedTags = Set.of(tagName);
        when(tagRepo.save(any(Tag.class)))
                .thenReturn(Tag.builder()
                        .communities(Set.of(expectedCommunity))
                        .tagName(tagName)
                        .build());
        // when
        Set<Tag> actualTags = tagService.getCommunityTags(expectedCommunity, rqRegisterCommunityDto);
        // then
        // test that created tag has only one post
        Optional<Tag> optionalTag = actualTags.stream().findFirst();
        assertTrue(optionalTag.isPresent());
        Set<Community> actualTagCommunities = optionalTag.get().getCommunities();
        assertEquals(1, actualTagCommunities.size());
        assertTrue(actualTagCommunities.contains(expectedCommunity));
        // test that tag found correct
        Set<String> actualTagNames = actualTags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toSet());
        assertEquals(expectedTags, actualTagNames);
    }

    @Test
    void ifCreateCommunityTagThatAlreadyExists() {
        // given
        UUID communityId = UUID.randomUUID();
        Community expectedCommunity = Community.builder().id(communityId).build();
        String communityTags = "#Test";
        String tagName = "#Test";
        when(tagRepo.notExistsByTagName(tagName)).thenReturn(false);
        RQRegisterCommunityDto rqRegisterCommunityDto =
                new RQRegisterCommunityDto(null, null, null, communityTags);
        Set<String> expectedTags = Set.of(tagName);
        Set<Community> communitiesSet = new HashSet<>();
        communitiesSet.add(expectedCommunity);
        Tag existedTag = Tag.builder()
                .communities(communitiesSet)
                .tagName(tagName)
                .build();
        when(tagRepo.findByTagNameAndFetchCommunitiesEagerly(tagName)).thenReturn(Optional.of(existedTag));
        // when
        Set<Tag> actualTags = tagService.getCommunityTags(expectedCommunity, rqRegisterCommunityDto);
        // then
        // test that created tag has only one post
        Optional<Tag> optionalTag = actualTags.stream().findFirst();
        assertTrue(optionalTag.isPresent());
        Set<Community> actualTagCommunities = optionalTag.get().getCommunities();
        assertEquals(1, actualTagCommunities.size());
        assertTrue(actualTagCommunities.contains(expectedCommunity));
        // test that tag found correct
        Set<String> actualTagNames = actualTags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toSet());
        assertEquals(expectedTags, actualTagNames);
    }

    @Test
    void ifCreateCommunityWithoutTags() {
        // given
        // when
        Set<Tag> actualTags = tagService.getCommunityTags(
                Community.builder().build(),
                new RQRegisterCommunityDto(null, null, null, null));
        // then
        assertEquals(Collections.emptySet(), actualTags);
    }
}