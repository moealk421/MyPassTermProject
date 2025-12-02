package com.mypass;

/**
 * RealSubject in the Proxy pattern. Stores actual sensitive data
 * and manages masking state.
 */
public class RealSensitiveData implements SensitiveData {
    
    private final String actualValue;   // The real sensitive value
    private final String maskedValue;   // Pre-computed masked representation
    private boolean masked;             // Current display state
    
    private static final String MASK_CHAR = "*";
    
    public RealSensitiveData(String actualValue) {
        this.actualValue = actualValue != null ? actualValue : "";
        this.maskedValue = createMaskedValue(this.actualValue);
        this.masked = true; // Start masked for security
    }
    
    // Create masked version (all characters replaced with asterisks)
    private String createMaskedValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return MASK_CHAR.repeat(value.length());
    }
    
    @Override
    public String getDisplayValue() {
        return masked ? maskedValue : actualValue;
    }
    
    @Override
    public String getActualValue() {
        return actualValue;
    }
    
    @Override
    public void mask() {
        this.masked = true;
    }
    
    @Override
    public void unmask() {
        this.masked = false;
    }
    
    @Override
    public boolean isMasked() {
        return masked;
    }
    
    public String getMaskedValue() {
        return maskedValue;
    }
}
