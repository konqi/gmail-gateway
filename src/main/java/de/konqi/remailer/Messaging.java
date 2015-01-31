package de.konqi.remailer;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author konqi
 */
public class Messaging {
    private static final Logger logger = LoggerFactory.getLogger(Messaging.class);

    /**
     * Gets an authorized Gmail client, ready to Gmail API stuff
     * @param credential Authorized RequestInitializer object
     * @return ready-to-use Gmail API client
     */
    public static Gmail getGmailClient(Credential credential){
        return new Gmail.Builder(Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential).setApplicationName(AppengineEnv.APP_ID).build();
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId  User's email address. The special value "me"
     *                can be used to indicate the authenticated user.
     * @param email   Email to be sent.
     * @throws MessagingException
     * @throws IOException
     */
    public static void sendMessage(Gmail service, String userId, MimeMessage email)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
    }

    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64 encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    private static Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Sens a service message
     * @param sender
     * @param recipient
     * @param subject
     * @param body
     */
    public static void sendServiceMessage(String sender, String recipient, String subject, String body){
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(sender));
            msg.addRecipient(MimeMessage.RecipientType.TO,
                    new InternetAddress(recipient));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);

        } catch (AddressException e) {
            logger.error("Could not send message", e);
        } catch (MessagingException e) {
            logger.error("Could not send message", e);
        }
    }
}
