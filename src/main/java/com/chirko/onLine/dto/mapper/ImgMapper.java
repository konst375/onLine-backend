package com.chirko.onLine.dto.mapper;

import com.chirko.onLine.dto.response.ImgDto;
import com.chirko.onLine.entity.Img;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ImgMapper {
    ImgDto imgToImgDto(Img entity);

    List<ImgDto> imagesToImagesDto(List<Img> images);
}
