CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email VARCHAR(255) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX ix_users_email ON users (email);
CREATE INDEX ix_users_id ON users (id);

CREATE TABLE categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE UNIQUE INDEX ix_categories_name ON categories (name);
CREATE INDEX ix_categories_id ON categories (id);

CREATE TABLE items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    category_id INTEGER,
    CONSTRAINT fk_items_category_id_categories FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX ix_items_id ON items (id);
CREATE INDEX ix_items_category_id ON items (category_id);
