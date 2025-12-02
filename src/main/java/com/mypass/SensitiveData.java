package com.mypass;

/**
 * Subject interface for the Proxy pattern.
 * Defines operations for accessing and controlling sensitive data display.
 */
public interface SensitiveData {
    // Get value for display (masked or actual based on state)
    String getDisplayValue();
    
    // Get the actual unmasked value
    String getActualValue();
    
    // Hide the data
    void mask();
    
    // Reveal the data
    void unmask();
    
    // Check current visibility state
    boolean isMasked();
}
