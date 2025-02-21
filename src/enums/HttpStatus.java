package enums;

public enum HttpStatus {
    OK(200, "OK"),
    NO_CONTENT(204, "No Content"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal models.Server Error");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
