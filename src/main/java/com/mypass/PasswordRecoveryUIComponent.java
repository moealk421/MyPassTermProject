package com.mypass;

/**
 * Mediator colleague for the password recovery screen.
 */
public class PasswordRecoveryUIComponent implements UIComponent {
    private static final String COMPONENT_ID = "password_recovery";
    private UIMediator mediator;
    
    public PasswordRecoveryUIComponent() {
        this.mediator = UIMediator.getInstance();
        this.mediator.registerComponent(this);
    }
    
    @Override
    public void onEvent(String event, Object data) {
        switch (event) {
            case "login_success":
                handleLoginSuccess();
                break;
            case "user_not_found":
                handleUserNotFound(data);
                break;
        }
    }
    
    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
    
    // Notify mediator of recovery progress
    public void notifyRecoveryInitiated(String email) {
        mediator.notify("recovery_initiated", email);
    }
    
    public void notifySecurityQuestionVerified(String email) {
        mediator.notify("security_verified", email);
    }
    
    public void notifyPasswordResetComplete(String email) {
        mediator.notify("password_reset_complete", email);
    }
    
    public void notifyRecoveryFailed(String reason) {
        mediator.notify("recovery_failed", reason);
    }
    
    public void notifyNavigateToLogin() {
        mediator.notify("navigate_to_login", null);
    }
    
    private void handleLoginSuccess() {
        System.out.println("[PasswordRecoveryUI] User already logged in, redirecting away");
    }
    
    private void handleUserNotFound(Object data) {
        System.out.println("[PasswordRecoveryUI] User not found: " + data);
    }
    
    public void destroy() {
        mediator.unregisterComponent(this);
    }
}
