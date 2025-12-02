package com.mypass;

/**
 * Builder interface for configuring and constructing Password objects.
 * Supports fluent method chaining.
 */
public interface PasswordBuilder {
    // Configuration methods - return this for chaining
    PasswordBuilder setLength(int length);
    PasswordBuilder setUppercase(boolean include);
    PasswordBuilder setLowercase(boolean include);
    PasswordBuilder setNumbers(boolean include);
    PasswordBuilder setSpecialChars(boolean include);
    
    // Build the final Password product
    Password build();
    
    // Reset builder to initial state for reuse
    void reset();
}
