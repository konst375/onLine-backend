package com.chirko.onLine.dto.response.community;

import com.chirko.onLine.dto.response.ImgDto;

public record BaseCommunityDto(String id, String name, String subject, ImgDto avatar) {
}
