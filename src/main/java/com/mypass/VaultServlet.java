package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles add/update/delete operations for vault items.
 */
@WebServlet("/vault")
public class VaultServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Validate session
        String sessionId = request.getSession().getId();
        UserSession userSession = UserSession.getInstance();
        User user = userSession.getLoggedUser(sessionId);
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        Vault vault = user.getVault();
        
        if ("add".equals(action)) {
            String type = request.getParameter("type");
            String name = request.getParameter("name");
            
            if (type == null || name == null) {
                response.sendRedirect("vault.jsp?error=missing_fields");
                return;
            }
            
            VaultItem item = null;
            
            // Normalize type from form
            String actualType = type;
            if ("CreditCard".equals(type)) {
                actualType = "Credit Card";
            } else if ("SecureNote".equals(type)) {
                actualType = "Secure Note";
            }
            
            // Create item based on type
            switch (actualType) {
                case "Login":
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");
                    String url = request.getParameter("url");
                    String loginNotes = request.getParameter("notes");
                    item = new LoginItem(name, username != null ? username : "", 
                                        password != null ? password : "", 
                                        url != null ? url : "");
                    if (loginNotes != null) {
                        ((LoginItem) item).setNotes(loginNotes);
                    }
                    break;
                    
                case "Credit Card":
                    String cardNumber = request.getParameter("cardNumber");
                    String cardholderName = request.getParameter("cardholderName");
                    String cvv = request.getParameter("cvv");
                    String expDateStr = request.getParameter("expirationDate");
                    String cardNotes = request.getParameter("notes");
                    Date expDate = null;
                    try {
                        if (expDateStr != null && !expDateStr.isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            expDate = sdf.parse(expDateStr);
                        }
                    } catch (Exception e) {
                        expDate = new Date();
                    }
                    if (expDate == null) expDate = new Date();
                    item = new CreditCardItem(name, 
                                            cardNumber != null ? cardNumber : "",
                                            cardholderName != null ? cardholderName : "",
                                            cvv != null ? cvv : "",
                                            expDate);
                    if (cardNotes != null) {
                        ((CreditCardItem) item).setNotes(cardNotes);
                    }
                    break;
                    
                case "Identity":
                    String firstName = request.getParameter("firstName");
                    String lastName = request.getParameter("lastName");
                    String passportNumber = request.getParameter("passportNumber");
                    String licenseNumber = request.getParameter("licenseNumber");
                    String ssn = request.getParameter("ssn");
                    String address = request.getParameter("address");
                    String phone = request.getParameter("phone");
                    String email = request.getParameter("email");
                    String identityNotes = request.getParameter("notes");
                    String passportExpDateStr = request.getParameter("passportExpirationDate");
                    String licenseExpDateStr = request.getParameter("licenseExpirationDate");
                    item = new IdentityItem(name, 
                                          firstName != null ? firstName : "",
                                          lastName != null ? lastName : "");
                    IdentityItem identity = (IdentityItem) item;
                    if (passportNumber != null) identity.setPassportNumber(passportNumber);
                    if (licenseNumber != null) identity.setLicenseNumber(licenseNumber);
                    if (ssn != null) identity.setSocialSecurityNumber(ssn);
                    if (address != null) identity.setAddress(address);
                    if (phone != null) identity.setPhone(phone);
                    if (email != null) identity.setEmail(email);
                    if (identityNotes != null) identity.setNotes(identityNotes);
                    // Parse expiration dates
                    if (passportExpDateStr != null && !passportExpDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            identity.setPassportExpirationDate(sdf.parse(passportExpDateStr));
                        } catch (Exception e) {
                            // Ignore parse errors
                        }
                    }
                    if (licenseExpDateStr != null && !licenseExpDateStr.isEmpty()) {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            identity.setLicenseExpirationDate(sdf.parse(licenseExpDateStr));
                        } catch (Exception e) {
                            // Ignore parse errors
                        }
                    }
                    break;
                    
                case "Secure Note":
                    String content = request.getParameter("content");
                    item = new SecureNoteItem(name, content != null ? content : "");
                    break;
            }
            
            if (item != null) {
                vault.addItem(item);
                vault.saveItem(item);
            }
            
        } else if ("update".equals(action)) {
            String itemId = request.getParameter("itemId");
            VaultItem item = vault.getItem(itemId);
            
            if (item == null) {
                response.sendRedirect("vault.jsp?error=item_not_found");
                return;
            }
            
            // Update name if provided
            String name = request.getParameter("name");
            if (name != null) {
                item.setName(name);
            }
            
            vault.saveItem(item);
            
            // Update type-specific fields
            if (item instanceof LoginItem) {
                LoginItem login = (LoginItem) item;
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String url = request.getParameter("url");
                String notes = request.getParameter("notes");
                if (username != null) login.setUsername(username);
                if (password != null) login.setPassword(password);
                if (url != null) login.setUrl(url);
                if (notes != null) login.setNotes(notes);
                
            } else if (item instanceof CreditCardItem) {
                CreditCardItem card = (CreditCardItem) item;
                String cardNumber = request.getParameter("cardNumber");
                String cardholderName = request.getParameter("cardholderName");
                String cvv = request.getParameter("cvv");
                String expDateStr = request.getParameter("expirationDate");
                String notes = request.getParameter("notes");
                if (cardNumber != null) card.setCardNumber(cardNumber);
                if (cardholderName != null) card.setCardholderName(cardholderName);
                if (cvv != null) card.setCvv(cvv);
                if (expDateStr != null && !expDateStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        card.setExpirationDate(sdf.parse(expDateStr));
                    } catch (Exception e) {
                        // Keep existing date
                    }
                }
                if (notes != null) card.setNotes(notes);
                
            } else if (item instanceof IdentityItem) {
                IdentityItem identity = (IdentityItem) item;
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                String passportNumber = request.getParameter("passportNumber");
                String licenseNumber = request.getParameter("licenseNumber");
                String ssn = request.getParameter("ssn");
                String address = request.getParameter("address");
                String phone = request.getParameter("phone");
                String email = request.getParameter("email");
                String notes = request.getParameter("notes");
                String passportExpDateStr = request.getParameter("passportExpirationDate");
                String licenseExpDateStr = request.getParameter("licenseExpirationDate");
                if (firstName != null) identity.setFirstName(firstName);
                if (lastName != null) identity.setLastName(lastName);
                if (passportNumber != null) identity.setPassportNumber(passportNumber);
                if (licenseNumber != null) identity.setLicenseNumber(licenseNumber);
                if (ssn != null) identity.setSocialSecurityNumber(ssn);
                if (address != null) identity.setAddress(address);
                if (phone != null) identity.setPhone(phone);
                if (email != null) identity.setEmail(email);
                if (notes != null) identity.setNotes(notes);
                // Update expiration dates
                if (passportExpDateStr != null && !passportExpDateStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        identity.setPassportExpirationDate(sdf.parse(passportExpDateStr));
                    } catch (Exception e) {
                        // Keep existing
                    }
                } else {
                    identity.setPassportExpirationDate(null);
                }
                if (licenseExpDateStr != null && !licenseExpDateStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        identity.setLicenseExpirationDate(sdf.parse(licenseExpDateStr));
                    } catch (Exception e) {
                        // Keep existing
                    }
                } else {
                    identity.setLicenseExpirationDate(null);
                }
                
            } else if (item instanceof SecureNoteItem) {
                SecureNoteItem note = (SecureNoteItem) item;
                String content = request.getParameter("content");
                if (content != null) note.setContent(content);
            }
            
        } else if ("delete".equals(action)) {
            String itemId = request.getParameter("itemId");
            if (itemId != null) {
                vault.removeItem(itemId);
            }
        }
        
        response.sendRedirect("vault.jsp");
    }
}
