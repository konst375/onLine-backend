package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepo extends CrudRepository<Message, UUID> {
}
