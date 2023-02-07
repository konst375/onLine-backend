package com.chirko.onLine.repo;

import com.chirko.onLine.entity.Img;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImgRepo extends CrudRepository<Img, UUID> {
}
