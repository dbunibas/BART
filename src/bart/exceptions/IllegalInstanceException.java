package bart.exceptions;

public class IllegalInstanceException extends RuntimeException {
    
    public IllegalInstanceException() {}
    
    public IllegalInstanceException(String message) {
        super(message);
    }
    
    public IllegalInstanceException(Throwable t) {
        super(t);
    }

}
