package com.chirko.onLine.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDto {
    UUID id;
    String name;
    String surname;
    ImgDto avatar;
}
