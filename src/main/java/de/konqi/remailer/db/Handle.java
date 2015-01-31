package de.konqi.remailer.db;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

/**
 * @author konqi
 */
@Entity
public class Handle {
    @Parent
    private Key<EmailMapping> parent;
    @Id
    private String handle;
    private String to;
    private String cc;
    private String bcc;

    public Key<EmailMapping> getParent() {
        return parent;
    }

    public void setParent(Key<EmailMapping> parent) {
        this.parent = parent;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }
}
