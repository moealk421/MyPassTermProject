package com.mypass;

/**
 * Mediator colleague for the login screen.
 */
public class LoginUIComponent implements UIComponent {
    private static final String COMPONENT_ID = "login";
    private UIMediator mediator;
    
    public LoginUIComponent() {
        this.mediator = UIMediator.getInstance();
        this.mediator.registerComponent(this);
    }
    
    @Override
    public void onEvent(String event, Object data) {
        // Handle events from other components
        switch (event) {
            case "session_expired":
                handleSessionExpired();
                break;
            case "logout_complete":
                handleLogoutComplete();
                break;
            case "registration_success":
                handleRegistrationSuccess(data);
                break;
            case "password_reset_complete":
                handlePasswordResetComplete();
                break;
        }
    }
    
    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
    
    // Notify mediator of login result
    public void notifyLoginSuccess(User user) {
        mediator.notify("login_success", user);
    }
    
    public void notifyLoginFailed(String reason) {
        mediator.notify("login_failed", reason);
    }
    
    private void handleSessionExpired() {
        System.out.println("[LoginUI] Session expired, user redirected to login");
    }
    
    private void handleLogoutComplete() {
        System.out.println("[LoginUI] Logout complete, ready for new login");
    }
    
    private void handleRegistrationSuccess(Object data) {
        System.out.println("[LoginUI] Registration successful, user can now login");
    }
    
    private void handlePasswordResetComplete() {
        System.out.println("[LoginUI] Password reset complete, user can login with new password");
    }
    
    public void destroy() {
        mediator.unregisterComponent(this);
    }
}
