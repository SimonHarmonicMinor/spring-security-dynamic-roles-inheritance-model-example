package com.example.demo.domain;

import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "community")
@Getter
public class Community {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch = LAZY, mappedBy = "community")
    private List<Post> post = new ArrayList<>();

    public static Community newCommunity(String name) {
        final var community = new Community();
        community.name = name;
        return community;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Community community = (Community) o;
        return id != null && Objects.equals(id, community.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
