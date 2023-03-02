package com.chirko.onLine.post.repo;

import com.chirko.onLine.post.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepo extends CrudRepository<Post, UUID> {
}
