package com.chirko.onLine.repos.postgres;

import com.chirko.onLine.entities.TagScores;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagScoresRepo extends CrudRepository<TagScores, UUID> {
    Optional<List<TagScores>> findAllByUserId(UUID userId);
}
