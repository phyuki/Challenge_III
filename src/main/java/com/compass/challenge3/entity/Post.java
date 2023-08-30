package com.compass.challenge3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @Column(unique = true)
    private Long id;

    private String title;

    @Column(length=512)
    private String body;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<Comment> comments;

    @NotNull
    @Column(nullable = false)
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<History> history;

}
