package com.chirko.onLine.service;

import com.chirko.onLine.dto.mapper.PostMapper;
import com.chirko.onLine.dto.request.CreateUserPostDto;
import com.chirko.onLine.dto.response.PostDto;
import com.chirko.onLine.entity.Img;
import com.chirko.onLine.entity.Post;
import com.chirko.onLine.entity.User;
import com.chirko.onLine.exception.PostNotFoundException;
import com.chirko.onLine.exception.UserEmailNotFoundException;
import com.chirko.onLine.repo.PostRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepo postRepo;
    private final UserService userService;
    private final PostMapper postMapper;

    public void createUserPost(String email, CreateUserPostDto createUserPostDto) throws UserEmailNotFoundException {

        Post createdPost = new Post();
        createdPost.setUser(userService.findUserByEmail(email));
        createdPost.setText(createUserPostDto.getText());

        if (createUserPostDto.getImages() != null) {
            List<Img> createdImages = createUserPostDto.getImages()
                    .stream()
                    .map(file -> {
                        try {
                            return file.getBytes();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex.getMessage());
                        }
                    })
                    .map(bytes -> Img.builder()
                            .post(createdPost)
                            .img(bytes)
                            .build())
                    .toList();
            createdPost.setImagesList(createdImages);
        }

        postRepo.save(createdPost);
    }

    public PostDto findPost(UUID id) throws PostNotFoundException {
        Post foundPost = postRepo.findByIdAndFetchImagesEagerly(id).orElseThrow(PostNotFoundException::new);
        User user = userService.findUserAndFetchImagedEagerlyByPost(id);
        foundPost.setUser(user);
        return postMapper.postToPostDto(foundPost);
    }
}
