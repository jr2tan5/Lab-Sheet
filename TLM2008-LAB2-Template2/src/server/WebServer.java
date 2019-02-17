/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author cristalngo
 */
public class WebServer {

    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(8081);  // Start, listen on port 80
        while (true) {
            try {
                Socket s = serverSocket.accept();  // Wait for a client to connect
                new ClientHandler(s);  // Handle the client in a separate thread
            } catch (IOException x) {
                System.out.println(x);
            }
        }
    }
}
