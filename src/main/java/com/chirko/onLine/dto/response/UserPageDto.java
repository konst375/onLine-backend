package com.chirko.onLine.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserPageDto {
    UUID id;
    String name;
    String surname;
    ImgDto avatar;
    LocalDate birthday;
    List<ImgDto> images;
    Set<PostForKnownUserDto> posts;
}
