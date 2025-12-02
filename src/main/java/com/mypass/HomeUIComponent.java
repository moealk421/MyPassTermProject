package com.mypass;

/**
 * Mediator colleague for the home screen.
 */
public class HomeUIComponent implements UIComponent {
    private static final String COMPONENT_ID = "home";
    private UIMediator mediator;
    
    public HomeUIComponent() {
        this.mediator = UIMediator.getInstance();
        this.mediator.registerComponent(this);
    }
    
    @Override
    public void onEvent(String event, Object data) {
        // Handle events from other components
        switch (event) {
            case "login_success":
                handleLoginSuccess(data);
                break;
            case "logout_complete":
                handleLogoutComplete();
                break;
            case "session_expired":
                handleSessionExpired();
                break;
            case "vault_updated":
                handleVaultUpdated(data);
                break;
        }
    }
    
    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
    
    // Notify mediator of navigation request
    public void notifyNavigationRequest(String destination) {
        mediator.notify("navigation_request", destination);
    }
    
    public void notifyOpenVault() {
        mediator.notify("open_vault", null);
    }
    
    public void notifyLogoutRequest() {
        mediator.notify("logout_request", null);
    }
    
    private void handleLoginSuccess(Object data) {
        if (data instanceof User) {
            User user = (User) data;
            System.out.println("[HomeUI] User logged in: " + user.getEmail());
        }
    }
    
    private void handleLogoutComplete() {
        System.out.println("[HomeUI] User logged out, redirecting to login");
    }
    
    private void handleSessionExpired() {
        System.out.println("[HomeUI] Session expired, redirecting to login");
    }
    
    private void handleVaultUpdated(Object data) {
        System.out.println("[HomeUI] Vault updated, refreshing statistics");
    }
    
    // Cleanup when component is destroyed
    public void destroy() {
        mediator.unregisterComponent(this);
    }
}
