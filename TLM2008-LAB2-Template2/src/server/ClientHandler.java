/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.net.Socket;

/**
 *
 * @author cristalngo
 */
public class ClientHandler extends Thread {

    private Socket socket;
    private BufferedReader in;
    PrintStream out;

    public ClientHandler(Socket s) {
        socket = s;
        start();
    }

    @Override
    public void run() {
        try {
            Boolean isGet = false, isPost = false;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
            
            //ASSSIGNMENT 2A: Enter your code here
            String reqType, filePath, inputLine;
            inputLine = in.readLine();
            if(inputLine != null){
                String[] lineSplit = inputLine.split(" ");

                reqType = lineSplit[0];
                filePath = lineSplit[1];

                if(reqType.equals("GET"))
                    isGet = true;
                else if(reqType.equals("POST"))
                    isPost = true;

                if (isGet) {
                    this.processGetRequest(filePath);
                } else if (isPost) {
                    this.processPostRequest();
                }
            }
            out.close();

        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void processGetRequest(String filename) throws IOException {
        //TODO: Copy the code from the lab manual here.
        try{
            if(filename.isEmpty())
            {
                throw new FileNotFoundException(); // Bad Request
            }
            filename = this.formatFilename(filename);
            this.printFileContent(filename);
            out.close();
        }
        catch(FileNotFoundException x){
            out.print("HTTP/1.0 404 Not Found \r\n"
                    + "Content-type: text/html\r\n\r\n"
                    + "<html><header></header><body>" + filename + " not found</body></html>\n");
            out.close();
        }
        
    }

    private void processPostRequest() throws IOException {
        //TODO: Copy the code from the lab manual here.
        System.out.println("This is a POST request");
        String postData = this.printPostRequest();
        String email = this.getEmailAddress(postData);
        sendConfirmPage(email);
    }
    
    private String printPostRequest() throws IOException
    {
        String contentLengthStr = "Content-Length: ";
        int contentLength = -1;
        String line;
        while(!(line = in.readLine()).equals(""))
        {
            if (line.startsWith(contentLengthStr)) 
            {
                contentLength = Integer.parseInt(line.substring(contentLengthStr.length()));
            }
            System.out.println(line);
        }
        final char[] content = new char[contentLength];
        in.read(content);
        line = new String (content);
        return line;
    }

    private String getEmailAddress(String postData) {
        String email = "";
        if(postData!=null)
        {
            String[] postDataSplit = postData.split("&");
            email= postDataSplit[1];
            email= email.substring(7);
            email = email.replace("%40", "@");
        }
        System.out.println(email);
        return email;
    }

    private void sendConfirmPage(String email) throws IOException {
        //TODO: Enter your code here for Exercise 2C
        
        //printFileContent("confirmContact1.html");
        String filename1, filename2;
        filename1 = "./www/confirmContact1.html";
        filename2 = "./www/confirmContact2.html";
        InputStream f1 = new FileInputStream(filename1);
        InputStream f2 = new FileInputStream(filename2);
        String mimeType = getMIMEType(filename1);
        
        out.print("HTTP/1.0 200 OK\r\n"
                + "Content-Type: " + mimeType + "\r\n\r\n");
        
        byte[] a = new byte[4096];
        int n;
        while((n = f1.read(a))> 0){
            out.write(a, 0, n);
        }
        
        out.print(email);
        //printFileContent("confirmContact2.html");
        
        byte[] b = new byte[4096];
        int m;
        while((m = f2.read(b))> 0){
            out.write(b, 0, m);
        }
    }

    private String formatFilename(String filename) throws FileNotFoundException, IOException {
        // Append trailing "/" with "index.html"
        if (filename.endsWith("/")) {
            filename += "index.html";
        }

        // Remove leading / from filename
        while (filename.indexOf("/") == 0) {
            filename = filename.substring(1);
        }

        // Replace "/" with "\" in path for PC-based servers
        filename = filename.replace('/', File.separator.charAt(0));

        // Check for illegal characters to prevent access to superdirectories
        if (filename.indexOf("..") >= 0 || filename.indexOf(':') >= 0
                || filename.indexOf('|') >= 0) {
            throw new FileNotFoundException();
        }

        // If a directory is requested and the trailing / is missing,
        // send the client an HTTP request to append it.  (This is
        // necessary for relative links to work correctly in the client).
        if (new File(filename).isDirectory()) {
            filename = filename.replace('\\', '/');
            out.print("HTTP/1.0 301 Moved Permanently\r\n"
                    + "Location: /" + filename + "/\r\n\r\n");
            out.close();
            return filename;
        }
        return filename;
    }

    private void printFileContent(String filename) throws FileNotFoundException, IOException {
        //TODO: Copy the code from the lab manual here.
        filename = "./www/" + filename;
        InputStream f = new FileInputStream(filename);
        String mimeType = getMIMEType(filename);
        
        out.print("HTTP/1.0 200 OK\r\n"
                + "Content-Type: " + mimeType + "\r\n\r\n");
        
        byte[] a = new byte[4096];
        int n;
        while((n = f.read(a))> 0){
            out.write(a, 0, n);
        }
    }

    private String getMIMEType(String filename) {
        // Determine the MIME type and print HTTP header
        String mimeType = "text/plain";
        if (filename.endsWith(".html") || filename.endsWith(".htm")) {
            mimeType = "text/html";
        }else if (filename.endsWith(".css")) {
            mimeType = "text/css";
        }else if (filename.endsWith(".png")) {
            mimeType = "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            mimeType = "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            mimeType = "image/gif";
        } else if (filename.endsWith(".class")) {
            mimeType = "application/octet-stream";
        } 
        return mimeType;
    }
}
