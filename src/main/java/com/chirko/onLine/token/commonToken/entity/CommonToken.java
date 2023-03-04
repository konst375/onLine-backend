package com.chirko.onLine.token.commonToken.entity;

import com.chirko.onLine.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "common_token")
public class CommonToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;

    private LocalDate expireDate;
}
