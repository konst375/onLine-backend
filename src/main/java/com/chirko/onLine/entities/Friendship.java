package com.chirko.onLine.entities;

import com.chirko.onLine.entities.enums.FriendshipStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Friendship extends AbstractEntity {
    @ManyToOne
    private User recipient;

    @ManyToOne
    private User sender;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    public User getFriend(User user) {
        if (recipient.equals(user)) {
            return sender;
        }
        return recipient;
    }
}
