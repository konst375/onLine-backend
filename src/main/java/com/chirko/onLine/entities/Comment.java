package com.chirko.onLine.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Comment extends AbstractEntity {
    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "img_id", referencedColumnName = "id")
    private Img img;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private Set<Like> likes;

    public Set<User> getWhoLiked() {
        return this.getLikes().stream()
                .map(Like::getUser)
                .collect(Collectors.toSet());
    }
}
