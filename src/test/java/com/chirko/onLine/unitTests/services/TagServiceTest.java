package com.chirko.onLine.unitTests.services;

import com.chirko.onLine.entities.Tag;
import com.chirko.onLine.repos.TagRepo;
import com.chirko.onLine.services.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {
    @Mock
    private TagRepo tagRepo;
    @InjectMocks
    private TagService tagService;

    @Test
    void createTag() {
        // given
        String tagText = "this is a test text for createTags method with #Tags #Tag #Testing tags";
        Set<String> expectedTagNames = Set.of("#Tags", "#Tag", "#Testing");
        // mocked tagRepo set up
        when(tagRepo.findAll()).thenReturn(new ArrayList<>());
        when(tagRepo.save(any(Tag.class))).thenAnswer(invocation -> {
            Tag tag = (Tag) invocation.getArguments()[0];
            tag.setId(UUID.randomUUID());
            return tag;
        });
        // when
        Set<Tag> actualTags = tagService.createTags(tagText);
        // then
        Set<String> setActualTagNames = actualTags.stream().map(Tag::getTagName).collect(Collectors.toSet());
        assertEquals(expectedTagNames, setActualTagNames);
    }

    @Test
    void ifCreateTagsAndTextIsBlank() {
        // given
        // when
        // then
        assertEquals(Collections.emptySet(), tagService.createTags(""));
    }
}