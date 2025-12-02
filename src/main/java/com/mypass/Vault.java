package com.mypass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory vault that manages vault items with database persistence.
 */
public class Vault {
    private List<VaultItem> items;
    private String userEmail;
    private VaultItemDAO vaultDAO;
    
    // Constructor for transient use
    public Vault() {
        this.items = new ArrayList<>();
        this.vaultDAO = new VaultItemDAO();
    }
    
    // Constructor with user binding - loads items from database
    public Vault(String userEmail) {
        this.userEmail = userEmail;
        this.items = new ArrayList<>();
        this.vaultDAO = new VaultItemDAO();
        loadItemsFromDatabase();
    }
    
    private void loadItemsFromDatabase() {
        if (userEmail != null) {
            this.items = vaultDAO.getAllItems(userEmail);
        }
    }
    
    /**
     * Add item to vault and persist to database.
     */
    public void addItem(VaultItem item) {
        items.add(item);
        if (userEmail != null) {
            vaultDAO.saveItem(userEmail, item);
        }
    }
    
    /**
     * Remove item from vault and database.
     */
    public void removeItem(String id) {
        items.removeIf(item -> item.getId().equals(id));
        if (userEmail != null) {
            vaultDAO.deleteItem(userEmail, id);
        }
    }
    
    /**
     * Get item by ID, loading from database if not in memory.
     */
    public VaultItem getItem(String id) {
        // Check memory first
        VaultItem item = items.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        // Fall back to database
        if (item == null && userEmail != null) {
            item = vaultDAO.getItem(userEmail, id);
            if (item != null) {
                items.add(item); // Cache in memory
            }
        }
        
        return item;
    }
    
    /**
     * Get all items, reloading from database if needed.
     */
    public List<VaultItem> getAllItems() {
        if (userEmail != null && items.isEmpty()) {
            loadItemsFromDatabase();
        }
        return new ArrayList<>(items);
    }
    
    /**
     * Filter items by type.
     */
    public List<VaultItem> getItemsByType(String type) {
        return items.stream()
                .filter(item -> item.getType().equals(type))
                .collect(Collectors.toList());
    }
    
    public int getItemCount() {
        return items.size();
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        loadItemsFromDatabase();
    }
    
    /**
     * Save/update item in database.
     */
    public void saveItem(VaultItem item) {
        if (userEmail != null) {
            vaultDAO.saveItem(userEmail, item);
            // Update in-memory list
            items.removeIf(i -> i.getId().equals(item.getId()));
            items.add(item);
        }
    }
}
