package com.example.demo.controller;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

public class DatabaseUtil {
    private static final List<String> TABLES = List.of(
        "community_role",
        "post_role",
        "post",
        "community",
        "users"
    );

    public static void cleanDatabase(JdbcTemplate jdbcTemplate) {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TABLES.toArray(String[]::new));
    }
}
