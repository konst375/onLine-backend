package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.community.BaseCommunityDto;
import com.chirko.onLine.dto.response.community.CommunityPageDto;
import com.chirko.onLine.dto.response.community.CommunityWithNumberOfFollowersDto;
import com.chirko.onLine.dto.response.post.CommunityPostDto;
import com.chirko.onLine.entities.Community;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, imports = {ImgMapper.class, UserMapper.class, TagMapper.class})
public interface CommunityMapper {
    BaseCommunityDto toBaseDto(Community entity);

    @Mapping(target = "numberOfFollowers", expression = "java(entity.getFollowers().size())")
    CommunityWithNumberOfFollowersDto toCommunityWithNumberOfFollowersDto(Community entity);

    @Mapping(target = "posts", source = "posts")
    @Mapping(target = "community", source = "entity")
    CommunityPageDto toCommunityPageDto(Community entity, Set<CommunityPostDto> posts);
}
