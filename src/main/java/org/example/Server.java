package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Server {
    Socket socket;
    ServerSocket serverSocket;
    BufferedReader in;
    BufferedOutputStream out;
    List<String> setVlidPaths;

    Server() throws IOException {
        serverSocket = new ServerSocket();
    }

    public void setVlidPaths(List<String> setVlidPaths) {
        this.setVlidPaths = setVlidPaths;
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

//        }
    }

    public void dataHandling(String text) throws IOException {
        String requestLine = text;
        final var parts = requestLine.split(" ");

        if (parts.length == 3) {
            final var path = parts[1];
            if (!!this.setVlidPaths.contains(path)) {
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                // special case for classic
                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();

                    this.write("HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n", content);

                } else {
                    final var length = Files.size(filePath);
                    final var content = Files.readAllBytes(filePath);
                    this.write("HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n", content);
                }


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


}
