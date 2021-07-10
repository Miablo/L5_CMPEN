import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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

        // Get and display header lines
        String headerLine = null;
        while((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        if((br.read() == -1)) {
            // CLose streams and socket
            System.out.println("\nAll done. Streams and Socket is closing!\n");
            socket.close();
            is.close();
            os.close();
            br.close();
        }

    }
}
