package com.chirko.onLine.entities;

import com.chirko.onLine.entities.enums.Owner;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Post extends AbstractEntity {
    private String text;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_id", referencedColumnName = "id")
    private Community community;

    @ManyToMany
    @JoinTable(name = "tag_post", joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    private List<Img> images;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<Like> likes;

    // it used by mapper
    public Owner getOwner() {
        if (this.getUser() != null) {
            return Owner.USER;
        } else if (this.getCommunity() != null) {
            return Owner.COMMUNITY;
        }
        return null;
    }

    public Set<User> getWhoLiked() {
        return this.getLikes().stream()
                .map(Like::getUser)
                .collect(Collectors.toSet());
    }
}
