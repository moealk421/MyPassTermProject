package com.mypass;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Initializes the database schema on application startup.
 * Ensures DAOs can assume the schema exists before handling requests.
 */
@WebListener
public class DatabaseInitializer implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseConnection.getInstance();
        System.out.println("Database initialized on application startup");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Reserved for connection pool cleanup if needed
    }
}
