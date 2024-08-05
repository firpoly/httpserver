package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Server {
    Socket socket;
    ServerSocket serverSocket;
    BufferedReader in;
    BufferedOutputStream out;
    List<String> setVlidPaths;
    Request request;

    private ConcurrentHashMap<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    Server() throws IOException {
        serverSocket = new ServerSocket();
    }

    public void setVlidPaths(List<String> setVlidPaths) {
        this.setVlidPaths = setVlidPaths;
    }

    public Handler findHandler(String method, String path) {
        var methodHandlers = handlers.get(method + " " + path.substring(0, path.indexOf('.')));
        if (methodHandlers == null) return null;

        return methodHandlers.get(path.substring(0, path.indexOf('.')));
    }

    public void listen(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        listenPort();
    }

    public void startListen(ServerSocket serverSocket) throws IOException {
        this.socket = serverSocket.accept();
        in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        out = new BufferedOutputStream(this.socket.getOutputStream());
    }

    public void listenPort() throws IOException {
        int threadBound = 64;

        ExecutorService executorService = Executors.newFixedThreadPool(64);
        for (int i = 0; i < threadBound + 1; i++) {
            startListen(this.serverSocket);
            Future future = executorService.submit(new Callable() {
                public Object call() throws Exception {
                    dataHandling(read());
                    return "result";
                }
            });
        }
        executorService.shutdown();

    }

    private void badRequest() throws IOException {
        var errorHandler = findHandler("GET", "/error.html");
        this.request.url = "/error.html";
        errorHandler.handle();
    }

    public void dataHandling(String text) throws IOException {
        String requestLine = text;
        var parts = requestLine.split(" ");
        if (parts.length == 3) {
            this.request = new Request(parts[0], parts[1], text);
            var handler = findHandler(parts[0], parts[1]);
            if (handler != null) {
                handler.handle();
            } else {
                badRequest();
            }
        }
    }

    public void write(String text, byte[] content) throws IOException {
        out.write(text.getBytes());
        if (content.length != 0) {
            out.write(content);
        }
        out.flush();
    }

    public String read() throws IOException {
        return in.readLine();
    }


    public void addHandler(String method, String path, Handler handler) {
        handlers.computeIfAbsent(method + " " + path, k -> new HashMap<>()).put(path, handler);
    }
}
