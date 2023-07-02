package com.chirko.onLine.dto.response.community;

import com.chirko.onLine.dto.response.img.BaseImgDto;
import com.chirko.onLine.dto.response.post.BasePostDto;

import java.util.List;
import java.util.Set;

public record CommunityPageDto(
        BaseCommunityDto community,
        BaseImgDto cover,
        List<BaseImgDto> images,
        Set<BasePostDto> posts) {
}
