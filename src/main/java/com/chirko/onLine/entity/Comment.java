package com.chirko.onLine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;
    @Column(nullable = false)
    private String text;
    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp createdDate;
    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp modifiedDate;
}
