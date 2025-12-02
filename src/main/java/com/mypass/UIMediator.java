package com.mypass;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton mediator that coordinates communication between UI components.
 */
public class UIMediator {
    private static UIMediator instance;
    private List<UIComponent> components;
    
    private UIMediator() {
        this.components = new ArrayList<>();
    }
    
    public static synchronized UIMediator getInstance() {
        if (instance == null) {
            instance = new UIMediator();
        }
        return instance;
    }
    
    // Register component, avoiding duplicates
    public void registerComponent(UIComponent component) {
        if (!components.contains(component)) {
            components.add(component);
        }
    }
    
    public void unregisterComponent(UIComponent component) {
        components.remove(component);
    }
    
    // Broadcast event to all components
    public void notify(String event, Object data) {
        for (UIComponent component : components) {
            component.onEvent(event, data);
        }
    }
    
    // Broadcast event to specific component type only
    public void notify(String event, Object data, Class<? extends UIComponent> componentType) {
        for (UIComponent component : components) {
            if (componentType.isInstance(component)) {
                component.onEvent(event, data);
            }
        }
    }
}
