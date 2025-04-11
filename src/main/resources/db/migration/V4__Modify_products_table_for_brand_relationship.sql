-- Drop the existing brand column and add brand_id with foreign key
ALTER TABLE products ALTER COLUMN brand SET NULL;
ALTER TABLE products DROP COLUMN brand;
ALTER TABLE products ADD brand_id BIGINT NOT NULL;
ALTER TABLE products ADD CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES brands(id);

-- Add index for brand and category combination
CREATE INDEX IF NOT EXISTS idx_product_brand_category ON products(brand_id, category_id); 