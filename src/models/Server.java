package models;

import com.sun.net.httpserver.HttpServer;
import utils.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private final HttpServer httpServer;
    private final RequestHandler requestHandler;
    private final InetSocketAddress addr;

    public Server(String host, int port, int backlog) throws IOException {
        this.requestHandler = new RequestHandler();
        this.addr = new InetSocketAddress(host, port);
        this.httpServer = HttpServer.create(addr, backlog);
        initRoutes();
    }

    public void start() {
        httpServer.start();
        System.out.printf(
                "Сервер был запущен на 'http://%s:%d'\n",
                addr.getHostName(),
                addr.getPort()
        );
    }

    private void initRoutes() {
        httpServer.createContext("/", requestHandler::handleMainRequest);
        httpServer.createContext("/apps", requestHandler::handleAppsRequest);
        httpServer.createContext("/apps/profile", requestHandler::handleProfileRequest);
    }
}
