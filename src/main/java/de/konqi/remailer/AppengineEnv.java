package de.konqi.remailer;

import com.google.appengine.api.utils.SystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by konqi on 30.01.2015.
 */
public class AppengineEnv {
    private static final Logger logger = LoggerFactory.getLogger(AppengineEnv.class);

    // private static AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
    public static final boolean DEBUG = (SystemProperty.environment.value() !=
            SystemProperty.Environment.Value.Production);
    public static final String appId = SystemProperty.applicationId.get();
}
