package com.chirko.onLine.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Img extends AbstractEntity {
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] img;

    @Builder.Default
    private boolean isAvatar = false;

    @Builder.Default
    private boolean isCover = false;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "community_id", referencedColumnName = "id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "img")
    private Set<Comment> comments;
}
