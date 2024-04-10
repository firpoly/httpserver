package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    Socket socket;
    ServerSocket serverSocket;
    BufferedReader in;
    BufferedOutputStream out;
    Server() throws IOException {
        serverSocket = new ServerSocket();
    }
    public void listen(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }
    public void startListen() throws IOException {
        this.socket = this.serverSocket.accept();
        in =new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        out = new BufferedOutputStream(this.socket.getOutputStream());
    }
    public void write (String text, byte [] content) throws IOException {
        out.write(text.getBytes());
        if (content.length != 0) {
            out.write(content);
        }
        out.flush();
    }
    public String read () throws IOException {
        return in.readLine();
    }


}
