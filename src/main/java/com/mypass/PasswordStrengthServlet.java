package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * API endpoint for evaluating password strength.
 */
@WebServlet("/api/password-strength")
public class PasswordStrengthServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String password = request.getParameter("password");
        if (password == null) {
            password = "";
        }
        
        // Set up checker with anonymous observer to capture results
        PasswordStrengthChecker checker = new PasswordStrengthChecker();
        
        final String[] strengthResult = new String[1];
        final String[] warningMessage = new String[1];
        
        PasswordObserver observer = new PasswordObserver() {
            @Override
            public void onWeakPassword(String message) {
                warningMessage[0] = message;
            }
            
            @Override
            public void onPasswordStrengthChange(String strength) {
                strengthResult[0] = strength;
            }
        };
        
        checker.addObserver(observer);
        String strength = checker.checkPasswordStrength(password);
        
        // Build JSON response
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"strength\":\"").append(strength).append("\",");
        json.append("\"warning\":");
        if (warningMessage[0] != null) {
            json.append("\"").append(warningMessage[0].replace("\"", "\\\"")).append("\"");
        } else {
            json.append("null");
        }
        json.append("}");
        
        response.getWriter().write(json.toString());
    }
}
