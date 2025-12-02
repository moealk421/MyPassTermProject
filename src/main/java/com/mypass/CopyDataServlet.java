package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * API endpoint for copying vault item field values to clipboard.
 */
@WebServlet("/api/copy")
public class CopyDataServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Retrieve session and validate user login
        String sessionId = request.getSession().getId();
        UserSession userSession = UserSession.getInstance();
        User user = userSession.getLoggedUser(sessionId);
        
        // Reject unauthenticated requests
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Get required parameters: which item and which field to copy
        String itemId = request.getParameter("itemId");
        String field = request.getParameter("field");
        
        if (itemId == null || field == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Fetch the item from user's vault
        Vault vault = user.getVault();
        VaultItem item = vault.getItem(itemId);
        
        if (item == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String value = null;
        
        // Extract requested field based on item type
        if (item instanceof LoginItem) {
            LoginItem login = (LoginItem) item;
            switch (field) {
                case "username":
                    value = login.getUsername();
                    break;
                case "password":
                    value = login.getPassword();
                    break;
                case "url":
                    value = login.getUrl();
                    break;
            }
        } else if (item instanceof CreditCardItem) {
            CreditCardItem card = (CreditCardItem) item;
            switch (field) {
                case "cardNumber":
                    value = card.getCardNumber();
                    break;
                case "cvv":
                    value = card.getCvv();
                    break;
            }
        } else if (item instanceof IdentityItem) {
            IdentityItem identity = (IdentityItem) item;
            switch (field) {
                case "passport":
                    value = identity.getPassportNumber();
                    break;
                case "license":
                    value = identity.getLicenseNumber();
                    break;
                case "ssn":
                    value = identity.getSocialSecurityNumber();
                    break;
            }
        }
        
        // Field not found or not supported for this item type
        if (value == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Return plain text for client-side clipboard copy
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(value);
    }
}
