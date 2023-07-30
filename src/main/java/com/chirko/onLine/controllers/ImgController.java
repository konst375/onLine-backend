package com.chirko.onLine.controllers;

import com.chirko.onLine.dto.response.img.FullImgDto;
import com.chirko.onLine.services.ImgService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/img")
public class ImgController {
    private final ImgService imgService;

    @GetMapping("/{imgId}")
    public ResponseEntity<FullImgDto> getImg(@PathVariable UUID imgId) {
        FullImgDto response = imgService.getFullImgDto(imgId);
        return ResponseEntity.ok(response);
    }
}
