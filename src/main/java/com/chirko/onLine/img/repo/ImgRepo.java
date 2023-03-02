package com.chirko.onLine.img.repo;

import com.chirko.onLine.img.entity.Img;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImgRepo extends CrudRepository<Img, UUID> {
}
