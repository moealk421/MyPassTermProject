package com.mypass;

/**
 * Director in the Builder pattern. Orchestrates password construction.
 */
public class PasswordDirector {
    
    private PasswordBuilder builder;
    
    public PasswordDirector(PasswordBuilder builder) {
        this.builder = builder;
    }
    
    public void setBuilder(PasswordBuilder builder) {
        this.builder = builder;
    }
    
    public PasswordBuilder getBuilder() {
        return builder;
    }
    
    /**
     * Builds a strong password with all character types enabled.
     */
    public Password buildStrongPassword(int length) {
        builder.reset();
        return builder
            .setLength(length)
            .setUppercase(true)
            .setLowercase(true)
            .setNumbers(true)
            .setSpecialChars(true)
            .build();
    }
}
