package com.chirko.onLine.comment.repo;

import com.chirko.onLine.comment.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepo extends CrudRepository<Comment, UUID> {
}
