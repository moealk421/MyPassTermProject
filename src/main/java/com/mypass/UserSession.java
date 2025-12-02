package com.mypass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton session manager with inactivity-based auto-lock.
 */
public class UserSession {
    private static UserSession instance;
    private Map<String, User> sessions;       // sessionId -> User
    private Map<String, Long> lastActivity;   // sessionId -> timestamp
    private static final long INACTIVITY_TIMEOUT = 5 * 60 * 1000; // 5 minutes
    
    private UserSession() {
        this.sessions = new ConcurrentHashMap<>();
        this.lastActivity = new ConcurrentHashMap<>();
    }
    
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    /**
     * Register user login and start activity tracking.
     */
    public void login(String sessionId, User user) {
        sessions.put(sessionId, user);
        updateActivity(sessionId);
    }
    
    /**
     * Get logged-in user, updating activity timestamp.
     * Returns null if session is invalid or timed out.
     */
    public User getLoggedUser(String sessionId) {
        if (!isLoggedIn(sessionId)) {
            return null;
        }
        updateActivity(sessionId);
        return sessions.get(sessionId);
    }
    
    /**
     * Check if session is valid and not timed out.
     * Auto-logs out if inactivity exceeded.
     */
    public boolean isLoggedIn(String sessionId) {
        if (!sessions.containsKey(sessionId) || sessions.get(sessionId) == null) {
            return false;
        }
        
        // Check inactivity timeout
        Long lastActive = lastActivity.get(sessionId);
        if (lastActive != null) {
            long timeSinceActivity = System.currentTimeMillis() - lastActive;
            if (timeSinceActivity > INACTIVITY_TIMEOUT) {
                // Auto-lock: session expired due to inactivity
                logout(sessionId);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Update activity timestamp for session.
     */
    public void updateActivity(String sessionId) {
        lastActivity.put(sessionId, System.currentTimeMillis());
    }
    
    /**
     * Remove session on logout.
     */
    public void logout(String sessionId) {
        sessions.remove(sessionId);
        lastActivity.remove(sessionId);
    }
    
    public User getUserByEmail(String email) {
        return UserService.getInstance().getUserByEmail(email);
    }
    
    public long getInactivityTimeout() {
        return INACTIVITY_TIMEOUT;
    }
}
