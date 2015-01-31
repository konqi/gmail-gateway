package de.konqi.remailer.db;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * @author konqi
 */
@Entity
public class EmailMapping {
    @Id
    private String ownerEmail;
    @Index
    private String senderEmail;
    private String userId;

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String email) {
        this.senderEmail = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }
}
