import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HTTPServer {

    private static final int PORT = 8081;

    // home handler for the path
    private static HttpHandler homeHandler(){

        return (exchange) -> {
            OutputStream responseBody = exchange.getResponseBody();

            byte[] response = "HTTP/1.1 200 OK\r\n".getBytes();
            exchange.sendResponseHeaders(200, response.length);
            responseBody.write(response);
            responseBody.close();
        };
    }

    private static HttpHandler userContext(){

        return (exchange) -> {
            OutputStream responseBody = exchange.getResponseBody();

            byte[] response = ("User " + exchange.getRequestURI().getPath().substring(exchange.getRequestURI().getPath().lastIndexOf("/") + 1) + "\r\n").getBytes();
            exchange.sendResponseHeaders(200, response.length);
            responseBody.write(response);
            responseBody.close();
        };
    }

    private static HttpHandler pageContext() throws Exception {

        FileReader reader = new FileReader("./index.html");

        char[] buffer = new char[1024];

        reader.read(buffer);

        reader.close();

        byte[] response = String.valueOf(buffer).getBytes();

        return (exchange) -> {
            OutputStream responseBody = exchange.getResponseBody();

            exchange.sendResponseHeaders(200, response.length);
            responseBody.write(response);
            responseBody.close();
        };
    }

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create();

        server.bind(new InetSocketAddress(PORT), 5);
        server.setExecutor(Executors.newFixedThreadPool(10));


        server.createContext("/home", homeHandler());

        server.createContext("/page", pageContext());

        server.createContext("/user", userContext());
        server.start();

        System.out.println("HTTP server started on port " + PORT);
    }
}
