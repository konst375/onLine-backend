package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TagRepo extends CrudRepository<Tag, UUID> {
}
