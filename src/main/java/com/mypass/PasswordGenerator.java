package com.mypass;

/**
 * Facade for password generation using the Builder pattern.
 */
public class PasswordGenerator {

    private final PasswordBuilder builder;
    private final PasswordDirector director;

    public PasswordGenerator() {
        this.builder = new ConcretePasswordBuilder();
        this.director = new PasswordDirector(builder);
    }

    // Access to builder for custom configuration
    public PasswordBuilder getBuilder() {
        return builder;
    }

    public PasswordDirector getDirector() {
        return director;
    }

    // Convenience method for generating strong passwords
    public Password generateStrong(int length) {
        return director.buildStrongPassword(length);
    }
}
