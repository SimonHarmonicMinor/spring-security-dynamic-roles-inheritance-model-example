CREATE TABLE post
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(200)                     NOT NULL,
    community_id BIGINT REFERENCES community (id) NOT NULL
);