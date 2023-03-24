package com.chirko.onLine.dto.mapper;

import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.entity.Img;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ImgMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "img", source = "entity.img")
    @Mapping(target = "createdDate", source = "entity.createdDate")
    ImgDto imgToImgDto(Img entity);

    List<ImgDto> imagesListToImagesDtoList(List<Img> imagesList);
}
