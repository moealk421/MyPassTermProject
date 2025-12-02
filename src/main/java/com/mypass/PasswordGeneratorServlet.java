package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * API endpoint for generating strong passwords.
 */
@WebServlet("/api/password-generator")
public class PasswordGeneratorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Parse length parameter with validation
        int length = 16; // Default
        try {
            String lengthParam = request.getParameter("length");
            if (lengthParam != null) {
                length = Integer.parseInt(lengthParam);
                // Enforce bounds
                if (length < 4 || length > 128) {
                    length = 16;
                }
            }
        } catch (NumberFormatException e) {
            length = 16;
        }

        // Use Builder pattern to generate password
        PasswordBuilder builder = new ConcretePasswordBuilder();
        PasswordDirector director = new PasswordDirector(builder);
        Password password = director.buildStrongPassword(length);

        // Return as JSON
        response.getWriter().write("{\"password\":\"" + password.getValue() + "\"}");
    }
}
