CREATE TABLE post_role
(
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users (id) NOT NULL,
    post_id BIGINT REFERENCES post (id)  NOT NULL,
    type    VARCHAR(50)                  NOT NULL,
    UNIQUE (user_id, post_id, type)
);