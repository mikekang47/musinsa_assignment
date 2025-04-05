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

-- Insert Products for each brand
-- Brand A
INSERT INTO products (brand, category_id, price) VALUES
    ('A', (SELECT id FROM categories WHERE name = '상의'), 11200),
    ('A', (SELECT id FROM categories WHERE name = '아우터'), 5500),
    ('A', (SELECT id FROM categories WHERE name = '바지'), 4200),
    ('A', (SELECT id FROM categories WHERE name = '스니커즈'), 9000),
    ('A', (SELECT id FROM categories WHERE name = '가방'), 2000),
    ('A', (SELECT id FROM categories WHERE name = '모자'), 1700),
    ('A', (SELECT id FROM categories WHERE name = '양말'), 1800),
    ('A', (SELECT id FROM categories WHERE name = '액세서리'), 2300);

-- Brand B
INSERT INTO products (brand, category_id, price) VALUES
    ('B', (SELECT id FROM categories WHERE name = '상의'), 10500),
    ('B', (SELECT id FROM categories WHERE name = '아우터'), 5900),
    ('B', (SELECT id FROM categories WHERE name = '바지'), 3800),
    ('B', (SELECT id FROM categories WHERE name = '스니커즈'), 9100),
    ('B', (SELECT id FROM categories WHERE name = '가방'), 2100),
    ('B', (SELECT id FROM categories WHERE name = '모자'), 2000),
    ('B', (SELECT id FROM categories WHERE name = '양말'), 2000),
    ('B', (SELECT id FROM categories WHERE name = '액세서리'), 2200);

-- Brand C
INSERT INTO products (brand, category_id, price) VALUES
    ('C', (SELECT id FROM categories WHERE name = '상의'), 10000),
    ('C', (SELECT id FROM categories WHERE name = '아우터'), 6200),
    ('C', (SELECT id FROM categories WHERE name = '바지'), 3300),
    ('C', (SELECT id FROM categories WHERE name = '스니커즈'), 9200),
    ('C', (SELECT id FROM categories WHERE name = '가방'), 2200),
    ('C', (SELECT id FROM categories WHERE name = '모자'), 1900),
    ('C', (SELECT id FROM categories WHERE name = '양말'), 2200),
    ('C', (SELECT id FROM categories WHERE name = '액세서리'), 2100);

-- Brand D
INSERT INTO products (brand, category_id, price) VALUES
    ('D', (SELECT id FROM categories WHERE name = '상의'), 10100),
    ('D', (SELECT id FROM categories WHERE name = '아우터'), 5100),
    ('D', (SELECT id FROM categories WHERE name = '바지'), 3000),
    ('D', (SELECT id FROM categories WHERE name = '스니커즈'), 9500),
    ('D', (SELECT id FROM categories WHERE name = '가방'), 2500),
    ('D', (SELECT id FROM categories WHERE name = '모자'), 1500),
    ('D', (SELECT id FROM categories WHERE name = '양말'), 2400),
    ('D', (SELECT id FROM categories WHERE name = '액세서리'), 2000);

-- Brand E
INSERT INTO products (brand, category_id, price) VALUES
    ('E', (SELECT id FROM categories WHERE name = '상의'), 10700),
    ('E', (SELECT id FROM categories WHERE name = '아우터'), 5000),
    ('E', (SELECT id FROM categories WHERE name = '바지'), 3800),
    ('E', (SELECT id FROM categories WHERE name = '스니커즈'), 9900),
    ('E', (SELECT id FROM categories WHERE name = '가방'), 2300),
    ('E', (SELECT id FROM categories WHERE name = '모자'), 1800),
    ('E', (SELECT id FROM categories WHERE name = '양말'), 2100),
    ('E', (SELECT id FROM categories WHERE name = '액세서리'), 2100);

-- Brand F
INSERT INTO products (brand, category_id, price) VALUES
    ('F', (SELECT id FROM categories WHERE name = '상의'), 11200),
    ('F', (SELECT id FROM categories WHERE name = '아우터'), 7200),
    ('F', (SELECT id FROM categories WHERE name = '바지'), 4000),
    ('F', (SELECT id FROM categories WHERE name = '스니커즈'), 9300),
    ('F', (SELECT id FROM categories WHERE name = '가방'), 2100),
    ('F', (SELECT id FROM categories WHERE name = '모자'), 1600),
    ('F', (SELECT id FROM categories WHERE name = '양말'), 2300),
    ('F', (SELECT id FROM categories WHERE name = '액세서리'), 1900);

-- Brand G
INSERT INTO products (brand, category_id, price) VALUES
    ('G', (SELECT id FROM categories WHERE name = '상의'), 10500),
    ('G', (SELECT id FROM categories WHERE name = '아우터'), 5800),
    ('G', (SELECT id FROM categories WHERE name = '바지'), 3900),
    ('G', (SELECT id FROM categories WHERE name = '스니커즈'), 9000),
    ('G', (SELECT id FROM categories WHERE name = '가방'), 2200),
    ('G', (SELECT id FROM categories WHERE name = '모자'), 1700),
    ('G', (SELECT id FROM categories WHERE name = '양말'), 2100),
    ('G', (SELECT id FROM categories WHERE name = '액세서리'), 2000);

-- Brand H
INSERT INTO products (brand, category_id, price) VALUES
    ('H', (SELECT id FROM categories WHERE name = '상의'), 10800),
    ('H', (SELECT id FROM categories WHERE name = '아우터'), 6300),
    ('H', (SELECT id FROM categories WHERE name = '바지'), 3100),
    ('H', (SELECT id FROM categories WHERE name = '스니커즈'), 9700),
    ('H', (SELECT id FROM categories WHERE name = '가방'), 2100),
    ('H', (SELECT id FROM categories WHERE name = '모자'), 1600),
    ('H', (SELECT id FROM categories WHERE name = '양말'), 2000),
    ('H', (SELECT id FROM categories WHERE name = '액세서리'), 2000);

-- Brand I
INSERT INTO products (brand, category_id, price) VALUES
    ('I', (SELECT id FROM categories WHERE name = '상의'), 11400),
    ('I', (SELECT id FROM categories WHERE name = '아우터'), 6700),
    ('I', (SELECT id FROM categories WHERE name = '바지'), 3200),
    ('I', (SELECT id FROM categories WHERE name = '스니커즈'), 9500),
    ('I', (SELECT id FROM categories WHERE name = '가방'), 2400),
    ('I', (SELECT id FROM categories WHERE name = '모자'), 1700),
    ('I', (SELECT id FROM categories WHERE name = '양말'), 1700),
    ('I', (SELECT id FROM categories WHERE name = '액세서리'), 2400);
