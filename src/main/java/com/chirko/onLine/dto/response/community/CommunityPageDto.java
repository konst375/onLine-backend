package com.chirko.onLine.dto.response.community;

import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.dto.response.TagDto;
import com.chirko.onLine.dto.response.post.CommunityPostDto;

import java.util.Set;

public record CommunityPageDto(BaseCommunityDto community, ImgDto cover, Set<ImgDto> images, Set<CommunityPostDto> posts, Set<TagDto> tags) {
}
