package com.chirko.onLine.entities;

import com.chirko.onLine.entities.enums.Role;
import com.google.common.collect.Sets;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.chirko.onLine.entities.enums.Role.USER;

@Entity
@Table(name = "member")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class User extends AbstractEntity implements UserDetails {
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

    @Column(nullable = false)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Set<Role> roles = Sets.newHashSet(USER);

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Img> images;

    @OneToMany(mappedBy = "user")
    private Set<Post> posts;

    @ManyToMany(mappedBy = "viewers")
    private Set<Post> viewedPosts = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Comment> comments;

    @OneToMany(mappedBy = "admin")
    private Set<Community> ownedCommunities;

    @ManyToMany(mappedBy = "followers")
    private Set<Community> communities;

    @OneToMany(mappedBy = "user")
    private Set<Like> likes;

    @ManyToMany(mappedBy = "participants")
    private Set<Chat> chats;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
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

    //they're also used by mapper
    public Img getAvatar() {
        if (images == null)
            return null;
        return images.stream()
                .filter(Img::isAvatar)
                .findFirst()
                .orElse(null);
    }

    public Img getCover() {
        if (images == null)
            return null;
        return images.stream()
                .filter(Img::isCover)
                .findFirst()
                .orElse(null);
    }
}
