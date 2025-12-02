package com.mypass;

import java.util.Date;

/**
 * Vault item for storing secure text notes.
 */
public class SecureNoteItem extends VaultItem {
    private String content; // Note text (can be multi-line)
    
    public SecureNoteItem(String name, String content) {
        super(name);
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.modifiedDate = new Date();
    }
    
    @Override
    public String getType() {
        return "Secure Note";
    }
}
