<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mypass.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    String sessionId = session.getId();
    UserSession userSession = UserSession.getInstance();
    User user = userSession.getLoggedUser(sessionId);
    
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    Vault vault = user.getVault();
    List<VaultItem> items = vault.getAllItems();
    
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>MyPass - Vault</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .header {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header h1 {
            margin: 0;
            color: #333;
        }
        .user-info {
            color: #666;
        }
        .logout-btn {
            background: #dc3545;
            color: white;
            border: none;
            padding: 8px 15px;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .logout-btn:hover {
            background: #c82333;
        }
        .vault-stats {
            background: white;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .vault-stats span {
            margin-right: 20px;
            color: #666;
        }
        .vault-stats strong {
            color: #333;
        }
        .items-container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .empty-vault {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        .item-card {
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 15px;
            margin-bottom: 15px;
            background: #fafafa;
        }
        .item-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        .item-name {
            font-size: 18px;
            font-weight: bold;
            color: #333;
        }
        .item-type {
            background: #007bff;
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
        }
        .item-actions {
            display: flex;
            gap: 5px;
        }
        .btn {
            padding: 5px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
            text-decoration: none;
            display: inline-block;
        }
        .btn-edit {
            background: #ffc107;
            color: #000;
        }
        .btn-delete {
            background: #dc3545;
            color: white;
        }
        .btn-copy {
            background: #17a2b8;
            color: white;
        }
        .btn-unmask {
            background: #6c757d;
            color: white;
        }
        .item-details {
            color: #666;
            font-size: 14px;
        }
        .item-details div {
            margin: 5px 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .sensitive-field {
            font-family: monospace;
            font-size: 14px;
        }
        .add-item-btn {
            background: #28a745;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            margin-bottom: 20px;
        }
        .add-item-btn:hover {
            background: #218838;
        }
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        .modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 20px;
            border-radius: 8px;
            width: 80%;
            max-width: 600px;
            max-height: 80vh;
            overflow-y: auto;
        }
        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .close {
            color: #aaa;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }
        .close:hover {
            color: #000;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .form-group textarea {
            min-height: 100px;
        }
        .error {
            color: #c62828;
            background: #ffebee;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .copy-success {
            position: fixed;
            top: 20px;
            right: 20px;
            background: #28a745;
            color: white;
            padding: 15px 20px;
            border-radius: 4px;
            display: none;
            z-index: 2000;
        }
    </style>
</head>
<body>
    <div class="header">
        <div>
            <h1>MyPass Vault</h1>
            <div class="user-info">Logged in as: <%= user.getEmail() %></div>
        </div>
        <div style="display:flex;gap:8px;align-items:center;">
            <a href="home.jsp" class="btn" style="background:#007bff;color:white;padding:8px 12px;border-radius:4px;text-decoration:none;">Home</a>
            <a href="logout" class="logout-btn">Logout</a>
        </div>
    </div>

    <div class="vault-stats">
        <span><strong>Total Items:</strong> <%= vault.getItemCount() %></span>
        <span><strong>Logins:</strong> <%= vault.getItemsByType("Login").size() %></span>
        <span><strong>Credit Cards:</strong> <%= vault.getItemsByType("Credit Card").size() %></span>
        <span><strong>Identities:</strong> <%= vault.getItemsByType("Identity").size() %></span>
        <span><strong>Secure Notes:</strong> <%= vault.getItemsByType("Secure Note").size() %></span>
    </div>
    <div class="vault-stats" style="margin-top: 10px; background: #fff3cd; border: 1px solid #ffeeba;">
        <span>Auto-lock after <strong>5 minutes</strong> of inactivity.</span>
        <span>Clipboard clears <strong>1 minute</strong> after copying sensitive data.</span>
    </div>

    <div id="expirationWarnings" style="display:none;" class="error"></div>

    <div class="items-container">
        <button class="add-item-btn" onclick="showAddModal()">+ Add New Item</button>
        
        <% if (error != null) { %>
            <div class="error">
                <% if ("missing_fields".equals(error)) { %>
                    Please fill in all required fields.
                <% } else if ("item_not_found".equals(error)) { %>
                    Item not found.
                <% } %>
            </div>
        <% } %>
        
        <h2>Your Vault Items</h2>
        
        <% if (items.isEmpty()) { %>
            <div class="empty-vault">
                <p>Your vault is empty. Click "Add New Item" to get started!</p>
            </div>
        <% } else { %>
            <% for (VaultItem item : items) { %>
                <div class="item-card">
                    <div class="item-header">
                        <span class="item-name"><%= item.getName() %></span>
                        <div style="display: flex; gap: 10px; align-items: center;">
                            <span class="item-type"><%= item.getType() %></span>
                            <button class="btn btn-edit" onclick="showEditModal('<%= item.getId() %>', '<%= item.getType() %>')">Edit</button>
                            <button class="btn btn-delete" onclick="deleteItem('<%= item.getId() %>')">Delete</button>
                        </div>
                    </div>
                    <div class="item-details">
                        <% if (item instanceof LoginItem) {
                            LoginItem login = (LoginItem) item;
                        %>
                            <div>
                                <strong>Username:</strong> 
                                <span class="sensitive-field" id="username_<%= item.getId() %>" data-masked="true" data-actual="<%= login.getUsername() != null ? login.getUsername().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;") : "" %>"><%= SensitiveDataProxy.maskStatic(login.getUsername()) %></span>
                                <button class="btn btn-unmask" onclick="toggleMask(this, '<%= item.getId() %>', 'username')">Show</button>
                                <button class="btn btn-copy" onclick="copyData('<%= item.getId() %>', 'username')">Copy</button>
                            </div>
                            <div>
                                <strong>Password:</strong> 
                                <span class="sensitive-field" id="password_<%= item.getId() %>" data-masked="true" data-actual="<%= login.getPassword() != null ? login.getPassword().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;") : "" %>"><%= SensitiveDataProxy.maskStatic(login.getPassword()) %></span>
                                <button class="btn btn-unmask" onclick="toggleMask(this, '<%= item.getId() %>', 'password')">Show</button>
                                <button class="btn btn-copy" onclick="copyData('<%= item.getId() %>', 'password')">Copy</button>
                            </div>
                            <div>
                                <strong>URL:</strong> 
                                <span id="url_<%= item.getId() %>"><%= login.getUrl() != null && !login.getUrl().isEmpty() ? login.getUrl() : "N/A" %></span>
                                <% if (login.getUrl() != null && !login.getUrl().isEmpty()) { %>
                                    <button class="btn btn-copy" onclick="copyData('<%= item.getId() %>', 'url')">Copy</button>
                                <% } %>
                            </div>
                            <% if (login.getNotes() != null && !login.getNotes().isEmpty()) { %>
                                <div><strong>Notes:</strong> <%= login.getNotes() %></div>
                            <% } %>
                        <% } else if (item instanceof CreditCardItem) {
                            CreditCardItem card = (CreditCardItem) item;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        %>
                            <div>
                                <strong>Card Number:</strong> 
                                <span class="sensitive-field" id="cardNumber_<%= item.getId() %>" data-masked="true" data-actual="<%= card.getCardNumber() != null ? card.getCardNumber().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;") : "" %>"><%= SensitiveDataProxy.maskStatic(card.getCardNumber()) %></span>
                                <button class="btn btn-unmask" onclick="toggleMask(this, '<%= item.getId() %>', 'cardNumber')">Show</button>
                                <button class="btn btn-copy" onclick="copyData('<%= item.getId() %>', 'cardNumber')">Copy</button>
                            </div>
                            <div>
                                <strong>CVV:</strong> 
                                <span class="sensitive-field" id="cvv_<%= item.getId() %>" data-masked="true" data-actual="<%= card.getCvv() != null ? card.getCvv().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;") : "" %>"><%= SensitiveDataProxy.maskStatic(card.getCvv()) %></span>
                                <button class="btn btn-unmask" onclick="toggleMask(this, '<%= item.getId() %>', 'cvv')">Show</button>
                                <button class="btn btn-copy" onclick="copyData('<%= item.getId() %>', 'cvv')">Copy</button>
                            </div>
                            <div><strong>Cardholder:</strong> <%= card.getCardholderName() %></div>
                            <div><strong>Expires:</strong> <%= card.getExpirationDate() != null ? sdf.format(card.getExpirationDate()) : "N/A" %></div>
                            <% if (card.getNotes() != null && !card.getNotes().isEmpty()) { %>
                                <div><strong>Notes:</strong> <%= card.getNotes() %></div>
                            <% } %>
                        <% } else if (item instanceof IdentityItem) {
                            IdentityItem identity = (IdentityItem) item;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        %>
                            <div><strong>Name:</strong> <%= identity.getFirstName() %> <%= identity.getLastName() %></div>
                            <div><strong>Email:</strong> <%= identity.getEmail() != null ? identity.getEmail() : "N/A" %></div>
                            <% if (identity.getPassportNumber() != null && !identity.getPassportNumber().isEmpty()) { %>
                                <div>
                                    <strong>Passport:</strong> 
                                    <span class="sensitive-field" id="passport_<%= item.getId() %>" data-masked="true" data-actual="<%= identity.getPassportNumber() != null ? identity.getPassportNumber().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;") : "" %>"><%= SensitiveDataProxy.maskStatic(identity.getPassportNumber()) %></span>
                                    <button class="btn btn-unmask" onclick="toggleMask(this, '<%= item.getId() %>', 'passport')">Show</button>
                                    <button class="btn btn-copy" onclick="copyData('<%= item.getId() %>', 'passport')">Copy</button>
                                </div>
                                <div><strong>Passport Expires:</strong> <%= identity.getPassportExpirationDate() != null ? sdf.format(identity.getPassportExpirationDate()) : "N/A" %></div>
                            <% } %>
                            <% if (identity.getLicenseNumber() != null && !identity.getLicenseNumber().isEmpty()) { %>
                                <div>
                                    <strong>License:</strong> 
                                    <span class="sensitive-field" id="license_<%= item.getId() %>" data-masked="true" data-actual="<%= identity.getLicenseNumber() != null ? identity.getLicenseNumber().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;") : "" %>"><%= SensitiveDataProxy.maskStatic(identity.getLicenseNumber()) %></span>
                                    <button class="btn btn-unmask" onclick="toggleMask(this, '<%= item.getId() %>', 'license')">Show</button>
                                    <button class="btn btn-copy" onclick="copyData('<%= item.getId() %>', 'license')">Copy</button>
                                </div>
                                <div><strong>License Expires:</strong> <%= identity.getLicenseExpirationDate() != null ? sdf.format(identity.getLicenseExpirationDate()) : "N/A" %></div>
                            <% } %>
                            <% if (identity.getSocialSecurityNumber() != null && !identity.getSocialSecurityNumber().isEmpty()) { %>
                                <div>
                                    <strong>SSN:</strong> 
                                    <span class="sensitive-field" id="ssn_<%= item.getId() %>" data-masked="true" data-actual="<%= identity.getSocialSecurityNumber() != null ? identity.getSocialSecurityNumber().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;") : "" %>"><%= SensitiveDataProxy.maskStatic(identity.getSocialSecurityNumber()) %></span>
                                    <button class="btn btn-unmask" onclick="toggleMask(this, '<%= item.getId() %>', 'ssn')">Show</button>
                                    <button class="btn btn-copy" onclick="copyData('<%= item.getId() %>', 'ssn')">Copy</button>
                                </div>
                            <% } %>
                            <% if (identity.getAddress() != null && !identity.getAddress().isEmpty()) { %>
                                <div><strong>Address:</strong> <%= identity.getAddress() %></div>
                            <% } %>
                            <% if (identity.getPhone() != null && !identity.getPhone().isEmpty()) { %>
                                <div><strong>Phone:</strong> <%= identity.getPhone() %></div>
                            <% } %>
                            <% if (identity.getNotes() != null && !identity.getNotes().isEmpty()) { %>
                                <div><strong>Notes:</strong> <%= identity.getNotes() %></div>
                            <% } %>
                        <% } else if (item instanceof SecureNoteItem) {
                            SecureNoteItem note = (SecureNoteItem) item;
                        %>
                            <div><strong>Content:</strong> <%= note.getContent().length() > 200 ? note.getContent().substring(0, 200) + "..." : note.getContent() %></div>
                        <% } %>
                        <div style="color: #999; font-size: 12px; margin-top: 10px;">
                            Created: <%= new SimpleDateFormat("yyyy-MM-dd HH:mm").format(item.getCreatedDate()) %>
                        </div>
                    </div>
                </div>
            <% } %>
        <% } %>
    </div>

    <!-- Add/Edit Modal -->
    <div id="itemModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2 id="modalTitle">Add New Item</h2>
                <span class="close" onclick="closeModal()">&times;</span>
            </div>
            <form id="itemForm" method="post" action="vault">
                <input type="hidden" name="action" id="formAction" value="add">
                <input type="hidden" name="itemId" id="formItemId">
                
                <div class="form-group">
                    <label>Item Type:</label>
                    <select name="type" id="itemType" onchange="updateFormFields()" required <% if (request.getParameter("edit") != null) { %>disabled<% } %>>
                        <option value="">Select Type</option>
                        <option value="Login">Login</option>
                        <option value="CreditCard">Credit Card</option>
                        <option value="Identity">Identity</option>
                        <option value="SecureNote">Secure Note</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label>Name:</label>
                    <input type="text" name="name" id="itemName" required>
                </div>
                
                <!-- Login Fields -->
                <div id="loginFields" style="display:none;">
                    <div class="form-group">
                        <label>Username:</label>
                        <input type="text" name="username">
                    </div>
                    <div class="form-group">
                        <label>Password:</label>
                        <input type="password" name="password">
                    </div>
                    <div class="form-group">
                        <label>URL:</label>
                        <input type="url" name="url">
                    </div>
                    <div class="form-group">
                        <label>Notes:</label>
                        <textarea name="notes"></textarea>
                    </div>
                </div>
                
                <!-- Credit Card Fields -->
                <div id="creditCardFields" style="display:none;">
                    <div class="form-group">
                        <label>Card Number:</label>
                        <input type="text" name="cardNumber" maxlength="19">
                    </div>
                    <div class="form-group">
                        <label>Cardholder Name:</label>
                        <input type="text" name="cardholderName">
                    </div>
                    <div class="form-group">
                        <label>CVV:</label>
                        <input type="text" name="cvv" maxlength="4">
                    </div>
                    <div class="form-group">
                        <label>Expiration Date:</label>
                        <input type="date" name="expirationDate">
                    </div>
                    <div class="form-group">
                        <label>Notes:</label>
                        <textarea name="notes"></textarea>
                    </div>
                </div>
                
                <!-- Identity Fields -->
                <div id="identityFields" style="display:none;">
                    <div class="form-group">
                        <label>First Name:</label>
                        <input type="text" name="firstName">
                    </div>
                    <div class="form-group">
                        <label>Last Name:</label>
                        <input type="text" name="lastName">
                    </div>
                    <div class="form-group">
                        <label>Passport Number:</label>
                        <input type="text" name="passportNumber">
                    </div>
                    <div class="form-group">
                        <label>Passport Expiration Date:</label>
                        <input type="date" name="passportExpirationDate">
                    </div>
                    <div class="form-group">
                        <label>License Number:</label>
                        <input type="text" name="licenseNumber">
                    </div>
                    <div class="form-group">
                        <label>License Expiration Date:</label>
                        <input type="date" name="licenseExpirationDate">
                    </div>
                    <div class="form-group">
                        <label>Social Security Number:</label>
                        <input type="text" name="ssn" maxlength="11">
                    </div>
                    <div class="form-group">
                        <label>Address:</label>
                        <input type="text" name="address">
                    </div>
                    <div class="form-group">
                        <label>Phone:</label>
                        <input type="tel" name="phone">
                    </div>
                    <div class="form-group">
                        <label>Email:</label>
                        <input type="email" name="email">
                    </div>
                    <div class="form-group">
                        <label>Notes:</label>
                        <textarea name="notes"></textarea>
                    </div>
                </div>
                
                <!-- Secure Note Fields -->
                <div id="secureNoteFields" style="display:none;">
                    <div class="form-group">
                        <label>Content:</label>
                        <textarea name="content" rows="10"></textarea>
                    </div>
                </div>
                
                <button type="submit" style="background: #28a745; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer;">Save</button>
                <button type="button" onclick="closeModal()" style="background: #6c757d; color: white; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; margin-left: 10px;">Cancel</button>
            </form>
        </div>
    </div>

    <div class="copy-success" id="copySuccess">Copied to clipboard!</div>

    <script>
        let clipboardTimeout = null;
        const CLIPBOARD_TIMEOUT = 60 * 1000; // 1 minute
        
        function showAddModal() {
            document.getElementById('modalTitle').textContent = 'Add New Item';
            document.getElementById('formAction').value = 'add';
            document.getElementById('itemForm').reset();
            document.getElementById('formItemId').value = '';
            document.getElementById('itemModal').style.display = 'block';
            updateFormFields();
        }
        
        function showEditModal(itemId, itemType) {
            document.getElementById('modalTitle').textContent = 'Edit Item';
            document.getElementById('formAction').value = 'update';
            document.getElementById('formItemId').value = itemId;
            document.getElementById('itemType').value = itemType;
            document.getElementById('itemModal').style.display = 'block';
            updateFormFields();
            
            // Load item data
            fetch('api/get-item?itemId=' + itemId)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('itemName').value = data.name || '';
                    
                    if (data.type === 'Login') {
                        document.querySelector('#loginFields input[name="username"]').value = data.username || '';
                        document.querySelector('#loginFields input[name="password"]').value = data.password || '';
                        document.querySelector('#loginFields input[name="url"]').value = data.url || '';
                        document.querySelector('#loginFields textarea[name="notes"]').value = data.notes || '';
                    } else if (data.type === 'CreditCard') {
                        document.querySelector('#creditCardFields input[name="cardNumber"]').value = data.cardNumber || '';
                        document.querySelector('#creditCardFields input[name="cardholderName"]').value = data.cardholderName || '';
                        document.querySelector('#creditCardFields input[name="cvv"]').value = data.cvv || '';
                        document.querySelector('#creditCardFields input[name="expirationDate"]').value = data.expirationDate || '';
                        document.querySelector('#creditCardFields textarea[name="notes"]').value = data.notes || '';
                    } else if (data.type === 'Identity') {
                        document.querySelector('#identityFields input[name="firstName"]').value = data.firstName || '';
                        document.querySelector('#identityFields input[name="lastName"]').value = data.lastName || '';
                        document.querySelector('#identityFields input[name="passportNumber"]').value = data.passportNumber || '';
                        document.querySelector('#identityFields input[name="passportExpirationDate"]').value = data.passportExpirationDate || '';
                        document.querySelector('#identityFields input[name="licenseNumber"]').value = data.licenseNumber || '';
                        document.querySelector('#identityFields input[name="licenseExpirationDate"]').value = data.licenseExpirationDate || '';
                        document.querySelector('#identityFields input[name="ssn"]').value = data.ssn || '';
                        document.querySelector('#identityFields input[name="address"]').value = data.address || '';
                        document.querySelector('#identityFields input[name="phone"]').value = data.phone || '';
                        document.querySelector('#identityFields input[name="email"]').value = data.email || '';
                        document.querySelector('#identityFields textarea[name="notes"]').value = data.notes || '';
                    } else if (data.type === 'SecureNote') {
                        document.querySelector('#secureNoteFields textarea[name="content"]').value = data.content || '';
                    }
                })
                .catch(error => {
                    console.error('Error loading item:', error);
                    alert('Failed to load item data');
                });
        }
        
        function updateFormFields() {
            const type = document.getElementById('itemType').value;
            document.getElementById('loginFields').style.display = type === 'Login' ? 'block' : 'none';
            document.getElementById('creditCardFields').style.display = type === 'CreditCard' ? 'block' : 'none';
            document.getElementById('identityFields').style.display = type === 'Identity' ? 'block' : 'none';
            document.getElementById('secureNoteFields').style.display = type === 'SecureNote' ? 'block' : 'none';
        }
        
        function closeModal() {
            document.getElementById('itemModal').style.display = 'none';
        }
        
        function deleteItem(itemId) {
            if (confirm('Are you sure you want to delete this item?')) {
                const form = document.createElement('form');
                form.method = 'post';
                form.action = 'vault';
                const actionInput = document.createElement('input');
                actionInput.type = 'hidden';
                actionInput.name = 'action';
                actionInput.value = 'delete';
                const idInput = document.createElement('input');
                idInput.type = 'hidden';
                idInput.name = 'itemId';
                idInput.value = itemId;
                form.appendChild(actionInput);
                form.appendChild(idInput);
                document.body.appendChild(form);
                form.submit();
            }
        }
        
        
        function toggleMask(btn, itemId, field) {
            const element = document.getElementById(field + '_' + itemId);
            const button = btn;
            const actualValue = element && element.dataset ? (element.dataset.actual || '') : '';

            // If dataset.masked is missing or explicitly 'true', treat as masked
            if (!element.dataset.masked || element.dataset.masked === 'true') {
                // Unmask
                element.textContent = actualValue;
                element.dataset.masked = 'false';
                button.textContent = 'Hide';
            } else {
                // Mask
                const masked = maskValue(actualValue);
                element.textContent = masked;
                element.dataset.masked = 'true';
                button.textContent = 'Show';
            }
        }
        
        function maskValue(value) {
            if (!value || value.length === 0) return '';
            // Mask all characters - show nothing until user unmasks
            return '*'.repeat(value.length);
        }
        
        function copyData(itemId, field) {
            fetch('api/copy?itemId=' + itemId + '&field=' + field)
                .then(response => {
                    if (!response.ok) throw new Error('Failed to copy');
                    return response.text();
                })
                .then(text => {
                    // Copy to clipboard
                    navigator.clipboard.writeText(text).then(() => {
                        showCopySuccess();
                        // Auto-delete from clipboard after timeout
                        clearTimeout(clipboardTimeout);
                        clipboardTimeout = setTimeout(() => {
                            navigator.clipboard.writeText('').catch(() => {});
                        }, CLIPBOARD_TIMEOUT);
                    }).catch(err => {
                        // Fallback for older browsers
                        const textArea = document.createElement('textarea');
                        textArea.value = text;
                        document.body.appendChild(textArea);
                        textArea.select();
                        document.execCommand('copy');
                        document.body.removeChild(textArea);
                        showCopySuccess();
                        clearTimeout(clipboardTimeout);
                        clipboardTimeout = setTimeout(() => {
                            const textArea2 = document.createElement('textarea');
                            textArea2.value = '';
                            document.body.appendChild(textArea2);
                            textArea2.select();
                            document.execCommand('copy');
                            document.body.removeChild(textArea2);
                        }, CLIPBOARD_TIMEOUT);
                    });
                })
                .catch(error => {
                    alert('Failed to copy: ' + error);
                });
        }
        
        function showCopySuccess() {
            const successDiv = document.getElementById('copySuccess');
            successDiv.style.display = 'block';
            setTimeout(() => {
                successDiv.style.display = 'none';
            }, 2000);
        }
        
        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('itemModal');
            if (event.target === modal) {
                closeModal();
            }
        }
        
        // Check for expiration warnings
        function checkExpirationWarnings() {
            fetch('api/expiration-warnings')
                .then(response => response.json())
                .then(data => {
                    const warningsDiv = document.getElementById('expirationWarnings');
                    if (data.warnings && data.warnings.length > 0) {
                        warningsDiv.innerHTML = '<strong>Expiration Warnings:</strong><ul>' +
                            data.warnings.map(w => '<li>' + w + '</li>').join('') + '</ul>';
                        warningsDiv.style.display = 'block';
                    } else {
                        warningsDiv.style.display = 'none';
                    }
                })
                .catch(error => {
                    console.error('Error checking expiration warnings:', error);
                });
        }
        
        // Check warnings on page load
        checkExpirationWarnings();
        
        // Check warnings periodically
        setInterval(checkExpirationWarnings, 60000); // Every minute
        
        // Track user activity for auto-lock
        let activityTimeout;
        const INACTIVITY_TIMEOUT = 5 * 60 * 1000; // 5 minutes
        
        function resetActivityTimer() {
            clearTimeout(activityTimeout);
            activityTimeout = setTimeout(() => {
                // Session expired, redirect to login
                window.location.href = 'login.jsp?timeout=true';
            }, INACTIVITY_TIMEOUT);
        }
        
        // Track mouse and keyboard activity
        ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart'].forEach(event => {
            document.addEventListener(event, resetActivityTimer, true);
        });
        
        // Initialize timer
        resetActivityTimer();
    </script>
</body>
</html>
