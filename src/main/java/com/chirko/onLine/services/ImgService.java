package com.chirko.onLine.services;

import com.chirko.onLine.dto.mappers.ImgMapper;
import com.chirko.onLine.dto.response.img.BaseImgDto;
import com.chirko.onLine.dto.response.img.FullImgDto;
import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.User;
import com.chirko.onLine.exceptions.ErrorCause;
import com.chirko.onLine.exceptions.OnLineException;
import com.chirko.onLine.repos.ImgRepo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ImgService {
    private final ImgRepo imgRepo;
    private final ImgMapper imgMapper;

    public Img getById(UUID imgId) {
        return imgRepo.findById(imgId)
                .orElseThrow(() -> new OnLineException(
                        "Image not found, imgId: " + imgId,
                        ErrorCause.IMAGE_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    public byte[] getBytes(MultipartFile img) {
        try {
            return img.getBytes();
        } catch (IOException exception) {
            throw new OnLineException(
                    exception.getMessage(),
                    ErrorCause.INTERNAL_SERVER_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Img> findUserImages(User user) {
        return imgRepo.findImagesByUser(user).orElseThrow(() -> new OnLineException(
                "Images noy found, userId: " + user.getId(),
                ErrorCause.IMAGE_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    public List<Img> createImages(List<MultipartFile> images) {
        if (images == null) {
            return null;
        }
        return images.stream()
                .map(this::getBytes)
                .map(bytes -> Img.builder().img(bytes).build())
                .collect(Collectors.toList());
    }

    public Img createAvatar(MultipartFile avatar) {
        if (avatar == null) {
            return null;
        }
        return Img.builder()
                .img(getBytes(avatar))
                .isAvatar(true)
                .build();
    }

    public Img createCover(MultipartFile cover) {
        return Img.builder()
                .img(getBytes(cover))
                .isCover(true)
                .build();
    }

    public BaseImgDto toDto(Img img) {
        return imgMapper.toDto(img);
    }

    public FullImgDto toFullImgDto(Img img) {
        return imgMapper.toFullDto(img);
    }

    public FullImgDto getFullImgDto(UUID imgId) {
        return imgMapper.toFullDto(getFullImgById(imgId));
    }

    public Img getFullImgById(UUID imgId) {
        Img img = imgRepo.findByIdWithLikesAndComments(imgId)
                .orElseThrow(() -> new OnLineException(ErrorCause.IMAGE_NOT_FOUND, HttpStatus.NOT_FOUND));
        img.getLikes().forEach(like -> {
            User user = like.getUser();
            user.setImages(findUserImages(user));
        });
        return img;
    }
}
