package com.chirko.onLine.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UserPostDto {
    private String text;
    private List<MultipartFile> images;
}
