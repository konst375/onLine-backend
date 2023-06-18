package com.chirko.onLine.services;

import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.repos.TagRepo;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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

    public Set<Tag> createTags(String text) {
        List<Tag> tags = Lists.newArrayList(tagRepo.findAll());
        return findTags(text).stream()
                .map(tagName -> tags.stream()
                            .filter(tag -> tag.getTagName().equals(tagName))
                            .findFirst()
                            .orElseGet(() -> tagRepo.save(Tag.builder().tagName(tagName).build())))
                .collect(Collectors.toSet());
    }

    private Set<String> findTags(String text) {
        if (StringUtils.isAllBlank(text)) {
            return Collections.emptySet();
        }
        Matcher matcher = TAG_PATTERN.matcher(text);
        return matcher.results()
                .map(MatchResult::group)
                .collect(Collectors.toSet());
    }
}
