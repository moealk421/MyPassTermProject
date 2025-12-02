package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Handles user registration with validation and auto-login.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Extract all registration parameters
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String q1Question = request.getParameter("securityQ1Question");
        String q1Answer = request.getParameter("securityQ1Answer");
        String q2Question = request.getParameter("securityQ2Question");
        String q2Answer = request.getParameter("securityQ2Answer");
        String q3Question = request.getParameter("securityQ3Question");
        String q3Answer = request.getParameter("securityQ3Answer");

        // Validate all required fields are present
        if (email == null || password == null ||
                q1Question == null || q1Answer == null ||
                q2Question == null || q2Answer == null ||
                q3Question == null || q3Answer == null ||
                email.isEmpty() || password.isEmpty() ||
                q1Question.isEmpty() || q1Answer.isEmpty() ||
                q2Question.isEmpty() || q2Answer.isEmpty() ||
                q3Question.isEmpty() || q3Answer.isEmpty()) {
            response.sendRedirect("register.jsp?error=missing_fields");
            return;
        }

        // Check for existing user
        UserService userService = UserService.getInstance();
        if (userService.userExists(email)) {
            response.sendRedirect("register.jsp?error=user_exists");
            return;
        }

        // Check password strength (informational only)
        PasswordStrengthChecker checker = new PasswordStrengthChecker();
        String strength = checker.checkPasswordStrength(password);
        
        // Attempt registration
        try {
            boolean success = userService.registerUser(
                    email,
                    password,
                    // Encode security questions with answers for storage
                    Arrays.asList(
                            SecurityQuestionUtil.encodeQuestionAndAnswer(q1Question, q1Answer),
                            SecurityQuestionUtil.encodeQuestionAndAnswer(q2Question, q2Answer),
                            SecurityQuestionUtil.encodeQuestionAndAnswer(q3Question, q3Answer)
                    )
            );
            
            if (success) {
                // Auto-login after successful registration
                User newUser = userService.getUserByEmail(email);
                if (newUser != null) {
                    String sessionId = request.getSession().getId();
                    UserSession.getInstance().login(sessionId, newUser);
                    response.sendRedirect("vault.jsp");
                } else {
                    response.sendRedirect("register.jsp?error=registration_failed&msg=user_not_found");
                }
            } else {
                response.sendRedirect("register.jsp?error=registration_failed&msg=user_creation_failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=registration_failed&msg=" + e.getMessage());
        }
    }
}
