package com.chirko.onLine.entity;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Img extends AbstractEntity {
    @EqualsAndHashCode.Include
    @Column(nullable = false, columnDefinition = "BYTEA")
    private byte[] img;

    @EqualsAndHashCode.Include
    @Builder.Default
    private boolean isAvatar = false;

    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @EqualsAndHashCode.Include
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "img")
    private Set<Comment> comments;
}
