package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.img.BaseImgDto;
import com.chirko.onLine.dto.response.img.FullImgDto;
import com.chirko.onLine.entities.Img;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = CommentMapper.class)
public interface ImgMapper {
    BaseImgDto toDto(Img entity);

    Set<BaseImgDto> imagesToImagesDto(Set<Img> images);

    List<BaseImgDto> imagesToImagesDto(List<Img> images);

    @Mapping(target = "baseImgDto", source = "entity")
    @Mapping(target = "likes", expression = "java(entity.getLikes().size())")
    @Mapping(target = "commentsAmount", expression = "java(entity.getComments().size())")
    FullImgDto toFullDto(Img entity);
}
