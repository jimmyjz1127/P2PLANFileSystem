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
    public static final String RED = "\033[31;1m";
    public static final String GREEN = "\033[32;1m";
    public static final String BLUE = "\033[36;1m";
    public static final String REVERSED = "\u001b[7m";

    private int              serverPort;        // The port used by file server to serve files
    private Socket           clientSocket;      // The socket connected to file server 
    private String           serverHostname;    // hostname of the server 
    private Configuration    configuration;     // Configuration of current machine
    private DataInputStream  in;                // input stream from file server
    private DataOutputStream out;               // not necessary


    /**
     * Constructor
     */
    public FileClient(Configuration configuration, String serverHostname, int serverPort) {
        this.configuration = configuration;
        this.serverHostname = serverHostname;
        this.serverPort = serverPort;

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
            String fileName = in.readUTF();
            long   fileSize = in.readLong();

            String saveFilePath = configuration.downloadDir + "/" + serverHostname + "/" + fileName;
            File file = new File(saveFilePath);
            File parentDir = file.getParentFile();

            // Ensure the directory exists
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("FileClient.receiveFiles() : Failed to create directory: " + parentDir.getAbsolutePath());
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

            if (bytesRemaining <= 0) {
                System.out.println("Downloaded file " + REVERSED + "[" + fileName + "]" + RESET + " from " + BLUE + serverHostname + RESET);
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

    /**
     *  For testing that FileServer will only send file to the client it expects 
     *  to connect (the one that sent the original download-request)
     * 
     * You must first run "java FileServer" and copy and paste the outputted port as a cmdline argument to this file 
     * --> java FileClient <server port copied from output of running "java FileServer">
     */
    public static void main(String[] args) {
        // after running "java FileServer", copy and paste the outputed port as cmd argument for this file
        int serverPort = Integer.parseInt(args[0]);

        Configuration configuration = new Configuration("filetreebrowser.properties");
        String fileString = "root_dir/dir1/text1-1.txt";
        String serverHostname = "pc7-033-l.cs.st-andrews.ac.uk";
        FileClient client = new FileClient(configuration, serverHostname, serverPort);
        Thread t = new Thread(client);
        t.start();
    }
}