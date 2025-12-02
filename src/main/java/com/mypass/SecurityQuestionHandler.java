package com.mypass;

/**
 * Concrete handler that validates a specific security question answer.
 */
public class SecurityQuestionHandler extends PasswordRecoveryHandler {
    private int questionIndex; // 0, 1, or 2
    
    public SecurityQuestionHandler(int questionIndex) {
        if (questionIndex < 0 || questionIndex > 2) {
            throw new IllegalArgumentException("Question index must be 0, 1, or 2");
        }
        this.questionIndex = questionIndex;
    }
    
    @Override
    protected boolean validate(User user, String[] answers) {
        // Validate inputs
        if (answers == null || questionIndex >= answers.length) {
            return false;
        }
        
        String userAnswer = answers[questionIndex];
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return false;
        }
        
        // Get stored answer for this question
        if (user.getSecurityQuestions() == null || questionIndex >= user.getSecurityQuestions().size()) {
            return false;
        }
        
        String stored = user.getSecurityQuestions().get(questionIndex);
        if (stored == null) {
            return false;
        }
        
        // Extract answer portion from stored value
        String storedAnswer = SecurityQuestionUtil.extractAnswer(stored);
        if (storedAnswer == null) {
            storedAnswer = "";
        }
        
        // Case-insensitive comparison
        return storedAnswer.trim().equalsIgnoreCase(userAnswer.trim());
    }
    
    @Override
    protected int getQuestionIndex() {
        return questionIndex;
    }
}
