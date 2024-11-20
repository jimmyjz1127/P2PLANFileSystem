import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Runnable Task for handling the downloading of files in response to rx 
 * download-result message.
 * 
 * @author 190015412
 * @since November 2024
 */
public class FileClient implements Runnable { 
    /**
     * ANSI escape codes for coloured cmd output
     */
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String BLUE = "\033[34m";

    private int              serverPort;        // The port used by file server to serve files
    private Socket           clientSocket;      // The socket connected to file server 
    private String           serverHostname;    // hostname of the server 
    private Configuration    configuration;     // Configuration of current machine
    private int              numMatchingFiles;  // number of files to expect from file server 
    private DataInputStream  in;                // input stream from file server
    private DataOutputStream out;               // not necessary


    /**
     * Constructor
     */
    public FileClient(Configuration configuration, String serverHostname, int serverPort, int numMatchingFiles) {
        this.configuration = configuration;
        this.serverHostname = serverHostname;
        this.serverPort = serverPort;
        this.numMatchingFiles = numMatchingFiles;

        try {
            InetAddress serverAddress = InetAddress.getByName(serverHostname);
            this.clientSocket = new Socket(serverAddress, serverPort);
            clientSocket.setSoTimeout(configuration.socketMaxTTL);

            this.in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("FileClient.FileClient() : IOException -> " + e.getMessage());
        }
    }


    /**
     * 
     */
    @Override
    public void run() {
        receiveFiles();
    }


    /**
     * Waits for incoming file data from remote file server.
     */
    public void receiveFiles() {
        try {
            // Continue waiting as long as there are more files to expect from server
            while (numMatchingFiles > 0) {
                String fileName = in.readUTF();
                long   fileSize = in.readLong();

                String saveFilePath = configuration.downloadDir + "/" + serverHostname + "/" + fileName;
                File file = new File(saveFilePath);
                File parentDir = file.getParentFile();

                // Ensure the directory exists
                if (!parentDir.exists() && !parentDir.mkdirs()) {
                    throw new IOException("FileClient.receiveFiles : Failed to create directory: " + parentDir.getAbsolutePath());
                }

                FileOutputStream fos = new FileOutputStream(file);

                byte[] buffer = new byte[2048];
                long bytesRemaining = fileSize;
                int bytesRead;

                while (bytesRemaining > 0 && 
                    (bytesRead = in.read(buffer,0, (int) Math.min(buffer.length, bytesRemaining))) != -1) {
                    
                    fos.write(buffer,0,bytesRead);
                    bytesRemaining -= bytesRead;
                }


                System.out.println("test: " + bytesRemaining);
                if (bytesRemaining <= 0) {
                    System.out.println("* Downloaded file " + GREEN + "[" + fileName + "]" + RESET + " from " + BLUE + serverHostname + RESET);
                    numMatchingFiles--;   
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("FileClient.receiveFiles() : Socket Timed Out for " + BLUE + serverHostname + ":" + serverPort + RESET);
            return;
        } catch (IOException e) {
            System.err.println("FileClient.receiveFiles() : IOException -> " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("FileServer.handleIncomingRequests() : Failed to close serverSocket -> " + e.getMessage());
            }
        }
    }
}