package de.konqi.remailer.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeCallbackServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import de.konqi.remailer.Utils;
import de.konqi.remailer.db.EmailMapping;
import de.konqi.remailer.db.OfyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2Callback extends AbstractAppEngineAuthorizationCodeCallbackServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
            throws ServletException, IOException {
        // Store mapping email to user id in datastore
        User user = UserServiceFactory.getUserService().getCurrentUser();
        EmailMapping mapping = new EmailMapping();
        mapping.setOwnerEmail(user.getEmail());
        mapping.setUserId(user.getUserId());
        OfyService.ofy().save().entity(mapping).now();

        resp.sendRedirect("/oauth2/settings.html");
    }

    @Override
    protected void onError(
            HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
            throws ServletException, IOException {
        String nickname = UserServiceFactory.getUserService().getCurrentUser().getNickname();
        resp.getWriter().print("<h3>" + nickname + ", why don't you want to play with me?</h1>");
        resp.setStatus(200);
        resp.addHeader("Content-Type", "text/html");
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