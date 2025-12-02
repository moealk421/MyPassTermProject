package com.mypass;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO for vault item persistence across metadata and type-specific tables.
 */
public class VaultItemDAO {
    private DatabaseConnection dbConnection;
    
    public VaultItemDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
     * Save or update a vault item.
     */
    public boolean saveItem(String userEmail, VaultItem item) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Check if item exists
            String checkSql = "SELECT COUNT(*) FROM vault_items WHERE id = ?";
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, item.getId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    exists = true;
                }
            }
            
            if (exists) {
                // Update metadata
                String updateSql = "UPDATE vault_items SET name = ?, modified_date = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setString(1, item.getName());
                    pstmt.setTimestamp(2, new Timestamp(item.getModifiedDate().getTime()));
                    pstmt.setString(3, item.getId());
                    pstmt.executeUpdate();
                }
            } else {
                // Insert new metadata
                String insertSql = "INSERT INTO vault_items (id, user_email, item_type, name, created_date, modified_date) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, item.getId());
                    pstmt.setString(2, userEmail);
                    pstmt.setString(3, item.getType());
                    pstmt.setString(4, item.getName());
                    pstmt.setTimestamp(5, new Timestamp(item.getCreatedDate().getTime()));
                    pstmt.setTimestamp(6, new Timestamp(item.getModifiedDate().getTime()));
                    pstmt.executeUpdate();
                }
            }
            
            // Save type-specific data
            if (item instanceof LoginItem) {
                saveLoginItem(conn, (LoginItem) item);
            } else if (item instanceof CreditCardItem) {
                saveCreditCardItem(conn, (CreditCardItem) item);
            } else if (item instanceof IdentityItem) {
                saveIdentityItem(conn, (IdentityItem) item);
            } else if (item instanceof SecureNoteItem) {
                saveSecureNoteItem(conn, (SecureNoteItem) item);
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            // Rollback on error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Type-specific save methods (delete then insert for simplicity)
    
    private void saveLoginItem(Connection conn, LoginItem item) throws SQLException {
        String deleteSql = "DELETE FROM login_items WHERE item_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, item.getId());
            pstmt.executeUpdate();
        }
        
        String sql = "INSERT INTO login_items (item_id, username, password, url, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getUsername());
            pstmt.setString(3, item.getPassword());
            pstmt.setString(4, item.getUrl());
            pstmt.setString(5, item.getNotes());
            pstmt.executeUpdate();
        }
    }
    
    private void saveCreditCardItem(Connection conn, CreditCardItem item) throws SQLException {
        String deleteSql = "DELETE FROM credit_card_items WHERE item_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, item.getId());
            pstmt.executeUpdate();
        }
        
        String sql = "INSERT INTO credit_card_items (item_id, card_number, cardholder_name, cvv, expiration_date, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getCardNumber());
            pstmt.setString(3, item.getCardholderName());
            pstmt.setString(4, item.getCvv());
            if (item.getExpirationDate() != null) {
                pstmt.setDate(5, new java.sql.Date(item.getExpirationDate().getTime()));
            } else {
                pstmt.setDate(5, null);
            }
            pstmt.setString(6, item.getNotes());
            pstmt.executeUpdate();
        }
    }
    
    private void saveIdentityItem(Connection conn, IdentityItem item) throws SQLException {
        String deleteSql = "DELETE FROM identity_items WHERE item_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, item.getId());
            pstmt.executeUpdate();
        }
        
        String sql = "INSERT INTO identity_items (item_id, first_name, last_name, passport_number, license_number, " +
                     "social_security_number, address, phone, email, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getFirstName());
            pstmt.setString(3, item.getLastName());
            pstmt.setString(4, item.getPassportNumber());
            pstmt.setString(5, item.getLicenseNumber());
            pstmt.setString(6, item.getSocialSecurityNumber());
            pstmt.setString(7, item.getAddress());
            pstmt.setString(8, item.getPhone());
            pstmt.setString(9, item.getEmail());
            pstmt.setString(10, item.getNotes());
            pstmt.executeUpdate();
        }
    }
    
    private void saveSecureNoteItem(Connection conn, SecureNoteItem item) throws SQLException {
        String deleteSql = "DELETE FROM secure_note_items WHERE item_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, item.getId());
            pstmt.executeUpdate();
        }
        
        String sql = "INSERT INTO secure_note_items (item_id, content) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getId());
            pstmt.setString(2, item.getContent());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Get all vault items for a user.
     */
    public List<VaultItem> getAllItems(String userEmail) {
        List<VaultItem> items = new ArrayList<>();
        String sql = "SELECT id, item_type, name, created_date, modified_date FROM vault_items WHERE user_email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userEmail);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String itemId = rs.getString("id");
                String itemType = rs.getString("item_type");
                VaultItem item = loadItemByType(conn, itemId, itemType, rs);
                if (item != null) {
                    items.add(item);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    /**
     * Get a single vault item by ID.
     */
    public VaultItem getItem(String userEmail, String itemId) {
        String sql = "SELECT id, item_type, name, created_date, modified_date FROM vault_items WHERE id = ? AND user_email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemId);
            pstmt.setString(2, userEmail);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String itemType = rs.getString("item_type");
                return loadItemByType(conn, itemId, itemType, rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Route to type-specific loader
    private VaultItem loadItemByType(Connection conn, String itemId, String itemType, ResultSet vaultRs) throws SQLException {
        VaultItem item = null;
        
        if ("Login".equals(itemType)) {
            item = loadLoginItem(conn, itemId, vaultRs);
        } else if ("Credit Card".equals(itemType)) {
            item = loadCreditCardItem(conn, itemId, vaultRs);
        } else if ("Identity".equals(itemType)) {
            item = loadIdentityItem(conn, itemId, vaultRs);
        } else if ("Secure Note".equals(itemType)) {
            item = loadSecureNoteItem(conn, itemId, vaultRs);
        }
        
        return item;
    }
    
    private LoginItem loadLoginItem(Connection conn, String itemId, ResultSet vaultRs) throws SQLException {
        String sql = "SELECT username, password, url, notes FROM login_items WHERE item_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                LoginItem item = new LoginItem(
                    vaultRs.getString("name"),
                    rs.getString("username") != null ? rs.getString("username") : "",
                    rs.getString("password") != null ? rs.getString("password") : "",
                    rs.getString("url") != null ? rs.getString("url") : ""
                );
                item.setId(itemId);
                item.setCreatedDate(new Date(vaultRs.getTimestamp("created_date").getTime()));
                item.setModifiedDate(new Date(vaultRs.getTimestamp("modified_date").getTime()));
                if (rs.getString("notes") != null) {
                    item.setNotes(rs.getString("notes"));
                }
                return item;
            }
        }
        
        return null;
    }
    
    private CreditCardItem loadCreditCardItem(Connection conn, String itemId, ResultSet vaultRs) throws SQLException {
        String sql = "SELECT card_number, cardholder_name, cvv, expiration_date, notes FROM credit_card_items WHERE item_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Date expDate = rs.getDate("expiration_date") != null ? 
                    new Date(rs.getDate("expiration_date").getTime()) : new Date();
                
                CreditCardItem item = new CreditCardItem(
                    vaultRs.getString("name"),
                    rs.getString("card_number") != null ? rs.getString("card_number") : "",
                    rs.getString("cardholder_name") != null ? rs.getString("cardholder_name") : "",
                    rs.getString("cvv") != null ? rs.getString("cvv") : "",
                    expDate
                );
                item.setId(itemId);
                item.setCreatedDate(new Date(vaultRs.getTimestamp("created_date").getTime()));
                item.setModifiedDate(new Date(vaultRs.getTimestamp("modified_date").getTime()));
                if (rs.getString("notes") != null) {
                    item.setNotes(rs.getString("notes"));
                }
                return item;
            }
        }
        
        return null;
    }
    
    private IdentityItem loadIdentityItem(Connection conn, String itemId, ResultSet vaultRs) throws SQLException {
        String sql = "SELECT first_name, last_name, passport_number, license_number, social_security_number, " +
                     "address, phone, email, notes FROM identity_items WHERE item_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                IdentityItem item = new IdentityItem(
                    vaultRs.getString("name"),
                    rs.getString("first_name") != null ? rs.getString("first_name") : "",
                    rs.getString("last_name") != null ? rs.getString("last_name") : ""
                );
                item.setId(itemId);
                item.setCreatedDate(new Date(vaultRs.getTimestamp("created_date").getTime()));
                item.setModifiedDate(new Date(vaultRs.getTimestamp("modified_date").getTime()));
                if (rs.getString("passport_number") != null) item.setPassportNumber(rs.getString("passport_number"));
                if (rs.getString("license_number") != null) item.setLicenseNumber(rs.getString("license_number"));
                if (rs.getString("social_security_number") != null) item.setSocialSecurityNumber(rs.getString("social_security_number"));
                if (rs.getString("address") != null) item.setAddress(rs.getString("address"));
                if (rs.getString("phone") != null) item.setPhone(rs.getString("phone"));
                if (rs.getString("email") != null) item.setEmail(rs.getString("email"));
                if (rs.getString("notes") != null) item.setNotes(rs.getString("notes"));
                return item;
            }
        }
        
        return null;
    }
    
    private SecureNoteItem loadSecureNoteItem(Connection conn, String itemId, ResultSet vaultRs) throws SQLException {
        String sql = "SELECT content FROM secure_note_items WHERE item_id = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                SecureNoteItem item = new SecureNoteItem(
                    vaultRs.getString("name"),
                    rs.getString("content") != null ? rs.getString("content") : ""
                );
                item.setId(itemId);
                item.setCreatedDate(new Date(vaultRs.getTimestamp("created_date").getTime()));
                item.setModifiedDate(new Date(vaultRs.getTimestamp("modified_date").getTime()));
                return item;
            }
        }
        
        return null;
    }
    
    /**
     * Delete a vault item (cascades to type-specific table).
     */
    public boolean deleteItem(String userEmail, String itemId) {
        String sql = "DELETE FROM vault_items WHERE id = ? AND user_email = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, itemId);
            pstmt.setString(2, userEmail);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
