CREATE TABLE restaurants (
    id          BIGSERIAL PRIMARY KEY,
    owner_id    BIGINT NOT NULL,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    address     VARCHAR(500),
    cuisine     VARCHAR(100),
    rating      NUMERIC(2,1) DEFAULT 0,
    is_open     BOOLEAN DEFAULT true,
    image_url   VARCHAR(500)
);

CREATE TABLE menu_items (
    id            BIGSERIAL PRIMARY KEY,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id),
    name          VARCHAR(255) NOT NULL,
    description   TEXT,
    price         NUMERIC(10,2) NOT NULL,
    category      VARCHAR(100),
    is_available  BOOLEAN DEFAULT true,
    image_url     VARCHAR(500)
);