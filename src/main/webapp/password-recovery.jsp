
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mypass.*" %>
<%
    String step = request.getParameter("step");
    String email = request.getParameter("email");
    if (email == null) {
        email = (String) session.getAttribute("recovery_email");
    }
    
    User user = null;
    if (email != null) {
        user = UserService.getInstance().getUserByEmail(email);
    }
    
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>MyPass - Password Recovery</title>
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
        input[type="email"], input[type="password"], input[type="text"] {
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
        .password-strength {
            margin-top: 5px;
            padding: 8px;
            border-radius: 4px;
            font-size: 14px;
        }
        .strength-weak {
            background: #ffebee;
            color: #c62828;
            border-left: 4px solid #c62828;
        }
        .strength-medium {
            background: #fff3e0;
            color: #e65100;
            border-left: 4px solid #e65100;
        }
        .strength-strong {
            background: #e8f5e9;
            color: #2e7d32;
            border-left: 4px solid #2e7d32;
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
        .security-question {
            background: #f8f9fa;
            padding: 10px;
            border-radius: 4px;
            margin: 10px 0;
            font-style: italic;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Password Recovery</h1>
        
        <% if (error != null) { %>
            <div class="error">
                <% if ("missing_email".equals(error)) { %>
                    Please enter your email address.
                <% } else if ("user_not_found".equals(error)) { %>
                    No account found with that email address.
                <% } else if ("missing_answers".equals(error)) { %>
                    Please answer all security questions.
                <% } else if ("invalid_answers".equals(error)) { %>
                    One or more security question answers are incorrect.
                <% } else if ("not_verified".equals(error)) { %>
                    Please verify your security questions first.
                <% } else if ("missing_password".equals(error)) { %>
                    Please enter a new password.
                <% } else if ("password_mismatch".equals(error)) { %>
                    Passwords do not match.
                <% } else if ("recovery_failed".equals(error)) { %>
                    Password recovery failed. Please try again.
                <% } %>
            </div>
        <% } %>
        
        <% if (step == null && user == null) { %>
            <!-- Step 1: Enter email -->
            <form action="password-recovery" method="post">
                <input type="hidden" name="action" value="verify">
                <label>Email Address:</label>
                <input type="email" name="email" required>
                <button type="submit">Continue</button>
            </form>
        <% } else if (step == null && user != null) { %>
            <!-- Step 2: Answer security questions -->
            <form action="password-recovery" method="post">
                <input type="hidden" name="action" value="verify">
                <input type="hidden" name="email" value="<%= email %>">
                
                <p>Please answer your security questions:</p>
                
                <% if (user.getSecurityQuestions() != null && user.getSecurityQuestions().size() >= 3) { %>
                    <div class="security-question">
                        <strong>Question 1:</strong> <%= SecurityQuestionUtil.extractQuestionText(user.getSecurityQuestions().get(0)) %>
                    </div>
                    <label>Answer 1:</label>
                    <input type="text" name="answer1" required>
                    
                    <div class="security-question">
                        <strong>Question 2:</strong> <%= SecurityQuestionUtil.extractQuestionText(user.getSecurityQuestions().get(1)) %>
                    </div>
                    <label>Answer 2:</label>
                    <input type="text" name="answer2" required>
                    
                    <div class="security-question">
                        <strong>Question 3:</strong> <%= SecurityQuestionUtil.extractQuestionText(user.getSecurityQuestions().get(2)) %>
                    </div>
                    <label>Answer 3:</label>
                    <input type="text" name="answer3" required>
                <% } else { %>
                    <div class="error">Security questions not found for this account.</div>
                <% } %>
                
                <button type="submit">Verify Answers</button>
            </form>
        <% } else if ("reset".equals(step)) { %>
            <!-- Step 3: Reset password -->
            <form action="password-recovery" method="post">
                <input type="hidden" name="action" value="reset">
                <input type="hidden" name="email" value="<%= email %>">
                
                <p>Please answer your security questions again and set a new password:</p>
                
                <% if (user != null && user.getSecurityQuestions() != null && user.getSecurityQuestions().size() >= 3) { %>
                    <div class="security-question">
                        <strong>Question 1:</strong> <%= SecurityQuestionUtil.extractQuestionText(user.getSecurityQuestions().get(0)) %>
                    </div>
                    <label>Answer 1:</label>
                    <input type="text" name="answer1" required>
                    
                    <div class="security-question">
                        <strong>Question 2:</strong> <%= SecurityQuestionUtil.extractQuestionText(user.getSecurityQuestions().get(1)) %>
                    </div>
                    <label>Answer 2:</label>
                    <input type="text" name="answer2" required>
                    
                    <div class="security-question">
                        <strong>Question 3:</strong> <%= SecurityQuestionUtil.extractQuestionText(user.getSecurityQuestions().get(2)) %>
                    </div>
                    <label>Answer 3:</label>
                    <input type="text" name="answer3" required>
                <% } %>
                
                <label>New Password:</label>
                <input type="password" name="newPassword" id="newPassword" required 
                       onkeyup="checkPasswordStrength()">
                <div id="passwordStrength" style="display:none; margin-top: 5px;"></div>
                
                <label>Confirm Password:</label>
                <input type="password" name="confirmPassword" required>
                
                <button type="submit">Reset Password</button>
            </form>
        <% } %>
        
        <div class="link">
            <a href="login.jsp">Back to Login</a> | <a href="home.jsp">Home</a>
        </div>
    </div>
    
    <script>
        function evaluatePasswordStrength(password) {
            const MIN_LENGTH = 8;
            const STRONG_LENGTH = 12;
            let score = 0;
            const warnings = [];

            if (password.length < MIN_LENGTH) {
                warnings.push(`Password must be at least ${MIN_LENGTH} characters long`);
            } else {
                score += 2;
                if (password.length >= STRONG_LENGTH) {
                    score += 1;
                }
            }

            const hasUppercase = /[A-Z]/.test(password);
            const hasLowercase = /[a-z]/.test(password);
            const hasNumber = /[0-9]/.test(password);
            const hasSpecial = /[^a-zA-Z0-9\s]/.test(password);

            if (hasUppercase) score += 1; else warnings.push('Add uppercase letters');
            if (hasLowercase) score += 1; else warnings.push('Add lowercase letters');
            if (hasNumber) score += 1; else warnings.push('Add numbers');
            if (hasSpecial) score += 1; else warnings.push('Add special characters');

            let strength = 'weak';
            if (score > 5) {
                strength = 'strong';
            } else if (score > 3) {
                strength = 'medium';
            }

            return {
                strength,
                warning: warnings.length ? warnings.join(', ') : null
            };
        }

        function checkPasswordStrength() {
            const password = document.getElementById('newPassword').value;
            const strengthDiv = document.getElementById('passwordStrength');

            if (password.length === 0) {
                strengthDiv.style.display = 'none';
                return;
            }

            const result = evaluatePasswordStrength(password);
            strengthDiv.style.display = 'block';
            strengthDiv.className = 'password-strength strength-' + result.strength;
            if (result.warning) {
                strengthDiv.innerHTML = '<strong>Warning:</strong> ' + result.warning;
            } else {
                strengthDiv.innerHTML = '<strong>Password Strength:</strong> ' +
                    result.strength.charAt(0).toUpperCase() + result.strength.slice(1);
            }
        }
    </script>
</body>
</html>

