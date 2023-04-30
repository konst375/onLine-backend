package com.chirko.onLine.services;

import com.chirko.onLine.dto.response.post.BasePostDto;
import com.chirko.onLine.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class FeedService {
    public Set<BasePostDto> getFeed(User user) {
        //soon
        return null;
    }
}
