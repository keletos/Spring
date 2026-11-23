CREATE TABLE app_user (
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(64)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE user_role (
    user_id BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role    VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT chk_user_role CHECK (role IN ('STUDENT', 'ADMIN'))
);

-- Demo-only accounts. Change these passwords before exposing the application.
INSERT INTO app_user (username, password_hash, enabled) VALUES
    ('student', '{bcrypt}$2y$12$tlVCrp6UiEsbc0KA2fs71.SqHb9LXSpCDQsczrEzmnukUeIdizsvi', TRUE),
    ('admin',   '{bcrypt}$2y$12$iLq47nMvKlmnguziZStMu.0EBa1OhsaUGr8Iki.r9nH8GIJzI8iEK', TRUE);

INSERT INTO user_role (user_id, role)
SELECT id, 'STUDENT' FROM app_user WHERE username = 'student';

INSERT INTO user_role (user_id, role)
SELECT id, 'ADMIN' FROM app_user WHERE username = 'admin';
