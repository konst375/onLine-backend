package com.chirko.onLine.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
@Setter
public class RQPostDto {
    private String text;
    private String tags;
    private Set<MultipartFile> images;
}
