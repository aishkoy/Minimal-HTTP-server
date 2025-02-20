package utils;

import com.sun.net.httpserver.HttpExchange;
import enums.ContentType;
import enums.HttpStatus;
import models.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class RequestHandler {
    private static final Path HOME_PATH = Path.of("homework/").toAbsolutePath();
    private static final Path IMAGES_PATH = Path.of("images/").toAbsolutePath();
    private final ResponseWriter responseWriter = new ResponseWriter();

    public void handleProfileRequest(HttpExchange exchange) {
        Profile info = Profile.from(exchange);
        responseWriter.sendResponse(exchange, info.formatResponse(), ContentType.PLAIN_TEXT, HttpStatus.OK);
    }

    public void handleAppsRequest(HttpExchange exchange) {
        try {
            List<String> apps = List.of(
                    "Instagram", "Telegram", "Threads", "Twitter", "WhatsApp", "TikTok"
            );

            Random random = new Random();
            String app = apps.get(random.nextInt(apps.size()));

            Path imagePath = IMAGES_PATH.resolve(app.toLowerCase() + ".png");

            Files.createDirectories(IMAGES_PATH);

            if (!Files.exists(imagePath)) {
                String message = String.format("Your random app for today: %s%n \n%s", app, HttpStatus.NO_CONTENT);
                responseWriter.sendResponse(exchange, message, ContentType.PLAIN_TEXT, HttpStatus.NO_CONTENT);
                return;
            }

            sendFile(exchange, imagePath);
        } catch (IOException e) {
            responseWriter.sendResponse(exchange, ContentType.PLAIN_TEXT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void handleMainRequest(HttpExchange exchange) {
        try {
            String some_path = exchange.getRequestURI().getPath();

            String path = "/".equals(some_path) ? "/index.html" : some_path ;
            Path filePath = resolveAndValidatePath(path);

            if (filePath == null) {
                responseWriter.sendResponse(exchange, ContentType.PLAIN_TEXT, HttpStatus.NOT_FOUND);
                return;
            }

            sendFile(exchange, filePath);
        } catch (IOException e) {
            responseWriter.sendResponse(exchange, ContentType.PLAIN_TEXT, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Path resolveAndValidatePath(String path) {
        Path filePath = Path.of(HOME_PATH + path).toAbsolutePath();
        return isFileValid(filePath) ? filePath : null;
    }

    private boolean isFileValid(Path path) {
        return path.startsWith(HOME_PATH) && Files.exists(path) && !Files.isDirectory(path);
    }

    private void sendFile(HttpExchange exchange, Path filePath) throws IOException {
        ContentType contentType = ContentType.getTypeFromPath(filePath.toString());
        exchange.getResponseHeaders().set("Content-Type", contentType.getMimeType());
        exchange.sendResponseHeaders(HttpStatus.OK.getCode(), Files.size(filePath));

        try (OutputStream os = exchange.getResponseBody();
             InputStream is = Files.newInputStream(filePath)) {
            is.transferTo(os);
        }
    }
}