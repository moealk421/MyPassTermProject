package com.mypass;

/**
 * Base interface for UI components in the Mediator pattern.
 */
public interface UIComponent {
    // Handle event from mediator
    void onEvent(String event, Object data);
    
    // Return unique component identifier
    String getComponentId();
}
