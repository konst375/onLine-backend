package com.chirko.onLine.entity;

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
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;
    @Column(name = "modified_date", nullable = false)
    private LocalDate modifiedDate;
}
