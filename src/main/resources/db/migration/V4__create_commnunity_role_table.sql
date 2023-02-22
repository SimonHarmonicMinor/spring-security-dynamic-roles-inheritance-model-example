CREATE TABLE community_role
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT REFERENCES users (id)     NOT NULL,
    community_id BIGINT REFERENCES community (id) NOT NULL,
    type         VARCHAR(50)                      NOT NULL,
    UNIQUE (user_id, community_id, type)
);