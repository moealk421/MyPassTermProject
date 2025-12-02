<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.mypass.SecurityQuestionUtil" %>
<!DOCTYPE html>
<html>
<head>
    <title>MyPass - Register</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px; background-color: #f5f5f5; }
        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #333; text-align: center; }
        label { display: block; margin-top: 15px; font-weight: bold; color: #555; }
        input[type="email"], input[type="password"], input[type="text"] {
            width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box;
        }
        .password-section { position: relative; }
        .security-question-block { margin-top: 15px; padding: 12px; background: #f8f9fa; border-radius: 6px; border: 1px solid #e0e0e0; }
        select { width: 100%; padding: 10px; margin-top: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; background-color: #fff; }
        .password-generator { margin-top: 10px; padding: 10px; background: #f8f9fa; border-radius: 4px; }
        .password-generator button { background: #007bff; color: white; border: none; padding: 8px 15px; border-radius: 4px; cursor: pointer; margin-right: 10px; }
        .password-generator button:hover { background: #0056b3; }
        .password-strength { margin-top: 5px; padding: 8px; border-radius: 4px; font-size: 14px; }
        .strength-weak { background: #ffebee; color: #c62828; border-left: 4px solid #c62828; }
        .strength-medium { background: #fff3e0; color: #e65100; border-left: 4px solid #e65100; }
        .strength-strong { background: #e8f5e9; color: #2e7d32; border-left: 4px solid #2e7d32; }
        button[type="submit"] { width: 100%; padding: 12px; margin-top: 20px; background: #28a745; color: white; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; }
        button[type="submit"]:hover { background: #218838; }
        .error { color: #c62828; background: #ffebee; padding: 10px; border-radius: 4px; margin-bottom: 20px; }
        .link { text-align: center; margin-top: 20px; }
        .link a { color: #007bff; text-decoration: none; }
        .show-password-toggle { margin-top: 5px; font-size: 14px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Register for MyPass</h1>
    <% 
        String error = request.getParameter("error");
        String msg = request.getParameter("msg");
        if (error != null) {
            String errorMsg = "";
            if ("missing_fields".equals(error)) errorMsg = "Please fill in all fields";
            else if ("user_exists".equals(error)) errorMsg = "Email already registered";
            else if ("registration_failed".equals(error)) {
                errorMsg = "Registration failed. Please try again.";
                if (msg != null && !msg.isEmpty()) errorMsg += " (" + msg + ")";
            }
    %>
        <div class="error"><%= errorMsg %></div>
    <% } %>

    <%
        Map<String, String> securityQuestionMap = SecurityQuestionUtil.getSecurityQuestions();
    %>

    <form action="register" method="post" id="registerForm">
        <label>Email:</label>
        <input type="email" name="email" id="email" required>

        <label>Master Password:</label>
        <div class="password-section">
            <input type="password" name="password" id="password" required onkeyup="checkPasswordStrength()">
            <div class="show-password-toggle">
                <input type="checkbox" id="showPassword" onclick="togglePasswordVisibility()">
                <label for="showPassword">Show Password</label>
            </div>
            <div id="passwordStrength" style="display:none; margin-top: 5px;"></div>
            <div id="weakPasswordWarning" style="display:none; margin-top: 5px;" class="error"></div>
            
            <div class="password-generator">
                <strong>Password Generator:</strong><br>
                <label>Length: <input type="number" id="pwdLength" value="16" min="8" max="64" style="width: 60px;"></label>
                <button type="button" onclick="generatePassword()">Generate Strong Password</button>
            </div>
        </div>

        <% for (int i=1; i<=3; i++) { %>
        <div class="security-question-block">
            <label>Security Question <%= i %>:</label>
            <select name="securityQ<%=i%>Question" required>
                <option value="">Select a question</option>
                <% for (Map.Entry<String,String> entry : securityQuestionMap.entrySet()) { %>
                    <option value="<%= entry.getKey() %>"><%= entry.getValue() %></option>
                <% } %>
            </select>
            <label style="margin-top: 10px;">Answer:</label>
            <input type="text" name="securityQ<%=i%>Answer" placeholder="Enter your answer" required>
        </div>
        <% } %>

        <button type="submit">Register</button>
    </form>

    <div class="link">
        Already have an account? <a href="login.jsp">Login</a> | <a href="home.jsp">Home</a>
    </div>
</div>

<script>
function togglePasswordVisibility() {
    const pwdInput = document.getElementById('password');
    const checkbox = document.getElementById('showPassword');
    pwdInput.type = checkbox.checked ? 'text' : 'password';
}

function generatePassword() {
    const length = document.getElementById('pwdLength').value;
    fetch('api/password-generator?length=' + length)
        .then(res => res.json())
        .then(data => {
            document.getElementById('password').value = data.password;
            checkPasswordStrength();
        }).catch(e => alert('Error generating password'));
}

function evaluatePasswordStrength(password) {
    const MIN_LENGTH = 8, STRONG_LENGTH = 12;
    let score = 0, warnings = [];

    if (password.length < MIN_LENGTH) warnings.push(`Password must be at least 8 characters long`);
    else { score += 2; if (password.length >= STRONG_LENGTH) score +=1; }

    const hasUpper = /[A-Z]/.test(password);
    const hasLower = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecial = /[^a-zA-Z0-9\s]/.test(password);

    if (!hasUpper) warnings.push('Add uppercase letters'); else score++;
    if (!hasLower) warnings.push('Add lowercase letters'); else score++;
    if (!hasNumber) warnings.push('Add numbers'); else score++;
    if (!hasSpecial) warnings.push('Add special characters'); else score++;

    let strength = 'weak';
    if (score > 5) strength = 'strong';
    else if (score > 3) strength = 'medium';

    return { strength, warning: warnings.length ? warnings.join(', ') : null };
}

function checkPasswordStrength() {
    const password = document.getElementById('password').value;
    const strengthDiv = document.getElementById('passwordStrength');
    const warningDiv = document.getElementById('weakPasswordWarning');

    if (password.length === 0) {
        strengthDiv.style.display = 'none';
        warningDiv.style.display = 'none';
        return;
    }

    const result = evaluatePasswordStrength(password);

    strengthDiv.style.display = 'block';
    strengthDiv.className = 'password-strength strength-' + result.strength;
    strengthDiv.innerHTML = '<strong>Password Strength:</strong> ' + result.strength.charAt(0).toUpperCase() + result.strength.slice(1);

    if (result.warning) {
        warningDiv.innerHTML = (result.strength==='weak'?'<strong>⚠ Weak Password Warning:</strong> ':'<strong>⚠ Password could be stronger:</strong> ') + result.warning;
        warningDiv.style.display = 'block';
    } else warningDiv.style.display = 'none';
}
</script>
</body>
</html>
