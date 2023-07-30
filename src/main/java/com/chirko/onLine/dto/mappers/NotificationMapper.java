package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.NotificationDto;
import com.chirko.onLine.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface NotificationMapper {
    @Mapping(target = "isViewed", source = "viewed")
    NotificationDto toDto(Notification entity);

    Set<NotificationDto> toDtoSet(Set<Notification> entities);
}
