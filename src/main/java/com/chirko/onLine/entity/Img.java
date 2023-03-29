package com.chirko.onLine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
}
