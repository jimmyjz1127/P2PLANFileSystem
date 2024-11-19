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
        this.serverHostName = serverHostName;
        this.serverPort = serverPort;
        this.numMatchingFiles = numMatchingFiles;

        try {
            InetAddress serverAddress = InetAddress.getByName(serverHostName);
            this.clientSocket = new Socket(serverAddress, serverPort);
            clientSocket.setSoTimeout(configuration.socketMaxTTL);

            this.in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataInputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("FileClient.FileClient() : IOException -> " + e.getMessage());
        }
    }


    /**
     * 
     */
    @Override
    public void run() {
        try {
            receiveFiles();
        } catch (IOException e) {
            System.err.println("FileClient.run() : IOException -> " + e.getMessage());
        }
    }


    /**
     * 
     */
    public void receiveFiles() {
        try {
            while (numMatchingFiles > 0) {
                String fileName = in.readUTF();
                long   fileSize = in.readLong();

                FileOutputStream fos = new FileOutputStream(configuration.downloadDir + "/" + serverHostname + "/" + fileName);

                byte[] buffer = new byte[2048];
                long bytesRemaining = fileSize;
                long bytesRead;

                while (bytesRemaining > 0 && 
                    bytesRead = dis.read(buffer,0, (int) Math.min(buffer.length, bytesRemaining)) != -1) {
                    
                    fos.write(buffer,0,bytesRead);
                    bytesRemaining -= bytesRead;
                }

                if (bytesRemaining <= 0) {
                    System.out.println("Downloaded file " + GREEN + "[" + fileName + "]" + RESET + " from " + BLUE + serverHostname + RESET);
                    numMatchingFiles--;
                }
            }
        } catch (IOException e) {
            System.out.println("FileClient.receiveFiles() : Socket Timed Out");
            return;
        } finally {
            try {
                clientSocket.close()
            } catch (IOException e) {
                System.err.println("FileServer.handleIncomingRequests() : Failed to close serverSocket -> " + e.getMessage());
            }
        }
    }
}