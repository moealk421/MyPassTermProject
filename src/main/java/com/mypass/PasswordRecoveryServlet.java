package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles password recovery workflow: verify questions then reset password.
 */
@WebServlet("/password-recovery")
public class PasswordRecoveryServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Show recovery form
        request.getRequestDispatcher("password-recovery.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String action = request.getParameter("action");
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            response.sendRedirect("password-recovery.jsp?error=missing_email");
            return;
        }
        
        // Look up user
        UserService userService = UserService.getInstance();
        User user = userService.getUserByEmail(email);
        
        if (user == null) {
            response.sendRedirect("password-recovery.jsp?error=user_not_found");
            return;
        }
        
        if ("verify".equals(action)) {
            // Step 1: Verify security questions
            String answer1 = request.getParameter("answer1");
            String answer2 = request.getParameter("answer2");
            String answer3 = request.getParameter("answer3");
            
            if (answer1 == null || answer2 == null || answer3 == null) {
                response.sendRedirect("password-recovery.jsp?error=missing_answers&email=" + email);
                return;
            }
            
            PasswordRecoveryService recoveryService = PasswordRecoveryService.getInstance();
            String[] answers = {answer1, answer2, answer3};
            
            if (recoveryService.verifySecurityQuestions(user, answers)) {
                // Questions verified - allow password reset
                request.getSession().setAttribute("recovery_email", email);
                request.getSession().setAttribute("recovery_verified", true);
                response.sendRedirect("password-recovery.jsp?step=reset");
            } else {
                response.sendRedirect("password-recovery.jsp?error=invalid_answers&email=" + email);
            }
            
        } else if ("reset".equals(action)) {
            // Step 2: Reset password (requires prior verification)
            Boolean verified = (Boolean) request.getSession().getAttribute("recovery_verified");
            String recoveryEmail = (String) request.getSession().getAttribute("recovery_email");
            
            // Ensure verification step was completed
            if (verified == null || !verified || !email.equals(recoveryEmail)) {
                response.sendRedirect("password-recovery.jsp?error=not_verified");
                return;
            }
            
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                response.sendRedirect("password-recovery.jsp?error=missing_password&step=reset");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                response.sendRedirect("password-recovery.jsp?error=password_mismatch&step=reset");
                return;
            }
            
            // Re-verify answers and update password
            String answer1 = request.getParameter("answer1");
            String answer2 = request.getParameter("answer2");
            String answer3 = request.getParameter("answer3");
            String[] answers = {answer1, answer2, answer3};
            
            PasswordRecoveryService recoveryService = PasswordRecoveryService.getInstance();
            if (recoveryService.recoverPassword(user, answers, newPassword)) {
                // Success - clear session and redirect to login
                request.getSession().removeAttribute("recovery_email");
                request.getSession().removeAttribute("recovery_verified");
                response.sendRedirect("login.jsp?recovered=true");
            } else {
                response.sendRedirect("password-recovery.jsp?error=recovery_failed&step=reset");
            }
        }
    }
}
