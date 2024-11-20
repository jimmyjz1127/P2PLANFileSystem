import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 */
public class DownloadResponseReceiver implements Runnable {
    /**
     * ANSI escape codes for colored cmd output
     */
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String BLUE = "\033[34m";

    private MulticastHandler multicastHandler;
    private Configuration configuration;

    private final BlockingQueue<Message> downloadResponses;

    /**
     * Constructor
     */
    public DownloadResponseReceiver(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        this.downloadResponses = new LinkedBlockingQueue<>();
    }

    /**
     * 
     */
    @Override 
    public void run() {
        while (!downloadResponses.isEmpty()) {
            Message responseMessage = downloadResponses.poll();
            processDownloadResponses(responseMessage);
        }
    }

    /**
     * Takes a received response message and processes it 
     * If response message is error, it prints indication to user in cmd 
     * If response message is a result, it will create a FileClient runnable task to download file(s) from source 
     * of download-response message. 
     * 
     * @param responseMessage : either a DownloadErrorMessage or DownloadResultMessage
     */
    public void processDownloadResponses(Message responseMessage) {
        if (responseMessage.getType().equals("download-result")) {
            DownloadResultMessage downloadResultMessage = (DownloadResultMessage) responseMessage;

            String identifier       = downloadResultMessage.getIdentifier();
            int    numMatchingFiles = downloadResultMessage.getNumMatchingFiles();
            String hostname         = downloadResultMessage.getHostname();
            int    serverPort       = downloadResultMessage.getFileTransferPort();

            System.out.println(GREEN + "[Download Result]" + RESET + " " + numMatchingFiles + " file(s) matched @" + BLUE + identifier + RESET);
            System.out.println("Initiating file transfer...");

            FileClient fileClient = new FileClient(configuration, hostname, serverPort, numMatchingFiles);
            Thread t = new Thread(fileClient);
            t.start();
        } else {
            System.out.println(RED + "[DOWNLOAD ERROR] : " + RESET + "No Results @ " + 
                               BLUE + responseMessage.getIdentifier() + RESET);
        }
    }

    /**
     * Adds rx download response to queue for processing
     * 
     * @param responseMessage : an rx download response message.
     */
    public void addDownloadResponse(Message responseMessage ) {
        downloadResponses.add(responseMessage);
    }
}