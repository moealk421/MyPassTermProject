package com.mypass;

/**
 * Immutable product of the Builder pattern representing a generated password.
 */
public class Password {
    private final String value;           // The generated password string
    private final int length;             // Password length
    private final boolean hasUppercase;   // Contains uppercase letters
    private final boolean hasLowercase;   // Contains lowercase letters
    private final boolean hasNumbers;     // Contains digits
    private final boolean hasSpecialChars;// Contains special characters
    
    // Package-private constructor - only Builder can create instances
    Password(String value, int length, boolean hasUppercase, boolean hasLowercase,
             boolean hasNumbers, boolean hasSpecialChars) {
        this.value = value;
        this.length = length;
        this.hasUppercase = hasUppercase;
        this.hasLowercase = hasLowercase;
        this.hasNumbers = hasNumbers;
        this.hasSpecialChars = hasSpecialChars;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getLength() {
        return length;
    }
    
    public boolean hasUppercase() {
        return hasUppercase;
    }
    
    public boolean hasLowercase() {
        return hasLowercase;
    }
    
    public boolean hasNumbers() {
        return hasNumbers;
    }
    
    public boolean hasSpecialChars() {
        return hasSpecialChars;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
