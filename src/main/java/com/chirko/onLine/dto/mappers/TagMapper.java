package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.TagDto;
import com.chirko.onLine.entities.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TagMapper {
    TagDto toDto(Tag entity);
    Set<TagDto> tagsToTagsDto(Set<Tag> images);
}
