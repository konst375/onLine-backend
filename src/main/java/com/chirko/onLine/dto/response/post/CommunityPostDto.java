package com.chirko.onLine.dto.response.post;

import com.chirko.onLine.dto.response.community.BaseCommunityDto;

public record CommunityPostDto(BaseCommunityDto community, BasePostDto post) {
}
