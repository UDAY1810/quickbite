CREATE TABLE drivers (
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    phone     VARCHAR(30),
    available BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE deliveries (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT NOT NULL UNIQUE,
    driver_id  BIGINT NOT NULL REFERENCES drivers(id),
    status     VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO drivers (name, phone) VALUES
('Ravi Kumar', '9876543210'),
('Anita Sharma', '9876543211'),
('Imran Khan', '9876543212');