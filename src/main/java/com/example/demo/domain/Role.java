package com.example.demo.domain;

import java.util.Set;

public interface Role {
    boolean includes(Role role);

    Set<Role> children();
}
