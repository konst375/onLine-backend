package com.chirko.onLine.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UserPostDto {
    private String text;
    private List<MultipartFile> images;
}
