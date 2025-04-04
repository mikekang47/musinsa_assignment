CREATE TABLE products
(
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    price            DECIMAL(19, 2) NOT NULL,
    category_id      BIGINT         NOT NULL,
    brand            VARCHAR(255)   NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX idx_product_category_id ON products (category_id);
CREATE INDEX idx_product_brand ON products (brand);
CREATE INDEX idx_product_category_price ON products (category_id, price);
