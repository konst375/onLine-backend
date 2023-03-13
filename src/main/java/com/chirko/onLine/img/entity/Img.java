package com.chirko.onLine.img.entity;

import com.chirko.onLine.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Img {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] img;

    @Column(name = "post_id")
    private UUID postId;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date", nullable = false)
    private Timestamp modifiedDate;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;

    @Transient
    private boolean isAvatar = false;
}
