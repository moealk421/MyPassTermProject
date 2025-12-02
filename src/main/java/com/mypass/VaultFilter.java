package com.mypass;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter that enforces authentication and updates activity for vault access.
 */
@WebFilter("/vault.jsp")
public class VaultFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String sessionId = httpRequest.getSession().getId();
        UserSession userSession = UserSession.getInstance();
        
        // Update activity on each request to prevent auto-lock
        if (userSession.isLoggedIn(sessionId)) {
            userSession.updateActivity(sessionId);
        }
        
        // Redirect if session expired or not logged in
        if (!userSession.isLoggedIn(sessionId)) {
            httpResponse.sendRedirect("login.jsp?timeout=true");
            return;
        }
        
        // Session valid, continue to vault
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // No cleanup needed
    }
}
