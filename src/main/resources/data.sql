-- Insert Categories
INSERT INTO categories (name) VALUES
    ('상의'),
    ('아우터'),
    ('바지'),
    ('스니커즈'),
    ('가방'),
    ('모자'),
    ('양말'),
    ('액세서리');

-- Insert Brands
INSERT INTO brands (name) VALUES
    ('A'),
    ('B'),
    ('C'),
    ('D'),
    ('E'),
    ('F'),
    ('G'),
    ('H'),
    ('I');

-- Insert Products for each brand
-- Brand A
INSERT INTO products (brand_id, category_id, price) VALUES
    (1, (SELECT id FROM categories WHERE name = '상의'), 11200),
    (1, (SELECT id FROM categories WHERE name = '아우터'), 5500),
    (1, (SELECT id FROM categories WHERE name = '바지'), 4200),
    (1, (SELECT id FROM categories WHERE name = '스니커즈'), 9000),
    (1, (SELECT id FROM categories WHERE name = '가방'), 2000),
    (1, (SELECT id FROM categories WHERE name = '모자'), 1700),
    (1, (SELECT id FROM categories WHERE name = '양말'), 1800),
    (1, (SELECT id FROM categories WHERE name = '액세서리'), 2300);

-- Brand B
INSERT INTO products (brand_id, category_id, price) VALUES
    (2, (SELECT id FROM categories WHERE name = '상의'), 10500),
    (2, (SELECT id FROM categories WHERE name = '아우터'), 5900),
    (2, (SELECT id FROM categories WHERE name = '바지'), 3800),
    (2, (SELECT id FROM categories WHERE name = '스니커즈'), 9100),
    (2, (SELECT id FROM categories WHERE name = '가방'), 2100),
    (2, (SELECT id FROM categories WHERE name = '모자'), 2000),
    (2, (SELECT id FROM categories WHERE name = '양말'), 2000),
    (2, (SELECT id FROM categories WHERE name = '액세서리'), 2200);

-- Brand C
INSERT INTO products (brand_id, category_id, price) VALUES
    (3, (SELECT id FROM categories WHERE name = '상의'), 10000),
    (3, (SELECT id FROM categories WHERE name = '아우터'), 6200),
    (3, (SELECT id FROM categories WHERE name = '바지'), 3300),
    (3, (SELECT id FROM categories WHERE name = '스니커즈'), 9200),
    (3, (SELECT id FROM categories WHERE name = '가방'), 2200),
    (3, (SELECT id FROM categories WHERE name = '모자'), 1900),
    (3, (SELECT id FROM categories WHERE name = '양말'), 2200),
    (3, (SELECT id FROM categories WHERE name = '액세서리'), 2100);

-- Brand D
INSERT INTO products (brand_id, category_id, price) VALUES
    (4, (SELECT id FROM categories WHERE name = '상의'), 10100),
    (4, (SELECT id FROM categories WHERE name = '아우터'), 5100),
    (4, (SELECT id FROM categories WHERE name = '바지'), 3000),
    (4, (SELECT id FROM categories WHERE name = '스니커즈'), 9500),
    (4, (SELECT id FROM categories WHERE name = '가방'), 2500),
    (4, (SELECT id FROM categories WHERE name = '모자'), 1500),
    (4, (SELECT id FROM categories WHERE name = '양말'), 2400),
    (4, (SELECT id FROM categories WHERE name = '액세서리'), 2000);

-- Brand E
INSERT INTO products (brand_id, category_id, price) VALUES
    (5, (SELECT id FROM categories WHERE name = '상의'), 10700),
    (5, (SELECT id FROM categories WHERE name = '아우터'), 5000),
    (5, (SELECT id FROM categories WHERE name = '바지'), 3800),
    (5, (SELECT id FROM categories WHERE name = '스니커즈'), 9900),
    (5, (SELECT id FROM categories WHERE name = '가방'), 2300),
    (5, (SELECT id FROM categories WHERE name = '모자'), 1800),
    (5, (SELECT id FROM categories WHERE name = '양말'), 2100),
    (5, (SELECT id FROM categories WHERE name = '액세서리'), 2100);

-- Brand F
INSERT INTO products (brand_id, category_id, price) VALUES
    (6, (SELECT id FROM categories WHERE name = '상의'), 11200),
    (6, (SELECT id FROM categories WHERE name = '아우터'), 7200),
    (6, (SELECT id FROM categories WHERE name = '바지'), 4000),
    (6, (SELECT id FROM categories WHERE name = '스니커즈'), 9300),
    (6, (SELECT id FROM categories WHERE name = '가방'), 2100),
    (6, (SELECT id FROM categories WHERE name = '모자'), 1600),
    (6, (SELECT id FROM categories WHERE name = '양말'), 2300),
    (6, (SELECT id FROM categories WHERE name = '액세서리'), 1900);

-- Brand G
INSERT INTO products (brand_id, category_id, price) VALUES
    (7, (SELECT id FROM categories WHERE name = '상의'), 10500),
    (7, (SELECT id FROM categories WHERE name = '아우터'), 5800),
    (7, (SELECT id FROM categories WHERE name = '바지'), 3900),
    (7, (SELECT id FROM categories WHERE name = '스니커즈'), 9000),
    (7, (SELECT id FROM categories WHERE name = '가방'), 2200),
    (7, (SELECT id FROM categories WHERE name = '모자'), 1700),
    (7, (SELECT id FROM categories WHERE name = '양말'), 2100),
    (7, (SELECT id FROM categories WHERE name = '액세서리'), 2000);

-- Brand H
INSERT INTO products (brand_id, category_id, price) VALUES
    (8, (SELECT id FROM categories WHERE name = '상의'), 10800),
    (8, (SELECT id FROM categories WHERE name = '아우터'), 6300),
    (8, (SELECT id FROM categories WHERE name = '바지'), 3100),
    (8, (SELECT id FROM categories WHERE name = '스니커즈'), 9700),
    (8, (SELECT id FROM categories WHERE name = '가방'), 2100),
    (8, (SELECT id FROM categories WHERE name = '모자'), 1600),
    (8, (SELECT id FROM categories WHERE name = '양말'), 2000),
    (8, (SELECT id FROM categories WHERE name = '액세서리'), 2000);

-- Brand I
INSERT INTO products (brand_id, category_id, price) VALUES
    (9, (SELECT id FROM categories WHERE name = '상의'), 11400),
    (9, (SELECT id FROM categories WHERE name = '아우터'), 6700),
    (9, (SELECT id FROM categories WHERE name = '바지'), 3200),
    (9, (SELECT id FROM categories WHERE name = '스니커즈'), 9500),
    (9, (SELECT id FROM categories WHERE name = '가방'), 2400),
    (9, (SELECT id FROM categories WHERE name = '모자'), 1700),
    (9, (SELECT id FROM categories WHERE name = '양말'), 1700),
    (9, (SELECT id FROM categories WHERE name = '액세서리'), 2400);
