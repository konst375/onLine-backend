package com.chirko.onLine.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RQRegisterCommunityDto {
    private String name;
    private String subject;
    private MultipartFile avatar;
    private String tags;
}
