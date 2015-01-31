package de.konqi.remailer.auth;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import de.konqi.remailer.AppengineEnv;
import de.konqi.remailer.Utils;
import de.konqi.remailer.db.EmailMapping;
import de.konqi.remailer.db.Handle;
import de.konqi.remailer.db.OfyService;
import de.konqi.remailer.db.Sender;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class SettingsPage extends AbstractAppEngineAuthorizationCodeServlet {
    private static final Logger logger = LoggerFactory.getLogger(SettingsPage.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Prep vars
        request.setAttribute("mailDomain", AppengineEnv.MAIL_DOMAIN);
        User user = UserServiceFactory.getUserService().getCurrentUser();
        EmailMapping mapping = OfyService.ofy().load().key(Key.create(EmailMapping.class, user.getEmail())).now();
        if(mapping != null){
            request.setAttribute("mapping", mapping);

            List<Handle> handles = OfyService.ofy().load().type(Handle.class).ancestor(mapping).list();
            request.setAttribute("handles", handles);
        }

        request.getRequestDispatcher("/WEB-INF/settings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = UserServiceFactory.getUserService().getCurrentUser();
        EmailMapping mapping = OfyService.ofy().load().key(Key.create(EmailMapping.class, user.getEmail())).now();

        String type = req.getParameter("type");
        if(type == null){
            resp.sendError(HttpStatus.SC_BAD_REQUEST);
        }

        logger.info(type);
        // Parse request
        try {
            Object o = Utils.OBJECT_MAPPER.readValue(req.getInputStream(), Class.forName("de.konqi.remailer.db." + type));
            if(o instanceof Sender){
                Sender sender = (Sender)o;
                mapping.setSenderEmail(sender.getSender());
                OfyService.ofy().save().entity(mapping).now();
                logger.info("Changed sender email to " + sender.getSender() + " for owner " + user.getEmail());

                resp.setStatus(HttpStatus.SC_ACCEPTED);
            } else if(o instanceof Handle) {
                Handle handle = (Handle)o;
                handle.setParent(Key.create(mapping));
                if(handle.getAction().equals("DELETE")){
                    OfyService.ofy().delete().entity(handle).now();
                    logger.info("Handle deleted for for owner " + user.getEmail());
                } else {
                    OfyService.ofy().save().entity(handle).now();
                    logger.info("Handle added for for owner " + user.getEmail());
                }

                resp.setStatus(HttpStatus.SC_ACCEPTED);
            } else {
                logger.error("Invalid request body");
                resp.sendError(HttpStatus.SC_BAD_REQUEST);
            }
        } catch (ClassNotFoundException e) {
            logger.error("Invalid request body", e);
            resp.sendError(HttpStatus.SC_BAD_REQUEST);
        }
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        return Utils.getRedirectUri(req);
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws IOException {
        return Utils.newFlow();
    }
}