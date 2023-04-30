package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.entities.Img;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ImgMapper {
    ImgDto toDto(Img entity);

    Set<ImgDto> imagesToImagesDto(Set<Img> images);
}
