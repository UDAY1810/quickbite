CREATE TABLE orders (
    id             BIGSERIAL PRIMARY KEY,
    customer_id    BIGINT NOT NULL,
    restaurant_id  BIGINT NOT NULL,
    status         VARCHAR(40) NOT NULL DEFAULT 'PLACED',
    total_amount   NUMERIC(10,2) NOT NULL,
    delivery_addr  VARCHAR(500) NOT NULL,
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE order_items (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT NOT NULL REFERENCES orders(id),
    menu_item_id BIGINT NOT NULL,
    item_name    VARCHAR(255) NOT NULL,
    unit_price   NUMERIC(10,2) NOT NULL,
    quantity     INT NOT NULL
);
CREATE INDEX idx_orders_customer ON orders(customer_id);