CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(255) NOT NULL,
    phone       VARCHAR(30),
    role        VARCHAR(30)  NOT NULL DEFAULT 'CUSTOMER',
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_users_email ON users(email);