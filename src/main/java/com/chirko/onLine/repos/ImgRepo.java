package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Img;
import com.chirko.onLine.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImgRepo extends CrudRepository<Img, UUID> {
    Optional<List<Img>> findImagesByUser(User user);
}
