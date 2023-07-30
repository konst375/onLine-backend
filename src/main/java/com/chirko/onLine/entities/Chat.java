package com.chirko.onLine.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@NamedEntityGraph(
        name = "Chat-with-dependencies",
        attributeNodes = {
                @NamedAttributeNode("messages"),
                @NamedAttributeNode(value = "participants", subgraph = "participants-subgraph")
        },
        subgraphs = @NamedSubgraph(name = "participants-subgraph", attributeNodes = @NamedAttributeNode("images"))
)
public class Chat extends AbstractEntity {
    @OneToMany(mappedBy = "chat", orphanRemoval = true)
    private Set<Message> messages = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "user_chat", joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<User> participants;

    private UUID admin;

    private String name;

    @OneToOne(mappedBy = "chat", cascade = CascadeType.ALL)
    private Img avatar;
}
