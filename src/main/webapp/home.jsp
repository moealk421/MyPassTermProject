
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>MyPass — Home</title>
    <style>
        :root{
            --accent:#2563eb;
            --accent-600:#1e40af;
            --muted:#6b7280;
            --card:#ffffff;
            --bg:#f3f4f6;
            --radius:12px;
            --max-width:1100px;
            --shadow: 0 6px 18px rgba(16,24,40,0.08);
        }
        html,body{height:100%;}
        body {
            margin:0;
            font-family: Inter, system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
            background: linear-gradient(180deg,#f8fafc 0%, var(--bg) 100%);
            color:#0f172a;
            -webkit-font-smoothing:antialiased;
        }
        .container{
            max-width:var(--max-width);
            margin:40px auto;
            padding:24px;
        }
        .btn{display:inline-block;padding:10px 16px;border-radius:10px;text-decoration:none;font-weight:600;cursor:pointer}
        .btn-primary{background:var(--accent);color:#fff;box-shadow:var(--shadow)}
        .hero{display:grid;grid-template-columns:1fr;gap:24px;align-items:center;background:transparent;border-radius:var(--radius);}
        input[type="email"], input[type="password"] {
            width:100%;
            padding:10px;
            border:1px solid #e6e9ef;
            border-radius:8px;
            margin-bottom:10px;
            box-sizing:border-box;
        }
        .show-password-toggle {
            margin-top:4px;
            font-size:14px;
            display:flex;
            align-items:center;
            gap:6px;
        }
    </style>
</head>
<body>
    <div class="container">
        <main style="display:flex;align-items:center;justify-content:center;min-height:60vh;">
            <div style="width:100%;max-width:420px;background:var(--card);padding:28px;border-radius:12px;box-shadow:var(--shadow);">
                <h2 style="margin-top:0;text-align:center">MyPass</h2>
                <p style="text-align:center;color:var(--muted);margin-top:4px;margin-bottom:18px">Sign in to access your vault or create a new account.</p>

                <form action="login" method="post">
                    <label style="display:block;margin-bottom:6px;font-weight:600">Email</label>
                    <input type="email" name="email" required>

                    <label style="display:block;margin-bottom:6px;font-weight:600">Password</label>
                    <input type="password" name="password" id="password" required>
                    <div class="show-password-toggle">
                        <input type="checkbox" id="showPassword" onclick="togglePasswordVisibility()">
                        <label for="showPassword">Show Password</label>
                    </div>

                    <button type="submit" class="btn btn-primary" style="width:100%;padding:12px;border-radius:8px;border:none;font-weight:700">Login</button>
                </form>

                <div style="display:flex;justify-content:space-between;align-items:center;margin-top:12px;font-size:14px;color:var(--muted)">
                    <a href="password-recovery" style="color:var(--accent);text-decoration:none">Forgot password?</a>
                    <a href="register.jsp" style="color:var(--accent);text-decoration:none">Register</a>
                </div>
            </div>
        </main>
    </div>

    <script>
        function togglePasswordVisibility() {
            const pwdInput = document.getElementById('password');
            const checkbox = document.getElementById('showPassword');
            pwdInput.type = checkbox.checked ? 'text' : 'password';
        }
    </script>
</body>
</html>
