package com.chirko.onLine.dto.response.community;

import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.TagDto;

import java.util.Set;

public record CommunityWithNumberOfFollowersDto(
        String id,
        String name,
        String subject,
        ImgDto avatar,
        Set<TagDto> tags,
        long numberOfFollowers) {
}
