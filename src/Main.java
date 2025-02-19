import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try{
            HttpServer server = makeServer();
            initRoutes(server);
            server.start();
        } catch (IOException e){
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
        server.createContext("/", exchange -> handleRequest(exchange));
        server.createContext("/apps/", exchange -> handleRequest(exchange));
        server.createContext("/apps/profile", exchange -> handleRequest(exchange));
    }

    private static void handleRequest(HttpExchange exchange) {
        try{
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
            int responseCode = 200;
            int length = 0;
            exchange.sendResponseHeaders(responseCode, length);

            try(PrintWriter writer = getWriterFrom(exchange)) {
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();

                String path = exchange.getHttpContext().getPath();
                write(writer, "HTTP method",method);
                write(writer, "Request", uri.toString());
                write(writer, "Handler", path);
                writeHeaders(writer, "Request headers", exchange.getRequestHeaders());
                writer.flush();
            }
        } catch (IOException e){
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
        try{
            writer.write(body);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void writeHeaders(Writer writer, String type, Headers headers) {
        write(writer, type, "");
        headers.forEach((key, value) -> write(writer, "\t" + key, value.toString()));
    }
}
