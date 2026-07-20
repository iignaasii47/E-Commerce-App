DELETE FROM products
WHERE id NOT IN (
    SELECT MIN(id) FROM products GROUP BY name
);

ALTER TABLE products ADD CONSTRAINT uq_products_name UNIQUE (name);
