package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Chat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ChatRepo extends CrudRepository<Chat, UUID> {
    @Query("""
            SELECT c
            FROM Chat c
            WHERE c.id = :id
            """)
    @EntityGraph(value = "Chat-with-dependencies", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Chat> findByIdWithAllDependencies(@Param("id") UUID chatId);

    @Query("""
            SELECT c
            FROM Chat c
            WHERE c IN :chats
            """)
    @EntityGraph(value = "Chat-with-dependencies", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Set<Chat>> findAllByIdWithAllDependencies(@Param("chats") Set<Chat> chats);
}
