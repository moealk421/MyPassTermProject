package com.mypass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Singleton database connection manager using H2 embedded database.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static final String DB_URL = "jdbc:h2:./data/mypass;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static boolean initialized = false;
    
    private DatabaseConnection() {
        // Ensure data directory exists for H2 database files
        try {
            Files.createDirectories(Paths.get("./data"));
        } catch (Exception e) {
            System.err.println("Warning: Could not create data directory: " + e.getMessage());
        }
        initializeDatabase();
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() throws SQLException {
        // Load H2 driver explicitly
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 Driver not found!");
            throw new SQLException("H2 Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Creates all database tables if they don't exist.
     * Uses double-checked locking for thread safety.
     */
    private void initializeDatabase() {
        if (initialized) {
            return;
        }
        
        synchronized (DatabaseConnection.class) {
            if (initialized) {
                return;
            }
            
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                
                System.out.println("Initializing database schema...");
                createSchemaDirectly(stmt);
                initialized = true;
                System.out.println("Database initialized successfully");
                
                // Verify tables were created
                try {
                    stmt.executeQuery("SELECT COUNT(*) FROM users");
                    System.out.println("Users table verified");
                } catch (SQLException e) {
                    System.err.println("Warning: Could not verify users table: " + e.getMessage());
                    throw e;
                }
                
            } catch (SQLException e) {
                System.err.println("Error initializing database: " + e.getMessage());
                System.err.println("SQL Error Code: " + e.getErrorCode());
                System.err.println("SQL State: " + e.getSQLState());
                e.printStackTrace();
                initialized = false;
                throw new RuntimeException("Failed to initialize database", e);
            } catch (Exception e) {
                System.err.println("Unexpected error initializing database: " + e.getMessage());
                e.printStackTrace();
                initialized = false;
                throw new RuntimeException("Failed to initialize database", e);
            }
        }
    }
    
    private void createSchemaDirectly(Statement stmt) throws SQLException {
        System.out.println("Creating database tables...");
        
        // Users table - stores credentials and security questions
        try {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "email VARCHAR(255) PRIMARY KEY, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "security_question_1 VARCHAR(500), " +
                "security_question_2 VARCHAR(500), " +
                "security_question_3 VARCHAR(500), " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            System.out.println("✓ Users table created/verified");
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
            throw e;
        }
        
        // Vault Items table - base table for all item types
        try {
            stmt.execute("CREATE TABLE IF NOT EXISTS vault_items (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "user_email VARCHAR(255) NOT NULL, " +
                "item_type VARCHAR(50) NOT NULL, " +
                "name VARCHAR(255) NOT NULL, " +
                "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_email) REFERENCES users(email) ON DELETE CASCADE)");
            System.out.println("✓ Vault items table created/verified");
        } catch (SQLException e) {
            System.err.println("Error creating vault_items table: " + e.getMessage());
            throw e;
        }
        
        // Login Items table - extends vault_items for login credentials
        try {
            stmt.execute("CREATE TABLE IF NOT EXISTS login_items (" +
                "item_id VARCHAR(36) PRIMARY KEY, " +
                "username VARCHAR(500), " +
                "password VARCHAR(500), " +
                "url VARCHAR(1000), " +
                "notes TEXT, " +
                "FOREIGN KEY (item_id) REFERENCES vault_items(id) ON DELETE CASCADE)");
            System.out.println("✓ Login items table created/verified");
        } catch (SQLException e) {
            System.err.println("Error creating login_items table: " + e.getMessage());
            throw e;
        }
        
        // Credit Card Items table - extends vault_items for card data
        try {
            stmt.execute("CREATE TABLE IF NOT EXISTS credit_card_items (" +
                "item_id VARCHAR(36) PRIMARY KEY, " +
                "card_number VARCHAR(50), " +
                "cardholder_name VARCHAR(255), " +
                "cvv VARCHAR(10), " +
                "expiration_date DATE, " +
                "notes TEXT, " +
                "FOREIGN KEY (item_id) REFERENCES vault_items(id) ON DELETE CASCADE)");
            System.out.println("✓ Credit card items table created/verified");
        } catch (SQLException e) {
            System.err.println("Error creating credit_card_items table: " + e.getMessage());
            throw e;
        }
        
        // Identity Items table - extends vault_items for personal documents
        try {
            stmt.execute("CREATE TABLE IF NOT EXISTS identity_items (" +
                "item_id VARCHAR(36) PRIMARY KEY, " +
                "first_name VARCHAR(255), " +
                "last_name VARCHAR(255), " +
                "passport_number VARCHAR(100), " +
                "license_number VARCHAR(100), " +
                "social_security_number VARCHAR(50), " +
                "address VARCHAR(500), " +
                "phone VARCHAR(50), " +
                "email VARCHAR(255), " +
                "notes TEXT, " +
                "FOREIGN KEY (item_id) REFERENCES vault_items(id) ON DELETE CASCADE)");
            System.out.println("✓ Identity items table created/verified");
        } catch (SQLException e) {
            System.err.println("Error creating identity_items table: " + e.getMessage());
            throw e;
        }
        
        // Secure Note Items table - extends vault_items for text notes
        try {
            stmt.execute("CREATE TABLE IF NOT EXISTS secure_note_items (" +
                "item_id VARCHAR(36) PRIMARY KEY, " +
                "content TEXT, " +
                "FOREIGN KEY (item_id) REFERENCES vault_items(id) ON DELETE CASCADE)");
            System.out.println("✓ Secure note items table created/verified");
        } catch (SQLException e) {
            System.err.println("Error creating secure_note_items table: " + e.getMessage());
            throw e;
        }
        
        // Indexes for query performance
        try {
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_vault_items_user_email ON vault_items(user_email)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_vault_items_type ON vault_items(item_type)");
            System.out.println("✓ Indexes created/verified");
        } catch (SQLException e) {
            System.err.println("Warning: Could not create indexes: " + e.getMessage());
        }
    }
}
