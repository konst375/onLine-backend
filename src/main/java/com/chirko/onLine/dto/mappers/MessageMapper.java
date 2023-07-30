package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.communication.MessageDto;
import com.chirko.onLine.entities.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = PostMapper.class,
        uses = PostMapper.class)
public interface MessageMapper {
    @Mapping(target = "isViewed", source = "viewed")
    MessageDto toMessageDto(Message entity);

    Set<MessageDto> toMessagesDto(Set<Message> entities);
}
