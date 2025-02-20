import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        try {
            HttpServer server = makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HttpServer makeServer() throws IOException {
        String host = "127.0.0.1";
        InetSocketAddress addr = new InetSocketAddress(host, 8080);

        System.out.printf(
                "Сервер был запущен на %s:%d\n",
                addr.getHostName(),
                addr.getPort()
        );

        HttpServer server = HttpServer.create(addr, 50);
        System.out.println("\t\tУспешно");
        return server;
    }

    private static void initRoutes(HttpServer server) {
        server.createContext("/", Main::handleMainRequest);
        server.createContext("/apps", Main::handleAppsRequest);
        server.createContext("/apps/profile", Main::handleProfileRequest);
    }

    private static void handleProfileRequest(HttpExchange exchange) {
        InetSocketAddress addr = exchange.getRemoteAddress();
        String userIP = addr.getAddress().getHostAddress();
        int userPort = addr.getPort();

        Headers headers = exchange.getRequestHeaders();
        String userAgent = headers.getFirst("User-Agent");

        String serverHost = exchange.getLocalAddress().getHostName();
        int  serverPort = exchange.getLocalAddress().getPort();


        String response = String.format(
                "🔹 Информация о пользователе:\n" +
                        "📌 IP: %s\n" +
                        "📌 Порт: %d\n" +
                        "📌 User-Agent: %s\n\n" +
                        "🔹 Информация о сервере:\n" +
                        "📌 Сервер работает на %s:%d",
                userIP, userPort, userAgent, serverHost, serverPort
        );
        sendTextResponse(exchange, response);
    }

    private static void handleAppsRequest(HttpExchange exchange) {
        List<String> applications = List.of(
                "Instagram", "Telegram", "Threads", "Twitter", "WhatsApp", "TikTok"
        );

        String app = applications.get(new Random().nextInt(applications.size()));

        Path imagesDir = Path.of("images/").toAbsolutePath();
        Path imagePath = imagesDir.resolve(app.toLowerCase() + ".png");

        try {
            Files.createDirectories(imagesDir);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (!Files.exists(imagePath)) {
            sendTextResponse(exchange, "Your random app for today: " + app + "\nИзображение не найдено!");
            return;
        }

        try {
            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, Files.size(imagePath));

            try (OutputStream os = exchange.getResponseBody();
                 InputStream imageStream = Files.newInputStream(imagePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = imageStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleMainRequest(HttpExchange exchange) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);

            try (PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();

                String path = exchange.getHttpContext().getPath();
                write(writer, "HTTP method", method);
                write(writer, "Request", uri.toString());
                write(writer, "Handler", path);
                writeHeaders(writer, "Request headers", exchange.getRequestHeaders());
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PrintWriter getWriterFrom(HttpExchange exchange) throws IOException {
        OutputStream os = exchange.getResponseBody();
        Charset charset = StandardCharsets.UTF_8;
        return new PrintWriter(os, false, charset);
    }

    private static void write(Writer writer, String message, String method) {
        String body = String.format("%s: %s\n\n", message, method);
        try {
            writer.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((key, value) -> write(writer, "\t" + key, value.toString()));
    }

    private static void sendTextResponse(HttpExchange exchange, String response) {
        try {
            exchange.getResponseHeaders().add("Content-Type", "text/plain" + "; charset=utf-8");
            int length = 0;
            exchange.sendResponseHeaders(200, length);
            try (PrintWriter writer = getWriterFrom(exchange)) {
                writer.write(response);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
