package com.mypass;

/**
 * Proxy for sensitive data with lazy initialization and access logging.
 */
public class SensitiveDataProxy implements SensitiveData {
    
    private RealSensitiveData realSubject;
    private final String actualValue;
    private boolean initialized;
    
    private static final String MASK_CHAR = "*";
    
    public SensitiveDataProxy(String actualValue) {
        this.actualValue = actualValue != null ? actualValue : "";
        this.initialized = false;
        this.realSubject = null;
    }
    
    // Lazy initialization - create RealSubject on first access
    private void ensureInitialized() {
        if (!initialized) {
            realSubject = new RealSensitiveData(actualValue);
            initialized = true;
        }
    }
    
    @Override
    public String getDisplayValue() {
        ensureInitialized();
        return realSubject.getDisplayValue();
    }
    
    @Override
    public String getActualValue() {
        ensureInitialized();
        logAccess("getActualValue");
        return realSubject.getActualValue();
    }
    
    @Override
    public void mask() {
        ensureInitialized();
        realSubject.mask();
    }
    
    @Override
    public void unmask() {
        ensureInitialized();
        logAccess("unmask");
        realSubject.unmask();
    }
    
    @Override
    public boolean isMasked() {
        ensureInitialized();
        return realSubject.isMasked();
    }
    
    // Hook for security audit logging
    private void logAccess(String operation) {
        // Reserved for audit trail implementation
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    // Static utility for one-time masking without proxy overhead
    public static String maskStatic(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return MASK_CHAR.repeat(value.length());
    }
    
    // Factory methods
    public static SensitiveData create(String value) {
        return new SensitiveDataProxy(value);
    }
    
    public static SensitiveData createReal(String value) {
        return new RealSensitiveData(value);
    }
}
