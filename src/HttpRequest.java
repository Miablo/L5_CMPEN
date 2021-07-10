import com.sun.xml.internal.ws.server.sei.EndpointResponseMessageBuilder;
import javafx.util.Builder;
import sun.net.www.http.HttpClient;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

/**
 * HttpRequest class used for threading
 * class constructor passed to thread implementing runnable interface
 *
 * @author Mio Diaz, Lab 5 Manual
 * @version 1.0
 *
 * @see java.io;
 * @see java.net.ServerSocket;
 * @see java.net.Socket;
 *
 */
final public class HttpRequest implements Runnable {
    Socket socket;
    // terminate line of server response message with carriage return CR and line feed LF
    final static String CRLF = "\r\n";
    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    /**
     * run calls processRequest method
     */
    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * processRequest method obtains socket input and output streams
     * request and header lines to console
     *
     * @throws Exception returns exception to try catch
     */
    private void processRequest() throws Exception {
        // obtain ref to socket input and output stream
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        // set up input stream filters
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // Get request line of HTTP request message
        String requestLine = br.readLine();
        // Display request line
        System.out.println("\n" + requestLine);

        // Extract filename from request line
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();
        // skip over method GET
        String fileName = tokens.nextToken();
        // Prepend a "." so file request is within current dir
        fileName = "." + fileName;
        // open file before sending to client
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch(FileNotFoundException e) {
            fileExists = false;
        }

        // Get and display header lines
        String headerLine = null;
        while((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        // Construct the Response message
        // three parts to response message status line, response headers, and entity body
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>404 File Not Found</BODY</HTML>";
        }

        // Send statusline and header to browers by writing to socket out stream
        os.writeBytes(statusLine);
        // Send content type line
        os.writeBytes(contentTypeLine);
        // send blank line to indicate end of header line
        os.writeBytes(CRLF);

        // send entity body
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }

        // if peer closed connection
        if((br.read() == -1)) {
            // CLose streams and socket
            System.out.println("\nAll done. Streams and Socket is closing!\n");
            socket.close();
            is.close();
            os.close();
            br.close();
        }

    }

    /**
     * sendBytes uses fis and os to send file to client
     *
     * @param fis file input stream
     * @param os dataoutput stream
     * @throws Exception returns exception
     */
    private void sendBytes(FileInputStream fis, DataOutputStream os) throws Exception {
        // Construct 1k buffer to hold bytes on their way to socket
        byte[] buffer = new byte[1024];
        int bytes = 0;
        // copy requested file into socket's output stream
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer,0,bytes);
        }
    }

    /**
     * contentType uses fileName to read extension and return
     * string representing MIME type or application/octet-stream if extension unknown
     *
     * @param fileName filename constructed in calling method
     * @return content type string to be displayed
     */
    private String contentType(String fileName) {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }

        if(fileName.endsWith(".gif")) {
            return "image/gif";
        }

        if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }

        return "application/octet-stream";
    }
}
