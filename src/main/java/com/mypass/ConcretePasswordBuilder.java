package com.mypass;

import java.security.SecureRandom;

/**
 * Builder pattern implementation for constructing secure passwords.
 */
public class ConcretePasswordBuilder implements PasswordBuilder {
    
    // Character sets for password generation
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    
    // Configuration state
    private int length;
    private boolean includeUppercase;
    private boolean includeLowercase;
    private boolean includeNumbers;
    private boolean includeSpecialChars;
    
    // SecureRandom for cryptographically strong randomness
    private final SecureRandom random;
    
    public ConcretePasswordBuilder() {
        this.random = new SecureRandom();
        reset();
    }
    
    @Override
    public PasswordBuilder setLength(int length) {
        // Enforce minimum length for security
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4");
        }
        // Enforce maximum to prevent abuse
        if (length > 128) {
            throw new IllegalArgumentException("Password length must not exceed 128");
        }
        this.length = length;
        return this;
    }
    
    @Override
    public PasswordBuilder setUppercase(boolean include) {
        this.includeUppercase = include;
        return this;
    }
    
    @Override
    public PasswordBuilder setLowercase(boolean include) {
        this.includeLowercase = include;
        return this;
    }
    
    @Override
    public PasswordBuilder setNumbers(boolean include) {
        this.includeNumbers = include;
        return this;
    }
    
    @Override
    public PasswordBuilder setSpecialChars(boolean include) {
        this.includeSpecialChars = include;
        return this;
    }
    
    @Override
    public Password build() {
        // Build character pool from enabled types
        StringBuilder charset = new StringBuilder();
        if (includeUppercase) charset.append(UPPERCASE);
        if (includeLowercase) charset.append(LOWERCASE);
        if (includeNumbers) charset.append(NUMBERS);
        if (includeSpecialChars) charset.append(SPECIAL);
        
        // At least one character type must be enabled
        if (charset.length() == 0) {
            throw new IllegalStateException("At least one character type must be enabled");
        }
        
        StringBuilder password = new StringBuilder();
        
        // Guarantee at least one character from each enabled type for strength
        if (includeUppercase && length > 0) {
            password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        }
        if (includeLowercase && length > password.length()) {
            password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        }
        if (includeNumbers && length > password.length()) {
            password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        if (includeSpecialChars && length > password.length()) {
            password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));
        }
        
        // Fill remaining positions with random characters
        while (password.length() < length) {
            password.append(charset.charAt(random.nextInt(charset.length())));
        }
        
        // Fisher-Yates shuffle to avoid predictable character positions
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }
        
        String passwordValue = new String(passwordArray);
        
        // Create the Product with final configuration
        return new Password(
            passwordValue,
            length,
            includeUppercase,
            includeLowercase,
            includeNumbers,
            includeSpecialChars
        );
    }
    
    @Override
    public void reset() {
        // Default to strong password configuration
        this.length = 12;
        this.includeUppercase = true;
        this.includeLowercase = true;
        this.includeNumbers = true;
        this.includeSpecialChars = true;
    }
}
