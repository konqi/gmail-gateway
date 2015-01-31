package de.konqi.remailer.db;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author konqi
 */
public class OfyService {
    private static final Logger logger = LoggerFactory.getLogger(OfyService.class);

    static {
        try {
            ObjectifyService.register(EmailMapping.class);
            ObjectifyService.register(Handle.class);
        } catch(Exception e){
            logger.error("Error registering database classes.", e);
        }
    }

    public static Objectify ofy(){
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
