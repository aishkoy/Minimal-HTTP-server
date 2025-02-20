package utils;

import com.sun.net.httpserver.HttpExchange;
import enums.ContentType;
import enums.HttpStatus;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ResponseWriter {
    public void sendResponse(HttpExchange exchange, String response,
                             ContentType contentType, HttpStatus status) {
        try {
            exchange.getResponseHeaders().set("Content-Type", contentType.getMimeType());
            exchange.sendResponseHeaders(status.getCode(), response.getBytes(StandardCharsets.UTF_8).length);

            try (OutputStreamWriter writer = new OutputStreamWriter(exchange.getResponseBody(), StandardCharsets.UTF_8)) {
                writer.write(response);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
