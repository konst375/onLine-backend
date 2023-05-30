package com.chirko.onLine.services;

import com.chirko.onLine.dto.request.RQPostDto;
import com.chirko.onLine.dto.request.community.RQRegisterCommunityDto;
import com.chirko.onLine.entities.Community;
import com.chirko.onLine.entities.Post;
import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.TagRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TagService {
    private static final Pattern TAG_PATTERN = Pattern.compile("\\B(\\#[a-zA-Z]+\\b)(?!;)");
    private final TagRepo tagRepo;

    @Transactional
    public Set<Tag> getPostTags(Post post, RQPostDto dto) {
        return findTags(dto.getText())
                .stream()
                .map(tagName -> {
                    if (tagRepo.notExistsByTagName(tagName)) {
                        return tagRepo.save(Tag.builder()
                                .posts(Set.of(post))
                                .tagName(tagName)
                                .build());
                    } else {
                        Tag tag = tagRepo.findByTagNameAndFetchPostsEagerly(tagName)
                                .orElseThrow(() -> new OnLineException(ErrorCause.TAG_NOT_FOUND, HttpStatus.NOT_FOUND));
                        tag.getPosts().add(post);
                        return tag;
                    }
                })
                .collect(Collectors.toSet());
    }

    @Transactional
    public Set<Tag> getCommunityTags(Community community, RQRegisterCommunityDto dto) {
        return findTags(dto.getTags())
                .stream()
                .map(tagName -> {
                    if (tagRepo.notExistsByTagName(tagName)) {
                        return tagRepo.save(Tag.builder()
                                .communities(Set.of(community))
                                .tagName(tagName)
                                .build());
                    } else {
                        Tag tag = tagRepo.findByTagNameAndFetchCommunitiesEagerly(tagName)
                                .orElseThrow(() -> new OnLineException(ErrorCause.TAG_NOT_FOUND, HttpStatus.NOT_FOUND));
                        tag.getCommunities().add(community);
                        return tag;
                    }
                })
                .collect(Collectors.toSet());
    }

    private Set<String> findTags(CharSequence requestCharSequence) {
        if (StringUtils.isAllBlank(requestCharSequence)) {
            return Collections.emptySet();
        }
        Matcher matcher = TAG_PATTERN.matcher(requestCharSequence);
        return matcher.results()
                .map(MatchResult::group)
                .collect(Collectors.toSet());
    }
}
