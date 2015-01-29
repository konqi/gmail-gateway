package de.konqi.remailer;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.gmail.Gmail;

/**
 * Created by konqi on 29.01.2015.
 */
public class GmailServiceWrapper {
    private static Gmail.Builder getGmailClient(){
        GoogleCredential googleCredential = new GoogleCredential.Builder().setTransport(Utils.HTTP_TRANSPORT).setJsonFactory(Utils.JSON_FACTORY).build();

        return new Gmail.Builder(Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, googleCredential);
    }
}
