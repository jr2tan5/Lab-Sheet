/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.logging.*;

/**
 *
 * @author cristalngo
 */
public class WebServer {

    public static void main(String args[]) {
        try {
            WebServer server = new WebServer();
            server.runServer();
        } catch (IOException ex) {
            Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runServer() throws IOException {
        final ServerSocket server = new ServerSocket(8080);
        System.out.println("Listening for connection on port 8080 ....");
        while (true) 
        {
            Socket client = server.accept();
            //1. Read HTTP request from client socket
            this.readHttpRequest(client);
            //2. Prepare HTTP response
            //3. send HTTP response to the client
            this.prepareAndSendResponse(client);
            //4. Close the socket
            client.close();
        }

    }
    
    private void readHttpRequest(Socket clientSocket) throws IOException{
        InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream()); 
        BufferedReader reader = new BufferedReader(isr); 
        String line ; 
        while (!(line = reader.readLine()).equals("")) 
        { 
            System.out.println(line);
        }
    }
    
    private void prepareAndSendResponse(Socket socket) throws IOException{
       Date today = new Date();
       String httpResponse = "HTTP/1.1 200 0K\r\n\r\n" + today;
       socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
    }

}
