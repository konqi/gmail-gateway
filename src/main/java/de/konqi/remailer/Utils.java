package de.konqi.remailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import com.google.appengine.api.search.checkers.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Utilities class for almost everything that
 * @author konqi
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final List<String> AUTH_SCOPES = Arrays.asList(GmailScopes.GMAIL_COMPOSE);
    public static final DataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();
    public static DataStore<Serializable> CREDENTIAL_STORE;

    private static GoogleClientSecrets clientSecrets;

    /**
     * static constructor 
     */
    static {
        try {
            CREDENTIAL_STORE = Utils.DATA_STORE_FACTORY.getDataStore(StoredCredential.DEFAULT_DATA_STORE_ID);
        } catch (IOException e) {
            logger.error("Could not get StoredCredential Store", e);
        }
    }

    /**
     * Gets data from client_secrets.json for authentication
     * @return
     * @throws IOException
     */
    public static GoogleClientSecrets getClientCredential() throws IOException {
        if (clientSecrets == null) {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(Utils.class.getResourceAsStream("/client_secrets.json")));
            Preconditions.checkArgument(!clientSecrets.getDetails().getClientId().startsWith("Enter ")
                            && !clientSecrets.getDetails().getClientSecret().startsWith("Enter "),
                    "Download client_secrets.json file from https://console.developers.google.com/"
                            + " into /src/main/resources/client_secrets.json");
        }

        return clientSecrets;
    }

    /**
     * Gets the redirect uri for oauth2 callback
     * @param req
     * @return
     */
    public static String getRedirectUri(HttpServletRequest req) {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/oauth2callback");
        return url.build();
    }

    /**
     * Initialize a new Google authorization flow
     * @return
     * @throws IOException
     */
    public static GoogleAuthorizationCodeFlow newFlow() throws IOException {
        return new GoogleAuthorizationCodeFlow.Builder(Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY,
                getClientCredential(), AUTH_SCOPES).setDataStoreFactory(
                DATA_STORE_FACTORY).setAccessType("offline").build();
    }
}
