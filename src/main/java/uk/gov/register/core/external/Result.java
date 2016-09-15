package uk.gov.register.core.external;

public abstract class Result<T> {
    private final T status;
    private final String message;

    public Result(T status, String message) {
        this.status = status;
        this.message = message;
    }

    public T getCommandStatus() {
        return status;
    }
    public String getMessage() {
        return message;
    }
}
