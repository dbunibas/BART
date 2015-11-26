package bart.exceptions;

public class ErrorGeneratorException extends RuntimeException {
    
    public ErrorGeneratorException() {
        super();
    }
    
    public ErrorGeneratorException(String s) {
        super(s);
    }
    
    public ErrorGeneratorException(Exception e) {
        super(e);
    }

}
