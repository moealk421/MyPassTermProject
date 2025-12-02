package com.mypass;

/**
 * Observer interface for password strength events.
 */
public interface PasswordObserver {
    // Called when password is weak
    void onWeakPassword(String message);
    
    // Called when strength is determined
    void onPasswordStrengthChange(String strength);
}
