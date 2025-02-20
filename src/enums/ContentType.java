package enums;

public enum ContentType {
    HTML("text/html; charset=utf-8"),
    CSS("text/css; charset=utf-8"),
    PLAIN_TEXT("text/plain; charset=utf-8"),
    JPEG("image/jpeg"),
    PNG("image/png"),;

    private final String mimeType;

    ContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static ContentType getTypeFromPath(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".html")) return HTML;
        if (path.endsWith(".css")) return CSS;
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return JPEG;
        if (path.endsWith(".png")) return PNG;
        return PLAIN_TEXT;
    }
}
