package com.chirko.onLine.post.entity;

import com.chirko.onLine.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User userId;
    private String text;
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;
    @Column(name = "modified_date", nullable = false)
    private LocalDate modifiedDate;
}
