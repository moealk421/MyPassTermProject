package com.mypass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Checks vault items for expiration and notifies registered observers.
 * Uses Observer pattern for decoupled notification handling.
 */
public class ExpirationChecker {
    private List<ExpirationObserver> observers;
    private static final int WARNING_DAYS = 30; // Days before expiration to warn
    
    public ExpirationChecker() {
        this.observers = new ArrayList<>();
    }
    
    // Register observer, avoiding duplicates
    public void addObserver(ExpirationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    public void removeObserver(ExpirationObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Scans all items in the vault for expiration.
     */
    public void checkVault(Vault vault) {
        if (vault == null) {
            return;
        }
        
        // Route each item to its type-specific checker
        for (VaultItem item : vault.getAllItems()) {
            if (item instanceof CreditCardItem) {
                checkCreditCardExpiration((CreditCardItem) item);
            } else if (item instanceof IdentityItem) {
                checkIdentityExpiration((IdentityItem) item);
            }
        }
    }
    
    private void checkCreditCardExpiration(CreditCardItem card) {
        if (card.getExpirationDate() == null) {
            return;
        }
        
        // Calculate days until expiration
        Date expirationDate = card.getExpirationDate();
        Date now = new Date();
        long diffInMillis = expirationDate.getTime() - now.getTime();
        int daysUntilExpiration = (int) (diffInMillis / (1000 * 60 * 60 * 24));
        
        // Notify based on expiration status
        if (daysUntilExpiration < 0) {
            notifyCreditCardExpired(card);
        } else if (daysUntilExpiration <= WARNING_DAYS) {
            notifyCreditCardExpiring(card, daysUntilExpiration);
        }
    }
    
    private void checkIdentityExpiration(IdentityItem identity) {
        // Check passport expiration
        if (identity.getPassportExpirationDate() != null) {
            Date expirationDate = identity.getPassportExpirationDate();
            Date now = new Date();
            long diffInMillis = expirationDate.getTime() - now.getTime();
            int daysUntilExpiration = (int) (diffInMillis / (1000 * 60 * 60 * 24));
            
            if (daysUntilExpiration < 0) {
                notifyPassportExpired(identity);
            } else if (daysUntilExpiration <= WARNING_DAYS) {
                notifyPassportExpiring(identity, daysUntilExpiration);
            }
        }
        
        // Check driver license expiration
        if (identity.getLicenseExpirationDate() != null) {
            Date expirationDate = identity.getLicenseExpirationDate();
            Date now = new Date();
            long diffInMillis = expirationDate.getTime() - now.getTime();
            int daysUntilExpiration = (int) (diffInMillis / (1000 * 60 * 60 * 24));
            
            if (daysUntilExpiration < 0) {
                notifyLicenseExpired(identity);
            } else if (daysUntilExpiration <= WARNING_DAYS) {
                notifyLicenseExpiring(identity, daysUntilExpiration);
            }
        }
    }
    
    // Broadcast notifications to all observers
    private void notifyCreditCardExpiring(CreditCardItem card, int daysUntilExpiration) {
        for (ExpirationObserver observer : observers) {
            observer.onCreditCardExpiring(card, daysUntilExpiration);
        }
    }
    
    private void notifyCreditCardExpired(CreditCardItem card) {
        for (ExpirationObserver observer : observers) {
            observer.onCreditCardExpired(card);
        }
    }
    
    private void notifyPassportExpiring(IdentityItem identity, int daysUntilExpiration) {
        for (ExpirationObserver observer : observers) {
            observer.onPassportExpiring(identity, daysUntilExpiration);
        }
    }
    
    private void notifyPassportExpired(IdentityItem identity) {
        for (ExpirationObserver observer : observers) {
            observer.onPassportExpired(identity);
        }
    }
    
    private void notifyLicenseExpiring(IdentityItem identity, int daysUntilExpiration) {
        for (ExpirationObserver observer : observers) {
            observer.onLicenseExpiring(identity, daysUntilExpiration);
        }
    }
    
    private void notifyLicenseExpired(IdentityItem identity) {
        for (ExpirationObserver observer : observers) {
            observer.onLicenseExpired(identity);
        }
    }
}
