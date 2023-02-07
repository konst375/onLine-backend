package com.chirko.onLine.repo;

import com.chirko.onLine.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepo extends CrudRepository<Post, UUID> {
}
