package com.chirko.onLine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.File;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Img {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;
    @Column(nullable = false)
    private File img;
    @Column(name = "post_id")
    private UUID postId;
    @Column(name = "create_date", columnDefinition = "DATE", nullable = false)
    private LocalDate createDate;
    @Column(name = "modified_date", columnDefinition = "DATE", nullable = false)
    private LocalDate modifiedDate;
    @OneToOne(mappedBy = "avatar")
    private User user;
}
