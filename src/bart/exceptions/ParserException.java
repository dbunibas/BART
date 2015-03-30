package bart.exceptions;

public class ParserException extends RuntimeException {
    
    public ParserException() {
        super();
    }
    
    public ParserException(String message) {
        super(message);
    }

    public ParserException(Exception e) {
        super(e.getMessage());
    }

    public ParserException(Throwable t) {
        super(t.getMessage());
    }
}
