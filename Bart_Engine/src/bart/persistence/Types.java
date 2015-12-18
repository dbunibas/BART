package bart.persistence;

import speedy.model.database.mainmemory.datasource.NullValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import speedy.exceptions.DAOException;

public class Types {

    private static Logger logger = LoggerFactory.getLogger(Types.class);

    public final static String BOOLEAN = "boolean";
    public final static String STRING = "string";
    public final static String INTEGER = "integer";
    public final static String LONG = "long";
    public final static String DOUBLE = "double";
    public final static String DATE = "date";
    public final static String DATETIME = "datetime";
    public final static String ANY = "any";

    public static Object getTypedValue(String type, Object value) throws DAOException {
        if (value == null || value.toString().equalsIgnoreCase("NULL")) {
            return NullValueFactory.getNullValue();
        }
        if (type.equals(BOOLEAN)) {
            return Boolean.parseBoolean(value.toString());
        }
        if (type.equals(STRING)) {
            return value.toString();
        }
        if (type.equals(INTEGER)) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException ex) {
                logger.error(ex.getLocalizedMessage());
                throw new DAOException(ex.getMessage());
            }
        }
        if (type.equals(DOUBLE)) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ex) {
                logger.error(ex.getLocalizedMessage());
                throw new DAOException(ex.getMessage());
            }
        }
        if (type.equals(DATE) || type.equals(DATETIME)) {
            return value.toString();
//            try {
//                return DateFormat.getDateInstance().parse(value.toString());
//            } catch (ParseException ex) {
//                logger.error(ex);
//                throw new DAOException(ex.getMessage());
//            }
        }
        return value.toString();
    }

}
