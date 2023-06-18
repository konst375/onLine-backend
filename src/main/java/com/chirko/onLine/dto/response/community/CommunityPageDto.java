package com.chirko.onLine.dto.response.community;

import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.post.BasePostDto;

import java.util.List;
import java.util.Set;

public record CommunityPageDto(
        BaseCommunityDto community,
        ImgDto cover,
        List<ImgDto> images,
        Set<BasePostDto> posts) {
}
