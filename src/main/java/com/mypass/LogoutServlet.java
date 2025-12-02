package com.mypass;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles logout by invalidating the user session.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Remove from session manager
        String sessionId = request.getSession().getId();
        UserSession.getInstance().logout(sessionId);
        
        // Invalidate HTTP session
        request.getSession().invalidate();
        
        // Redirect to login
        response.sendRedirect("login.jsp");
    }
}
