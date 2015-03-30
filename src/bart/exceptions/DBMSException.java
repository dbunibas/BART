package bart.exceptions;

public class DBMSException extends RuntimeException {
    
    public DBMSException() {
        super();
    }
    
    public DBMSException(String message) {
        super(message);
    }

}