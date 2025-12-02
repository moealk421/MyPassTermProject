package com.mypass;

/**
 * Observer interface for receiving expiration notifications.
 */
public interface ExpirationObserver {
    // Credit card events
    void onCreditCardExpiring(CreditCardItem card, int daysUntilExpiration);
    void onCreditCardExpired(CreditCardItem card);
    
    // Passport events
    void onPassportExpiring(IdentityItem identity, int daysUntilExpiration);
    void onPassportExpired(IdentityItem identity);
    
    // License events
    void onLicenseExpiring(IdentityItem identity, int daysUntilExpiration);
    void onLicenseExpired(IdentityItem identity);
}
