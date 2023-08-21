package com.chirko.onLine.repos.postgres;

import com.chirko.onLine.entities.Otp;
import com.chirko.onLine.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepo extends CrudRepository<Otp, UUID> {
    Optional<Otp> findByToken(String token);

    Optional<Otp> findByUser(User user);
}
