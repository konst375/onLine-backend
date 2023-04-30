package com.chirko.onLine.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CommonToken {
    @Transient
    public static final long EXPIRATION = 300000;//5 min

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    private String token;

    private Timestamp expireTimestamp;

    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;
}
