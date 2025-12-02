<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>MyPass - Login</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
        }
        label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
            color: #555;
        }
        input[type="email"], input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        button[type="submit"] {
            width: 100%;
            padding: 12px;
            margin-top: 20px;
            background: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 16px;
            cursor: pointer;
        }
        button[type="submit"]:hover {
            background: #218838;
        }
        .error {
            color: #c62828;
            background: #ffebee;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .success {
            color: #2e7d32;
            background: #e8f5e9;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .link {
            text-align: center;
            margin-top: 20px;
        }
        .link a {
            color: #007bff;
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Login</h1>

        <% String error = request.getParameter("error");
           String recovered = request.getParameter("recovered");
           String timeout = request.getParameter("timeout");
           if ("1".equals(error)) { %>
            <div class="error">Invalid email or password.</div>
        <% } %>
        
        <% if ("true".equals(timeout)) { %>
            <div class="error">Your session has expired due to inactivity. Please login again.</div>
        <% } %>
        
        <% if ("true".equals(recovered)) { %>
            <div class="success">Password has been reset successfully. Please login with your new password.</div>
        <% } %>

        <form action="login" method="post">
            <label>Email:</label>
            <input type="email" name="email" required>

            <label>Password:</label>
            <input type="password" name="password" required>

            <button type="submit">Login</button>
        </form>
        
        <div class="link">
            <a href="password-recovery">Forgot Password?</a> | 
            <a href="register.jsp">Register</a> | 
            <a href="home.jsp">Home</a>
        </div>
    </div>
</body>
</html>
