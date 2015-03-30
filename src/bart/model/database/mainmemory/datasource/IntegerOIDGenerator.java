package bart.model.database.mainmemory.datasource;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerOIDGenerator {

    private static Logger logger = LoggerFactory.getLogger(IntegerOIDGenerator.class);
    
    private static Integer counter = 0;
    private static Map<String, OID> cache = new HashMap<String, OID>();

    public static void clearCache() {
        cache.clear();
    }

    public static void resetCounter() {
        counter = 0;
    }

    public static OID generateOIDForSkolemString(String string) {
        OID oid = cache.get(string);
        if (oid == null) {
            oid = getNextOID();
            oid.setSkolemString(string);
            cache.put(string, oid);
        }
        return oid;
    }
        
    public static OID getNextOID() {
        OID result = new OID(counter++);
        if (logger.isDebugEnabled()) logger.debug("Generating new OID: " + result);
        return result;
    }
    
}
