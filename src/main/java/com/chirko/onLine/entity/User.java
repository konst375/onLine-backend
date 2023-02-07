package com.chirko.onLine.entity;

import com.chirko.onLine.entity.enums.Roles;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity(name = "member")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;
    private String alias;
    private String name;
    private String surname;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private LocalDate birthday;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles role;
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;
    @Column(name = "modified_date", nullable = false)
    private LocalDate modifiedDate;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "avatar", referencedColumnName = "id")
    private Img avatar;
    @OneToMany(mappedBy = "userId")
    private List<Post> posts;
    @OneToMany(mappedBy = "userId")
    private List<Comment> comments;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
