package de.konqi.remailer.inbound;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.gmail.Gmail;
import com.googlecode.objectify.Key;
import de.konqi.remailer.Messaging;
import de.konqi.remailer.Utils;
import de.konqi.remailer.db.EmailMapping;
import de.konqi.remailer.db.OfyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author konqi
 */
public class MailServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MailServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            MimeMessage message = new MimeMessage(session, req.getInputStream());
            InternetAddress address = (InternetAddress)message.getFrom()[0];
            String sender = address.getAddress();
            logger.info("Message from " + sender);

            EmailMapping mapping = OfyService.ofy().load().key(Key.create(EmailMapping.class, sender)).now();
            if(mapping == null){
                logger.error("Unknown user " + sender);
                return;
            }

            logger.info("User Id " + mapping.getUserId());
            StoredCredential storedCredential = (StoredCredential) Utils.CREDENTIAL_STORE.get(mapping.getUserId());
            if(storedCredential == null) {
                logger.error("Unknown credentials for user " + mapping.getUserId());
                return;
            }

            logger.info("Found credentials for " + mapping.getUserId());

            // Rebuild credential from datastore
            DataStoreCredentialRefreshListener refreshListener = new DataStoreCredentialRefreshListener(mapping.getUserId(), Utils.DATA_STORE_FACTORY);
            GoogleCredential credential = new GoogleCredential.Builder().addRefreshListener(refreshListener).setTransport(Utils.HTTP_TRANSPORT).setJsonFactory(Utils.JSON_FACTORY).setClientSecrets(Utils.getClientCredential()).build();
            credential.setAccessToken(storedCredential.getAccessToken());
            credential.setRefreshToken(storedCredential.getRefreshToken());

            // Bounce message
            // TODO Work magic
            message.setRecipient(Message.RecipientType.TO, address);

            // Send message
            Gmail gmailClient = Messaging.getGmailClient(credential);
            Messaging.sendMessage(gmailClient, "me", message);

        } catch (MessagingException e) {
            logger.error("Could not extract message from request.", e);
        }
    }
}
