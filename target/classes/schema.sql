-- MyPass Database Schema

-- Users table
CREATE TABLE IF NOT EXISTS users (
    email VARCHAR(255) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    security_question_1 VARCHAR(500),
    security_question_2 VARCHAR(500),
    security_question_3 VARCHAR(500),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vault Items table (stores all types of vault items)
CREATE TABLE IF NOT EXISTS vault_items (
    id VARCHAR(36) PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_email) REFERENCES users(email) ON DELETE CASCADE
);

-- Login Items table
CREATE TABLE IF NOT EXISTS login_items (
    item_id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(500),
    password VARCHAR(500),
    url VARCHAR(1000),
    notes TEXT,
    FOREIGN KEY (item_id) REFERENCES vault_items(id) ON DELETE CASCADE
);

-- Credit Card Items table
CREATE TABLE IF NOT EXISTS credit_card_items (
    item_id VARCHAR(36) PRIMARY KEY,
    card_number VARCHAR(50),
    cardholder_name VARCHAR(255),
    cvv VARCHAR(10),
    expiration_date DATE,
    notes TEXT,
    FOREIGN KEY (item_id) REFERENCES vault_items(id) ON DELETE CASCADE
);

-- Identity Items table
CREATE TABLE IF NOT EXISTS identity_items (
    item_id VARCHAR(36) PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    passport_number VARCHAR(100),
    license_number VARCHAR(100),
    social_security_number VARCHAR(50),
    address VARCHAR(500),
    phone VARCHAR(50),
    email VARCHAR(255),
    notes TEXT,
    FOREIGN KEY (item_id) REFERENCES vault_items(id) ON DELETE CASCADE
);

-- Secure Note Items table
CREATE TABLE IF NOT EXISTS secure_note_items (
    item_id VARCHAR(36) PRIMARY KEY,
    content TEXT,
    FOREIGN KEY (item_id) REFERENCES vault_items(id) ON DELETE CASCADE
);

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_vault_items_user_email ON vault_items(user_email);
CREATE INDEX IF NOT EXISTS idx_vault_items_type ON vault_items(item_type);

