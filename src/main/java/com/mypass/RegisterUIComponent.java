package com.mypass;

/**
 * Mediator colleague for the registration screen.
 */
public class RegisterUIComponent implements UIComponent {
    private static final String COMPONENT_ID = "register";
    private UIMediator mediator;
    
    public RegisterUIComponent() {
        this.mediator = UIMediator.getInstance();
        this.mediator.registerComponent(this);
    }
    
    @Override
    public void onEvent(String event, Object data) {
        switch (event) {
            case "login_success":
                handleLoginSuccess();
                break;
            case "email_already_exists":
                handleEmailExists(data);
                break;
        }
    }
    
    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
    
    // Notify mediator of registration result
    public void notifyRegistrationSuccess(User user) {
        mediator.notify("registration_success", user);
    }
    
    public void notifyRegistrationFailed(String reason) {
        mediator.notify("registration_failed", reason);
    }
    
    public void notifyNavigateToLogin() {
        mediator.notify("navigate_to_login", null);
    }
    
    private void handleLoginSuccess() {
        System.out.println("[RegisterUI] User already logged in, redirecting away from registration");
    }
    
    private void handleEmailExists(Object data) {
        System.out.println("[RegisterUI] Email already exists: " + data);
    }
    
    public void destroy() {
        mediator.unregisterComponent(this);
    }
}
