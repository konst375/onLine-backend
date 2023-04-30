package com.chirko.onLine.services;

import com.chirko.onLine.entities.Community;
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
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImgService {
    private final ImgRepo imgRepo;

    public Img getById(UUID imgId) {
        return imgRepo.findById(imgId)
                .orElseThrow(() -> new OnLineException("Image not found, imgId: " + imgId, ErrorCause.IMAGE_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    public byte[] getBytes(MultipartFile img) {
        try {
            return img.getBytes();
        } catch (IOException e) {
            throw new OnLineException(e.getMessage(), ErrorCause.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Img buildUserAvatar(MultipartFile avatar, User user) {
        Img builtAvatar = buildAvatar(avatar);
        builtAvatar.setUser(user);
        return builtAvatar;
    }

    public Img buildCommunityAvatar(MultipartFile avatar, Community community) {
        Img builtAvatar = buildAvatar(avatar);
        builtAvatar.setCommunity(community);
        return builtAvatar;
    }

    public Set<Img> findUserImages(User user) {
        return imgRepo.findImagesByUser(user).orElseThrow(() -> new OnLineException(
                "Images noy found, userId: " + user.getId(),
                ErrorCause.IMAGE_NOT_FOUND,
                HttpStatus.NOT_FOUND));
    }

    private Img buildAvatar(MultipartFile avatar) {
        return Img.builder()
                .img(getBytes(avatar))
                .isAvatar(true)
                .build();
    }
}
