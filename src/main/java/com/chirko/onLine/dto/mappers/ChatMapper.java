package com.chirko.onLine.dto.mappers;

import com.chirko.onLine.dto.response.communication.ChatDto;
import com.chirko.onLine.entities.Chat;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = MessageMapper.class)
public interface ChatMapper {
    ChatDto toDto(Chat entity);

    Set<ChatDto> toDtoSet(Set<Chat> chats);
}
