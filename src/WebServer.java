
import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.*;
import java.util.*;
/**
 * WebServer class implements multi-threaded server using TCP
 * capable of processing multiple simultaneous service requests in parallel
 *
 * @author Mio Diaz, Lab 5 Manual
 * @version 1.0
 *
 * @see java.io
 * @see java.net
 * @see java.util
 */
public final class WebServer {
    // main thread server listens on a fixed port
    // when TCP connection request is received TCP connection is setup through another port
    // services request in a separate thread

    /**
     * main method is the driver function
     *
     * @param args command line args
     * @throws Exception error if service request fails
     */
    public static void main(String[] args) throws Exception {
        // port created to process service requests any port higher than 1024
        int port = 1337;
        // open socket and wait for TCP connection request
        ServerSocket socket = new ServerSocket(port);
        // Establish the listen socket with ServerSocket
        Socket s = socket.accept();
        // Provide a message for users
        System.out.println("The server is listening.");
        // Process HTTP service requests in infinite loop
        while(true) {
            // Listen for TCP connection request
            if(s.isConnected()) {
                // pass constructor a reference to object that requests our established connection
                // Construct an object to process HTTP request message
                HttpRequest request = new HttpRequest(s);
                // Create new thread to process request
                Thread thread = new Thread(request);
                // created thread object passing reference to httprequest obj to handle incoming http service request

                // Start thread
                thread.start();
            }
            // if connection is closed
            break;

        }
    }
}
