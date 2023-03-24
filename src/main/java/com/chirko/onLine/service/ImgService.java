package com.chirko.onLine.service;

import com.chirko.onLine.entity.Img;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.repo.ImgRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ImgService {
    private final ImgRepo imgRepo;

    @Transactional
    public void updateAvatarForUser(MultipartFile avatar, User user) {
        getAvatarImg(user).setImg(getBytes(avatar));
    }

    public byte[] getBytes(MultipartFile avatar) {
        final byte[] img;
        try {
            img = avatar == null || avatar.isEmpty()
                    ? Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("static/defaultAvatar.png")).readAllBytes()
                    : avatar.getBytes();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return img;
    }

    private Img getAvatarImg(User user) {
        return imgRepo.findByUser(user)
                .orElseGet(() -> {
                    final Img createdAvatar = Img.builder()
                            .isAvatar(true)
                            .img(getBytes(null))
                            .user(user)
                            .build();
                    imgRepo.save(createdAvatar);
                    return createdAvatar;
                });
    }
}
