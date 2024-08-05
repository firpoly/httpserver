package org.example;

import org.apache.http.NameValuePair;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class Main {
    public static void main(String[] args) {
        final var validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

        try {
            Server server = new Server();
            server.setVlidPaths(validPaths);
            // добавление хендлеров (обработчиков)
            Handler handler = () -> {
                try {
                    var tempUrl = server.request.getUrl();
                    final long length = Files.size(Path.of(".", "public", tempUrl));
                    final var content = Files.readAllBytes(Path.of(".", "public", tempUrl));
                    final var mimeType = Files.probeContentType(Path.of(".", "public", tempUrl));
                    server.write("HTTP/1.1 200 OK\r\n" + "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n", content);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            };
            server.addHandler("GET", "/index", handler);
            server.addHandler("GET", "/forms", handler);
            server.addHandler("GET", "/error", handler);
            handler = () -> {
                try {
                    var tempUrl = server.request.getUrl();
                    final long length = Files.size(Path.of(".", "public", tempUrl));
                    final var mimeType = Files.probeContentType(Path.of(".", "public", tempUrl));
                    final var template = Files.readString(Path.of(".", "public", tempUrl));
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();

                    server.write("HTTP/1.1 200 OK\r\n" + "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n", content);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            };
            server.addHandler("GET", "/classic", handler);

            server.listen(4539);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


