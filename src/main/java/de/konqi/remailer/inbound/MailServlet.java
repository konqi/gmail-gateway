package de.konqi.remailer.inbound;

import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.gmail.Gmail;
import com.googlecode.objectify.Key;
import de.konqi.remailer.AppengineEnv;
import de.konqi.remailer.Messaging;
import de.konqi.remailer.Utils;
import de.konqi.remailer.db.EmailMapping;
import de.konqi.remailer.db.Handle;
import de.konqi.remailer.db.OfyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

/**
 * @author konqi
 */
public class MailServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MailServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        InternetAddress address = null;
        MimeMessage message = null;
        try {
            message = new MimeMessage(session, req.getInputStream());
            address = (InternetAddress) message.getFrom()[0];
            String sender = address.getAddress();
            logger.info("Message from " + sender);

            EmailMapping mapping = OfyService.ofy().load().type(EmailMapping.class).filter("senderEmail", sender).first().now();
            if (mapping == null) {
                logger.error("Unknown user " + sender);
                throw new IllegalArgumentException("Unknown user " + sender);
            }

            logger.info("User Id " + mapping.getUserId());
            StoredCredential storedCredential = (StoredCredential) Utils.CREDENTIAL_STORE.get(mapping.getUserId());
            if (storedCredential == null) {
                logger.error("Unknown credentials for user " + mapping.getUserId());
                throw new IllegalArgumentException("Unknown credentials for user " + mapping.getUserId());
            }

            logger.info("Found credentials for " + mapping.getUserId());

            // Rebuild credential from datastore
            DataStoreCredentialRefreshListener refreshListener = new DataStoreCredentialRefreshListener(mapping.getUserId(), Utils.DATA_STORE_FACTORY);
            GoogleCredential credential = new GoogleCredential.Builder().addRefreshListener(refreshListener).setTransport(Utils.HTTP_TRANSPORT).setJsonFactory(Utils.JSON_FACTORY).setClientSecrets(Utils.getClientCredential()).build();
            credential.setAccessToken(storedCredential.getAccessToken());
            credential.setRefreshToken(storedCredential.getRefreshToken());

            // Get intended recipients
            String localAddress = req.getRequestURI().substring(req.getServletPath().length() + 1);
            localAddress = localAddress.substring(0, localAddress.indexOf('@'));
            logger.info("local address: " + localAddress);
            Handle handle = OfyService.ofy().load().key(Key.create(Key.create(mapping), Handle.class, localAddress)).now();
            String recipientTo = handle.getTo();
            String recipientCc = handle.getCc();
            String recipientBcc = handle.getBcc();

            if (recipientTo != null) {
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientTo));
            }
            if (recipientCc != null) {
                message.setRecipient(Message.RecipientType.CC, new InternetAddress(recipientCc));
            }
            if (recipientBcc != null) {
                message.setRecipient(Message.RecipientType.BCC, new InternetAddress(recipientBcc));
            }

            // Send message
            Gmail gmailClient = Messaging.getGmailClient(credential);
            Messaging.sendMessage(gmailClient, "me", message);

        } catch (MessagingException e) {
            logger.error("Could not extract message from request.", e);
        } catch (Exception e) {
            try {
                if (address != null) {
                    logger.info("There's been an invalid request. ", e);
                    // bounce message
                    String subject = "unknown subject";
                    if (message != null) {
                        subject = message.getSubject();
                    }
                    Messaging.sendServiceMessage(AppengineEnv.MAIL_DOMAIN, address.getAddress(), "Something is wrong!",
                            "I'm terribly sorry but i could not handle your last email. \n\n" +
                                    "The subject was '" + subject + "'.\n" +
                                    "The reason why processing failed was " + e.getMessage() + "\n\n" +
                                    "Please try again later.");
                } else {
                    logger.error("Could read original sender for service response mail.", e);
                }
            } catch (Exception innerException) {
                logger.error("Could not send notification mail", innerException);
            }
        }
    }
}
