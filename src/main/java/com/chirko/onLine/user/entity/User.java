package com.chirko.onLine.user.entity;

import com.chirko.onLine.comment.entity.Comment;
import com.chirko.onLine.user.entity.enums.Role;
import com.chirko.onLine.img.entity.Img;
import com.chirko.onLine.post.entity.Post;
import com.chirko.onLine.token.commonToken.entity.CommonToken;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    private String name;

    private String surname;
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = false;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Exclude
    private Role role;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date", nullable = false)
    private LocalDate modifiedDate;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "avatar", referencedColumnName = "id")
    @ToString.Exclude
    private Img avatar;

    @OneToMany(mappedBy = "userId")
    @ToString.Exclude
    private List<Post> posts;

    @OneToMany(mappedBy = "userId")
    @ToString.Exclude
    private List<Comment> comments;

    @OneToOne(mappedBy = "user")
    @ToString.Exclude
    private CommonToken commonToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
