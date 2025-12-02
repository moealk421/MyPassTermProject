package com.mypass;

/**
 * Abstract handler in the Chain of Responsibility pattern for password recovery.
 * Each handler validates one security question.
 */
public abstract class PasswordRecoveryHandler {
    protected PasswordRecoveryHandler nextHandler;
    
    // Link to next handler in chain
    public void setNext(PasswordRecoveryHandler handler) {
        this.nextHandler = handler;
    }
    
    /**
     * Process through the chain. Returns true only if this handler
     * and all subsequent handlers pass validation.
     */
    public boolean handle(User user, String[] answers) {
        if (validate(user, answers)) {
            // This handler passed, check next in chain
            if (nextHandler != null) {
                return nextHandler.handle(user, answers);
            }
            return true; // End of chain, all passed
        }
        return false; // This handler failed
    }
    
    // Validate this handler's specific question
    protected abstract boolean validate(User user, String[] answers);
    
    // Get the question index (0, 1, or 2)
    protected abstract int getQuestionIndex();
}
