package com.mypass;

import java.util.Date;

/**
 * Vault item for storing personal identity documents and contact info.
 */
public class IdentityItem extends VaultItem {
    // Personal details
    private String firstName;
    private String lastName;
    
    // Document numbers (sensitive)
    private String passportNumber;
    private String licenseNumber;
    private String socialSecurityNumber;
    
    // Document expiration dates
    private Date passportExpirationDate;
    private Date licenseExpirationDate;
    
    // Contact information
    private String address;
    private String phone;
    private String email;
    private String notes;
    
    public IdentityItem(String name, String firstName, String lastName) {
        super(name);
        this.firstName = firstName;
        this.lastName = lastName;
        this.notes = "";
    }
    
    // All setters update modifiedDate to track changes
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.modifiedDate = new Date();
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.modifiedDate = new Date();
    }
    
    public String getPassportNumber() {
        return passportNumber;
    }
    
    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
        this.modifiedDate = new Date();
    }
    
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        this.modifiedDate = new Date();
    }
    
    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }
    
    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
        this.modifiedDate = new Date();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
        this.modifiedDate = new Date();
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        this.modifiedDate = new Date();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.modifiedDate = new Date();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
        this.modifiedDate = new Date();
    }
    
    public Date getPassportExpirationDate() {
        return passportExpirationDate;
    }
    
    public void setPassportExpirationDate(Date passportExpirationDate) {
        this.passportExpirationDate = passportExpirationDate;
        this.modifiedDate = new Date();
    }
    
    public Date getLicenseExpirationDate() {
        return licenseExpirationDate;
    }
    
    public void setLicenseExpirationDate(Date licenseExpirationDate) {
        this.licenseExpirationDate = licenseExpirationDate;
        this.modifiedDate = new Date();
    }
    
    @Override
    public String getType() {
        return "Identity";
    }
}
