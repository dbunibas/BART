package bart.persistence.xml;

public class IllegalSchemaException extends RuntimeException {
    
    public IllegalSchemaException() {}
    
    public IllegalSchemaException(String message) {
        super(message);
    }
    
    public IllegalSchemaException(Throwable t) {
        super(t);
    }

}
