package com.chirko.onLine.dto.request.community;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class RQRegisterCommunityDto {
    private String name;
    private String subject;
    private MultipartFile avatar;
    private String tags;
}
