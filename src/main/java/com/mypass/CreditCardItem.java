package com.mypass;

import java.util.Date;

/**
 * Vault item for storing credit card information.
 */
public class CreditCardItem extends VaultItem {
    private String cardNumber;      // Full card number (sensitive)
    private String cardholderName;  // Name on card
    private String cvv;             // Card verification value (sensitive)
    private Date expirationDate;    // Card expiration
    private String notes;           // User notes
    
    public CreditCardItem(String name, String cardNumber, String cardholderName, String cvv, Date expirationDate) {
        super(name);
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.cvv = cvv;
        this.expirationDate = expirationDate;
        this.notes = "";
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    // All setters update modifiedDate to track changes
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        this.modifiedDate = new Date();
    }
    
    public String getCardholderName() {
        return cardholderName;
    }
    
    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
        this.modifiedDate = new Date();
    }
    
    public String getCvv() {
        return cvv;
    }
    
    public void setCvv(String cvv) {
        this.cvv = cvv;
        this.modifiedDate = new Date();
    }
    
    public Date getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
        this.modifiedDate = new Date();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.modifiedDate = new Date();
    }
    
    @Override
    public String getType() {
        return "Credit Card";
    }
}
