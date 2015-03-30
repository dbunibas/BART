package bart.exceptions;

public class NodeNotFoundException extends RuntimeException {
    
    public NodeNotFoundException() {}
    
    public NodeNotFoundException(String message) {
        super(message);
    }
    
    public NodeNotFoundException(Throwable t) {
        super(t);
    }

}
