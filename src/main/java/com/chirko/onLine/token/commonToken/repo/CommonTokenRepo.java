package com.chirko.onLine.token.commonToken.repo;

import com.chirko.onLine.user.entity.User;
import com.chirko.onLine.token.commonToken.entity.CommonToken;
import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommonTokenRepo extends CrudRepository<CommonToken, UUID> {

    Optional<CommonToken> findByToken(String token);

    Optional<CommonToken> findByUser(User user);

    @Override
    void delete(@NonNull CommonToken entity);
}
