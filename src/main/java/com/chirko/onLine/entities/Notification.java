package com.chirko.onLine.entities;

import com.chirko.onLine.entities.enums.NotificationType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Notification extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;

    private UUID target;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean isViewed = false;
}
