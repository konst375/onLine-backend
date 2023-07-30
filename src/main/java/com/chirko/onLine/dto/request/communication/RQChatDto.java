package com.chirko.onLine.dto.request.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RQChatDto {
    private Set<UUID> participants;
    private String name;
    private MultipartFile avatar;
}
