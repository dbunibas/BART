package bart.generator;

public class DataGeneratorException extends RuntimeException {

    public DataGeneratorException() {
    }

    public DataGeneratorException(String message) {
        super(message);
    }

    public DataGeneratorException(Exception e) {
        super(e);
    }

}
