import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Runnable task for handling the serving of files in response to download-request messages.
 * 
 * @author 190015412
 * @since November 2024
 */
public class FileServer implements Runnable {
    /**
     * ANSI escape codes for coloured cmd output
     */
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31;1m";
    public static final String GREEN = "\033[32;1m";
    public static final String BLUE = "\033[36;1m";

    private int              port;            // Ephemeral port of server
    private ServerSocket     serverSocket;    // Ephemeral socket
    private Configuration    configuration;
    private String           identifier;
    private File             fileToTransfer;
    private String           requestHostname;

    /**
     * Constructor for FileServer runnable task.
     * 
     * @param configuration   : configuration of current machine.
     */
    public FileServer(Configuration configuration, File fileToTransfer, String requestHostname) {
        this.configuration   = configuration;
        this.fileToTransfer  = fileToTransfer;
        this.identifier      = configuration.identifier;
        this.requestHostname = requestHostname;
        try {
            // obtain ephemeral socket
            this.serverSocket = new ServerSocket(0);
            serverSocket.setSoTimeout(configuration.socketMaxTTL); // set a timeout of 10 seconds in case client has issues
            this.port = serverSocket.getLocalPort(); 
        } catch (IOException e) {
            System.err.println("FileServer.FileServer() : IOException -> " + e.getMessage());
        }
    }

    /**
     * 
     */
    @Override
    public void run() {
        handleIncomingRequests();            
    }

    /**
     * Getter method for port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Waits for client to initiate download, then upon initiation begins sending the file.
     */
    public void handleIncomingRequests() {
        try {
            boolean done = false;
            while (!done) {
                Socket clientSocket = serverSocket.accept();
                // Get client's IP address and port
                String clientAddress = clientSocket.getInetAddress().getHostName();
                int clientPort = clientSocket.getPort();

                if (requestHostname.equals(clientAddress)) {
                    DataOutputStream fileOut = new DataOutputStream(clientSocket.getOutputStream());

                    int bytesRead = sendFile(fileOut, fileToTransfer);

                    configuration.log.writeLog("tx-> " + identifier + " sent " + bytesRead + " bytes to " + clientAddress+":"+clientPort);
                    done = true;
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("FileServer.handleIncomingRequests() : Socket Timed out");
            return;
        } catch (IOException e) {
            System.err.println("FileServer.handleIncomingRequests() : IOException -> " + e.getMessage());
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("FileServer.handleIncomingRequests() : Failed to close serverSocket -> " + e.getMessage());
            }
        }

    }

    /**
     * Function to handle sending the file data to a given DataOutputStream
     * @param fileOut : DataOutputStream of client connection
     * @param file : the file to send 
     * 
     * @return the amount of bytes sent
     */
    public int sendFile(DataOutputStream fileOut, File file) {
        int totalBytesSent = 0;
        try (FileInputStream fis = new FileInputStream(file)) {
            // write meta-data
            fileOut.writeUTF(file.getName());
            fileOut.writeLong(file.length());

            byte[] buffer = new byte[2048];

            int bytesRead; // To keep track of how many bytes of file are left to send 
            while ((bytesRead = fis.read(buffer)) != -1) {
                fileOut.write(buffer,0, bytesRead);
                totalBytesSent += bytesRead;
            }
            fileOut.flush();
        } catch (IOException e) {
            System.err.println("FileServer.sendFile() : IOException -> " + e.getMessage());
        }
        return totalBytesSent;
    }

    /**
     *  For testing that FileServer will only send file to the client it expects 
     *  to connect (the one that sent the original download-request)
     */
    public static void main(String[] args) {
        Configuration configuration = new Configuration("filetreebrowser.properties");
        File file = new File("root_dir/dir1/text1-1.txt");
        String requesterHostname = "pc7-007-l.cs.st-andrews.ac.uk";
        FileServer server = new FileServer(configuration, file, requesterHostname);
        System.out.println(server.getPort()); // copy and paste into other node when running java FileClient <serverPort>
        Thread t = new Thread(server);
        t.start();        
    }
}