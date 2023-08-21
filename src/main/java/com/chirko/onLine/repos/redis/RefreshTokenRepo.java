package com.chirko.onLine.repos.redis;

import com.chirko.onLine.entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepo extends CrudRepository<RefreshToken, String> {
}
