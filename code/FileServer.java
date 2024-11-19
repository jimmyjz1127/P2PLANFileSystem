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
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String BLUE = "\033[34m";

    private int              port;            // Ephemeral port of server
    private ServerSocket     serverSocket;    // Ephemeral socket
    private ArrayDeque<File> filesToDownload; // file(s) that client will download
    private Configuration    configuration;
    private String           identifier;

    /**
     * Constructor for FileServer runnable task.
     * 
     * @param filesToDownload : ArrayDeque of file objects. All file objects files and not directories.
     * @param configuration   : configuration of current machine.
     */
    public FileServer(ArrayDeque<File> filesToDownload, Configuration configuration) {
        this.filesToDownload = filesToDownload;
        this.configuration   = configuration;
        this.identifier      = configuration.identifier;
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
        try {
            handleIncomingRequests();            
        } catch (IOException e) {
            System.err.println("FileServer.run() : IOException -> " + e.getMessage());
        }
    }

    /**
     * Getter method for port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Waits for client to initiate download, then upon initiation begins sending the file(s).
     */
    public void handleIncomingRequests() {
        try {
            // While there are still files to download 
            while (!filesToDownload.isEmpty()) {
                Socket clientSocket = serverSocket.accept();
                // Get client's IP address and port
                String clientAddress = clientSocket.getInetAddress().getHostName();
                int clientPort = clientSocket.getPort();

                File file = filesToDownload.poll();

                DataOutputStream fileOut = new DataOutputStream(clientSocket.getOutputStream());

                int bytesRead = sendFile(fileOut, File);

                configuration.log.writelog("tx-> " + identifier + " sent " + bytesRead + " bytes to " + clientAddress+":"+clientPort);
            }
        } catch (IOException e) {
            System.err.println("FileServer.handleIncomingRequests() : IOException -> " + e.getMessage());
        } catch (SocketTimeoutException e) {
            System.err.printlng("FileServer.handleIncomingRequests() : Socket Timed out")
            return;
        } finally {
            try {
                serverSocket.close()
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
    public int sendFile(DataOutputSteam fileOut, File file) {
        int totalBytesSent = 0;
        try {
            FileInputStream fis = new FileInputStream(file);

            // write meta-data
            fileOut.writeUTF(file.getName());
            fileOut.writeLong(file.length());

            byte[] buffer = new byte[2048];

            int bytesRead; // To keep track of how many bytes of file are left to send 
            while ((bytesRead = fis.read(buffer)) != -1) {
                fileOut.write(buffer,0, bytesRead);
                totalBytesSent += bytesRead;
            }
            dos.flush();
        } catch (IOException e) {
            System.err.println("FileServer.sendFile() : IOException -> " e.getMessage());
        }
        return totalBytesSent;
    }


}