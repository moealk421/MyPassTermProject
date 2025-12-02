package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * API endpoint that returns a single vault item as JSON.
 */
@WebServlet("/api/get-item")
public class GetItemServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Validate session
        String sessionId = request.getSession().getId();
        UserSession userSession = UserSession.getInstance();
        User user = userSession.getLoggedUser(sessionId);
        
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Get and validate item ID parameter
        String itemId = request.getParameter("itemId");
        if (itemId == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Fetch item from vault
        Vault vault = user.getVault();
        VaultItem item = vault.getItem(itemId);
        
        if (item == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Build JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":\"").append(item.getId()).append("\",");
        json.append("\"name\":\"").append(escapeJson(item.getName())).append("\",");
        
        // Normalize type for form compatibility (spaces removed)
        String typeForForm = item.getType();
        if ("Credit Card".equals(typeForForm)) {
            typeForForm = "CreditCard";
        } else if ("Secure Note".equals(typeForForm)) {
            typeForForm = "SecureNote";
        }
        json.append("\"type\":\"").append(typeForForm).append("\",");
        json.append("\"typeDisplay\":\"").append(item.getType()).append("\",");
        
        // Serialize type-specific fields
        if (item instanceof LoginItem) {
            LoginItem login = (LoginItem) item;
            json.append("\"username\":\"").append(escapeJson(login.getUsername())).append("\",");
            json.append("\"password\":\"").append(escapeJson(login.getPassword())).append("\",");
            json.append("\"url\":\"").append(escapeJson(login.getUrl())).append("\",");
            json.append("\"notes\":\"").append(escapeJson(login.getNotes())).append("\"");
        } else if (item instanceof CreditCardItem) {
            CreditCardItem card = (CreditCardItem) item;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            json.append("\"cardNumber\":\"").append(escapeJson(card.getCardNumber())).append("\",");
            json.append("\"cardholderName\":\"").append(escapeJson(card.getCardholderName())).append("\",");
            json.append("\"cvv\":\"").append(escapeJson(card.getCvv())).append("\",");
            json.append("\"expirationDate\":\"").append(card.getExpirationDate() != null ? sdf.format(card.getExpirationDate()) : "").append("\",");
            json.append("\"notes\":\"").append(escapeJson(card.getNotes())).append("\"");
        } else if (item instanceof IdentityItem) {
            IdentityItem identity = (IdentityItem) item;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            json.append("\"firstName\":\"").append(escapeJson(identity.getFirstName())).append("\",");
            json.append("\"lastName\":\"").append(escapeJson(identity.getLastName())).append("\",");
            json.append("\"passportNumber\":\"").append(escapeJson(identity.getPassportNumber())).append("\",");
            json.append("\"passportExpirationDate\":\"").append(identity.getPassportExpirationDate() != null ? sdf.format(identity.getPassportExpirationDate()) : "").append("\",");
            json.append("\"licenseNumber\":\"").append(escapeJson(identity.getLicenseNumber())).append("\",");
            json.append("\"licenseExpirationDate\":\"").append(identity.getLicenseExpirationDate() != null ? sdf.format(identity.getLicenseExpirationDate()) : "").append("\",");
            json.append("\"ssn\":\"").append(escapeJson(identity.getSocialSecurityNumber())).append("\",");
            json.append("\"address\":\"").append(escapeJson(identity.getAddress())).append("\",");
            json.append("\"phone\":\"").append(escapeJson(identity.getPhone())).append("\",");
            json.append("\"email\":\"").append(escapeJson(identity.getEmail())).append("\",");
            json.append("\"notes\":\"").append(escapeJson(identity.getNotes())).append("\"");
        } else if (item instanceof SecureNoteItem) {
            SecureNoteItem note = (SecureNoteItem) item;
            json.append("\"content\":\"").append(escapeJson(note.getContent())).append("\"");
        }
        
        json.append("}");
        response.getWriter().write(json.toString());
    }
    
    // Escape special JSON characters
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
