package com.chirko.onLine.dto.response.community;

import com.chirko.onLine.dto.response.TagDto;
import com.chirko.onLine.dto.response.img.BaseImgDto;

import java.util.Set;

public record CommunityWithNumberOfFollowersDto(
        String id,
        String name,
        String subject,
        BaseImgDto avatar,
        Set<TagDto> tags,
        long numberOfFollowers) {
}
