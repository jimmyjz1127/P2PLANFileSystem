import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * Runnable task for managing received download-request messages from other nodes
 * in multicast group. 
 * 
 * In charge of receiving incoming requests, storing them, and then responding to them 
 * with download-response message.
 * 
 * @author : 190015412
 * @since November 2024
 */

public class DownloadRequestReceiver implements Runnable { 
    /**
     * ANSI escape codes for colored cmd output
     */
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String BLUE = "\033[34m";

    private MulticastHandler multicastHandler;
    private Configuration configuration;
    private final BlockingQueue<DownloadRequestMessage> downloadRequests;

    /**
     * Constructor for DownloadRequestReceiver
     * 
     * @param multicastHandler : central multicast handler 
     */
    public DownloadRequestReceiver(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        this.downloadRequests = new LinkedBlockingQueue<>();
    }


    /**
     * 
     */
    @Override 
    public void run() {
        try {
            processDownloadRequests();
        } catch (InterruptedException e) {
            System.out.println("DownloadRequestReceiver.run() : InterruptedException -> " + e.getMessage());
        }
    }


    public void processDownloadRequests() throws InterruptedException {
        DownloadRequestMessage msg = downloadRequests.take();

        if (msg != null) {
            String fileString = msg.getFileString();

            String targetIdentifier = msg.getTargetIdentifier();
            String identifier = msg.getIdentifier();
            String username   = msg.getUsername();
            String hostname   = msg.getHostname();
            Long   serialNo   = msg.getSerialNo();
            String timestamp  = msg.getTimestamp(); 

            ArrayList<File> matchingFiles = multicastHandler.getMatchingFiles(fileString);

            ArrayDeque<File> filesToDownload = new ArrayDeque<File>();
            for (File file : matchingFiles) {
                if (file.isFile()) {
                    filesToDownload.add(file);
                }
            }

            // If no matching results were found 
            if (filesToDownload == null || filesToDownload.isEmpty()) {
                Message response = new DownloadErrorMessage(identifier, serialNo);
                multicastHandler.txMessage(response);
            } else {
                FileServer fileServer = new FileServer(filesToDownload, configuration);

                /**
                 * NTS : look into prevention methods of thread explosion
                 * - Thread pooling + queued processing of requests 
                 */
                Thread t = new Thread(fileServer);
                t.start();

                String rootDir = configuration.rootDir;
                int port = fileServer.getPort();


                DownloadResultMessage response = new DownloadResultMessage(fileString, identifier, serialNo, port, matchingFiles.size());
                multicastHandler.txMessage(response);

            }
        }
    }

    /**
     * For adding rx download request messages to data structure.
     */
    public void addDownloadRequest(DownloadRequestMessage downloadRequestMessage) {
        downloadRequests.add(downloadRequestMessage);
    }
}

