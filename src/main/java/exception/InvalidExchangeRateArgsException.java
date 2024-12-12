package exception;

public class InvalidExchangeRateArgsException extends RuntimeException {
    public InvalidExchangeRateArgsException(String message) {
        super(message);
    }
}
