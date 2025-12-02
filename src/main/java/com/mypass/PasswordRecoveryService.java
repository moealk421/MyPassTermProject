package com.mypass;

/**
 * Singleton service for password recovery using Chain of Responsibility.
 */
public class PasswordRecoveryService {
    private static PasswordRecoveryService instance;
    
    private PasswordRecoveryService() {}
    
    public static synchronized PasswordRecoveryService getInstance() {
        if (instance == null) {
            instance = new PasswordRecoveryService();
        }
        return instance;
    }
    
    /**
     * Verifies all security questions and resets password if valid.
     */
    public boolean recoverPassword(User user, String[] answers, String newPassword) {
        if (user == null || answers == null || answers.length != 3) {
            return false;
        }
        
        // Build chain of handlers for each security question
        PasswordRecoveryHandler handler1 = new SecurityQuestionHandler(0);
        PasswordRecoveryHandler handler2 = new SecurityQuestionHandler(1);
        PasswordRecoveryHandler handler3 = new SecurityQuestionHandler(2);
        
        handler1.setNext(handler2);
        handler2.setNext(handler3);
        
        // Process through chain
        boolean allValid = handler1.handle(user, answers);
        
        // If all questions passed, update password
        if (allValid && newPassword != null && !newPassword.trim().isEmpty()) {
            return UserService.getInstance().updatePassword(user.getEmail(), newPassword);
        }
        
        return false;
    }
    
    /**
     * Verifies security questions without changing password.
     */
    public boolean verifySecurityQuestions(User user, String[] answers) {
        if (user == null || answers == null || answers.length != 3) {
            return false;
        }
        
        // Build and process chain
        PasswordRecoveryHandler handler1 = new SecurityQuestionHandler(0);
        PasswordRecoveryHandler handler2 = new SecurityQuestionHandler(1);
        PasswordRecoveryHandler handler3 = new SecurityQuestionHandler(2);
        
        handler1.setNext(handler2);
        handler2.setNext(handler3);
        
        return handler1.handle(user, answers);
    }
}
