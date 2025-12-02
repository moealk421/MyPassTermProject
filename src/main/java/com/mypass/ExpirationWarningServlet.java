package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * API endpoint that returns expiration warnings for user's vault items.
 */
@WebServlet("/api/expiration-warnings")
public class ExpirationWarningServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Validate user session
        String sessionId = request.getSession().getId();
        UserSession userSession = UserSession.getInstance();
        User user = userSession.getLoggedUser(sessionId);
        
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        // Set up expiration checker with observer to collect warnings
        Vault vault = user.getVault();
        ExpirationChecker checker = new ExpirationChecker();
        VaultExpirationObserver observer = new VaultExpirationObserver();
        checker.addObserver(observer);
        
        // Run the check
        checker.checkVault(vault);
        
        // Build JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        StringBuilder json = new StringBuilder();
        json.append("{\"warnings\":[");
        
        List<String> warnings = observer.getWarnings();
        for (int i = 0; i < warnings.size(); i++) {
            if (i > 0) json.append(",");
            json.append("\"").append(escapeJson(warnings.get(i))).append("\"");
        }
        
        json.append("]}");
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
