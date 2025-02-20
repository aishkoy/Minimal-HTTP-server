package models;

import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;

public class Profile {
    private final String userIP;
    private final int userPort;
    private final String userAgent;
    private final String serverHost;
    private final int serverPort;
    private final String method;

    private Profile(String userIP, int userPort, String userAgent,
                        String serverHost, int serverPort, String method) {
        this.userIP = userIP;
        this.userPort = userPort;
        this.userAgent = userAgent;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.method = method;
    }

    public static Profile from(HttpExchange exchange) {
        InetSocketAddress remoteAddr = exchange.getRemoteAddress();
        InetSocketAddress localAddr = exchange.getLocalAddress();

        return new Profile(
                remoteAddr.getAddress().getHostAddress(),
                remoteAddr.getPort(),
                exchange.getRequestHeaders().getFirst("User-Agent"),
                localAddr.getHostName(),
                localAddr.getPort(),
                exchange.getRequestMethod()
        );
    }

    public String formatResponse() {
        return String.format(
                "🔹 Информация о пользователе:%n" +
                        "📌 IP: %s%n" +
                        "📌 Порт: %d%n" +
                        "📌 User-Agent: %s%n%n" +
                        "🔹 Информация о сервере:%n" +
                        "📌 Сервер работает на %s:%d%n" +
                        "Метод: %s%n",
                userIP, userPort, userAgent, serverHost, serverPort, method
        );
    }
}