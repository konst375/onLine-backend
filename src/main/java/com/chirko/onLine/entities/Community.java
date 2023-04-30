package com.chirko.onLine.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Community extends AbstractEntity {
    private String name;
    private String subject;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "community")
    private Set<Img> images;

    @OneToMany(mappedBy = "community")
    private Set<Post> posts;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User admin;

    @ManyToMany
    @JoinTable(name = "moderator", joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<User> moderators;

    @ManyToMany
    @JoinTable(name = "follower", joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<User> followers;

    @ManyToMany
    @JoinTable(name = "tag_community", joinColumns = @JoinColumn(name = "community_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    //they're also used by mapper
    public Img getAvatar() {
        return images.stream()
                .filter(Img::isAvatar)
                .findFirst()
                .orElse(null);
    }

    public Img getCover() {
        return images.stream()
                .filter(Img::isCover)
                .findFirst()
                .orElse(null);
    }
}
