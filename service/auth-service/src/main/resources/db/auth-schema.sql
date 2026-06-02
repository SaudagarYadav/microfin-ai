-- ===========================================
-- FINPILOT AUTH SERVICE SCHEMA
-- PostgreSQL
-- ===========================================

-- ===========================================
-- USERS
-- ===========================================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,

    first_name VARCHAR(100),
    last_name VARCHAR(100),

    provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL', -- LOCAL, GOOGLE, GITHUB
    provider_id VARCHAR(255),

    enabled BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================
-- ROLES
-- ===========================================

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (role_name)
VALUES
('ROLE_USER'),
('ROLE_ADMIN');

-- ===========================================
-- USER ROLES
-- ===========================================

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

-- ===========================================
-- REFRESH TOKENS
-- ===========================================

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,

    token VARCHAR(500) UNIQUE NOT NULL,

    user_id BIGINT NOT NULL,

    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date TIMESTAMP NOT NULL,

    revoked BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ===========================================
-- PASSWORD RESET TOKENS
-- ===========================================

CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,

    token VARCHAR(500) UNIQUE NOT NULL,

    user_id BIGINT NOT NULL,

    expiry_date TIMESTAMP NOT NULL,

    used BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_password_reset_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ===========================================
-- LOGIN AUDIT
-- ===========================================

CREATE TABLE login_audit (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT,

    username VARCHAR(100),

    ip_address VARCHAR(100),

    login_status VARCHAR(20), -- SUCCESS / FAILED

    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_login_audit_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- ===========================================
-- INDEXES
-- ===========================================

CREATE INDEX idx_users_username
ON users(username);

CREATE INDEX idx_users_email
ON users(email);

CREATE INDEX idx_refresh_tokens_user_id
ON refresh_tokens(user_id);

CREATE INDEX idx_login_audit_user_id
ON login_audit(user_id);

-- ===========================================
-- DEFAULT ADMIN USER
-- Password = Admin@123
-- Replace password hash before execution
-- ===========================================

INSERT INTO users (
    username,
    email,
    password,
    first_name,
    last_name
)
VALUES (
    'admin',
    'admin@finpilot.com',
    '$2a$10.REPLACE_WITH_BCRYPT_HASH',
    'System',
    'Administrator'
);

INSERT INTO user_roles (
    user_id,
    role_id
)
VALUES (
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM roles WHERE role_name = 'ROLE_ADMIN')
);

-- ===========================================
-- SAMPLE NORMAL USER
-- Password = User@123
-- Replace password hash before execution
-- ===========================================

INSERT INTO users (
    username,
    email,
    password,
    first_name,
    last_name
)
VALUES (
    'Saudagar',
    'saudagar.yadav@gmail.com',
    'admin',
    'Saudagar',
    'Yadav'
);

INSERT INTO user_roles (
    user_id,
    role_id
)
VALUES (
    (SELECT id FROM users WHERE username = 'Saudagar'),
    (SELECT id FROM roles WHERE role_name = 'ROLE_USER')
);