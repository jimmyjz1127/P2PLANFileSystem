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
            Message responseMessage = searchResponse.poll();
            processDownloadResponses(responseMessage);
        }
    }

    /**
     * 
     */
    public void processDownloadResponses(Message responseMessage) {
        if (responseMessage.getType().equals("download-result")) {

        } else {
            System.out.println(RED + "[DOWNLOAD ERROR] : " + RESET + "No Results @ " + 
                               BLUE + responseMessage.getIdentifier() + RESET);
        }
    }
}