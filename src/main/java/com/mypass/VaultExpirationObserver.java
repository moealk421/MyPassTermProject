package com.mypass;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer that collects expiration warnings for UI display.
 */
public class VaultExpirationObserver implements ExpirationObserver {
    private List<String> warnings;
    
    public VaultExpirationObserver() {
        this.warnings = new ArrayList<>();
    }
    
    @Override
    public void onCreditCardExpiring(CreditCardItem card, int daysUntilExpiration) {
        String warning = String.format("Credit card '%s' expires in %d days", 
                                      card.getName(), daysUntilExpiration);
        warnings.add(warning);
    }
    
    @Override
    public void onCreditCardExpired(CreditCardItem card) {
        String warning = String.format("Credit card '%s' has expired", card.getName());
        warnings.add(warning);
    }
    
    @Override
    public void onPassportExpiring(IdentityItem identity, int daysUntilExpiration) {
        String warning = String.format("Passport for '%s' expires in %d days", 
                                      identity.getName(), daysUntilExpiration);
        warnings.add(warning);
    }
    
    @Override
    public void onPassportExpired(IdentityItem identity) {
        String warning = String.format("Passport for '%s' has expired", identity.getName());
        warnings.add(warning);
    }
    
    @Override
    public void onLicenseExpiring(IdentityItem identity, int daysUntilExpiration) {
        String warning = String.format("License for '%s' expires in %d days", 
                                      identity.getName(), daysUntilExpiration);
        warnings.add(warning);
    }
    
    @Override
    public void onLicenseExpired(IdentityItem identity) {
        String warning = String.format("License for '%s' has expired", identity.getName());
        warnings.add(warning);
    }
    
    // Return copy of warnings list
    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    public void clearWarnings() {
        warnings.clear();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
}
