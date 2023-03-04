package com.chirko.onLine.comment.entity;

import com.chirko.onLine.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
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
    private User userId;
    @Column(nullable = false)
    private String text;
    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;
    @UpdateTimestamp
    @Column(name = "modified_date", nullable = false)
    private LocalDate modifiedDate;
}
