package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Handles login requests and session establishment.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate required parameters
        if (email == null || password == null) {
            response.sendRedirect("login.jsp?error=1");
            return;
        }

        // Verify credentials against database
        UserService userService = UserService.getInstance();
        if (userService.verifyPassword(email, password)) {
            // Login successful - establish session
            User currentUser = userService.getUserByEmail(email);
            if (currentUser != null) {
                String sessionId = request.getSession().getId();
                UserSession.getInstance().login(sessionId, currentUser);
                response.sendRedirect("vault.jsp");
            } else {
                System.err.println("User not found after password verification: " + email);
                response.sendRedirect("login.jsp?error=1");
            }
        } else {
            // Login failed
            System.err.println("Login failed for email: " + email);
            response.sendRedirect("login.jsp?error=1");
        }
    }
}
