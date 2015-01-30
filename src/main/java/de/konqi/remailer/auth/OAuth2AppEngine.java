package de.konqi.remailer.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.appengine.api.users.UserServiceFactory;
import de.konqi.remailer.Utils;
import de.konqi.remailer.db.EmailMapping;
import de.konqi.remailer.db.OfyService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OAuth2AppEngine extends AbstractAppEngineAuthorizationCodeServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // do stuff
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();

        EmailMapping mapping = new EmailMapping();
        mapping.setEmail(UserServiceFactory.getUserService().getCurrentUser().getEmail());
        mapping.setUserId(userId);

        OfyService.ofy().save().entity(mapping).now();

        request.getRequestDispatcher("/WEB-INF/settings.jsp").forward(request, response);
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