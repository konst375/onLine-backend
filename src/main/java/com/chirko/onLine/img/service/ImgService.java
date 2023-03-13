package com.chirko.onLine.img.service;

import com.chirko.onLine.img.entity.Img;
import com.chirko.onLine.img.repo.ImgRepo;
import com.chirko.onLine.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImgService {
    private final ImgRepo imgRepo;

    public Img createAvatarForUser(MultipartFile avatar, User user) {
        final Img createdAvatar = buildImgForUser(user, getBytes(avatar));

        imgRepo.save(createdAvatar);
        return createdAvatar;
    }

    @Transactional
    public void updateAvatarForUser(MultipartFile avatar, User user) {
        imgRepo.findByUser(user)
                .orElseGet(() -> createAvatarForUser(avatar, user))
                .setImg(getBytes(avatar));
    }

    private byte[] getBytes(MultipartFile avatar) {
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

    private Img buildImgForUser(User user, byte[] img) {
        return Img.builder()
                .isAvatar(true)
                .img(img)
                .user(user)
                .build();
    }
}
