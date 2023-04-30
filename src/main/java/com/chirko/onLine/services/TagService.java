package com.chirko.onLine.services;

import com.chirko.onLine.entities.Community;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.TagRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class TagService {
    private final TagRepo tagRepo;

    @Transactional
    public Tag createPostTag(Post post, String tagName) {
        Tag tag;
        if (tagRepo.notExistsByTagName("#" + tagName)) {
            tag = Tag.builder()
                    .posts(Set.of(post))
                    .tagName("#" + tagName)
                    .build();
            tagRepo.save(tag);
        } else {
            tag = tagRepo.findByTagNameAndFetchPostsEagerly("#" + tagName)
                    .orElseThrow(() -> new OnLineException(ErrorCause.TAG_NOT_FOUND, HttpStatus.NOT_FOUND));
            if (tag.getPosts().contains(post)) { // post may contain tag if this method used at update post scenario
                tag.getPosts().add(post);
            }
        }
        return tag;
    }

    @Transactional
    public Tag createCommunityTag(Community community, String tagName) {
        Tag tag;
        if (tagRepo.notExistsByTagName("#" + tagName)) {
            tag = Tag.builder()
                    .communities(Set.of(community))
                    .tagName("#" + tagName)
                    .build();
            tagRepo.save(tag);
        } else {
            tag = tagRepo.findByTagNameAndFetchCommunitiesEagerly("#" + tagName)
                    .orElseThrow(() -> new OnLineException(ErrorCause.TAG_NOT_FOUND, HttpStatus.NOT_FOUND));
            if (tag.getCommunities().contains(community)) { // community may contain tag if this method used at update post scenario
                tag.getCommunities().add(community);
            }
        }
        return tag;
    }
}
