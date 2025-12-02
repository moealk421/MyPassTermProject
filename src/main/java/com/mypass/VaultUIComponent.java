package com.mypass;

/**
 * Mediator colleague for the vault screen.
 */
public class VaultUIComponent implements UIComponent {
    private static final String COMPONENT_ID = "vault";
    private UIMediator mediator;
    
    public VaultUIComponent() {
        this.mediator = UIMediator.getInstance();
        this.mediator.registerComponent(this);
    }
    
    @Override
    public void onEvent(String event, Object data) {
        switch (event) {
            case "login_success":
                handleLoginSuccess(data);
                break;
            case "session_expired":
                handleSessionExpired();
                break;
            case "logout_request":
                handleLogoutRequest();
                break;
            case "item_added":
                handleItemAdded(data);
                break;
            case "item_updated":
                handleItemUpdated(data);
                break;
            case "item_deleted":
                handleItemDeleted(data);
                break;
            case "expiration_warning":
                handleExpirationWarning(data);
                break;
        }
    }
    
    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
    
    // Notify mediator of item changes
    public void notifyItemAdded(VaultItem item) {
        mediator.notify("item_added", item);
        mediator.notify("vault_updated", item);
    }
    
    public void notifyItemUpdated(VaultItem item) {
        mediator.notify("item_updated", item);
        mediator.notify("vault_updated", item);
    }
    
    public void notifyItemDeleted(String itemId) {
        mediator.notify("item_deleted", itemId);
        mediator.notify("vault_updated", itemId);
    }
    
    public void notifyCopyRequest(String data, String fieldType) {
        mediator.notify("copy_request", new CopyRequest(data, fieldType));
    }
    
    public void notifyMaskToggle(String itemId, String field, boolean mask) {
        mediator.notify("mask_toggle", new MaskToggle(itemId, field, mask));
    }
    
    public void notifyExpirationWarning(String message) {
        mediator.notify("expiration_warning", message);
    }
    
    private void handleLoginSuccess(Object data) {
        if (data instanceof User) {
            User user = (User) data;
            System.out.println("[VaultUI] Loading vault for user: " + user.getEmail());
        }
    }
    
    private void handleSessionExpired() {
        System.out.println("[VaultUI] Session expired, locking vault");
    }
    
    private void handleLogoutRequest() {
        System.out.println("[VaultUI] Logout requested, clearing vault display");
    }
    
    private void handleItemAdded(Object data) {
        if (data instanceof VaultItem) {
            VaultItem item = (VaultItem) data;
            System.out.println("[VaultUI] Item added: " + item.getName());
        }
    }
    
    private void handleItemUpdated(Object data) {
        if (data instanceof VaultItem) {
            VaultItem item = (VaultItem) data;
            System.out.println("[VaultUI] Item updated: " + item.getName());
        }
    }
    
    private void handleItemDeleted(Object data) {
        System.out.println("[VaultUI] Item deleted: " + data);
    }
    
    private void handleExpirationWarning(Object data) {
        System.out.println("[VaultUI] Expiration warning: " + data);
    }
    
    public void destroy() {
        mediator.unregisterComponent(this);
    }
    
    // Helper classes for event data
    public static class CopyRequest {
        public String data;
        public String fieldType;
        
        public CopyRequest(String data, String fieldType) {
            this.data = data;
            this.fieldType = fieldType;
        }
    }
    
    public static class MaskToggle {
        public String itemId;
        public String field;
        public boolean mask;
        
        public MaskToggle(String itemId, String field, boolean mask) {
            this.itemId = itemId;
            this.field = field;
            this.mask = mask;
        }
    }
}
