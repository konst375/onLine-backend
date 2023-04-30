package com.chirko.onLine.dto.response.post;

import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.TagDto;

import java.sql.Timestamp;
import java.util.Set;

public record BasePostDto(String id, String text, Set<ImgDto> images, Set<TagDto> tags, Timestamp modifiedDate) {
}
