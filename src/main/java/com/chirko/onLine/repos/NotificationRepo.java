package com.chirko.onLine.repos;

import com.chirko.onLine.entities.Notification;
import com.chirko.onLine.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface NotificationRepo extends CrudRepository<Notification, UUID> {
    Optional<Set<Notification>> findAllByUser(User user);
}
