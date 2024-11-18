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
 * Runnable task for managing received search-request messages from other nodes.
 * In charge of receiving incoming requests, storing them, and then responding to them
 * with search-result messages.
 * 
 * 
 * @author 190015412
 * @since November 2024
 */
public class SearchRequestReceiver implements Runnable {
    private MulticastHandler multicastHandler;
    private Configuration configuration;
    private final BlockingQueue<SearchRequestMessage> searchRequests;

    /**
     * Constructor. 
     */
    public SearchRequestReceiver(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        this.searchRequests = new LinkedBlockingQueue<>();
    }

    /**
     * Process a rx search-request messages
     * This task should be scheduled by threadpool to constantly be processing 
     * received search-request messages at some interval.
     */
    @Override
    public void run() {
        try {
            processSearchRequests();
        } catch (InterruptedException e) {
            /**
             * Handle exception
             */
            System.out.println();
        }
        
    }


    /**
     * Function for processing search requests
     * This includes processing the information in the rx search-request,
     * then sending out a corresponding search-response. 
     */
    public void processSearchRequests() throws InterruptedException {
        SearchRequestMessage msg = searchRequests.take();

        // Shouldn't ever be null, but check just in case
        if (msg != null) {
            // the query (filename, filepath, substring)
            String searchString = msg.getSearchString();

            // Other attributes 
            String identifier = msg.getIdentifier();
            String username   = msg.getUsername();
            String hostname   = msg.getHostname();
            Long serialNo     = msg.getSerialNo();
            String timestamp  = msg.getTimestamp();

            ArrayList<File> matchingFiles = multicastHandler.getMatchingFiles(searchString);

            // If no matching results were found 
            if (matchingFiles == null || matchingFiles.isEmpty()) {
                SearchErrorMessage response = new SearchErrorMessage(identifier, serialNo);
                multicastHandler.txMessage(response);
            } else {
                // iterate through each result and send separate search-result for each
                for (File file : matchingFiles) {
                    String rootDir = configuration.rootDir;

                    // Get path relative to root_dir
                    String path = rootDir + file.getAbsolutePath().split(rootDir)[1];
                    SearchResultMessage response = new SearchResultMessage(identifier, serialNo, path);

                    multicastHandler.txMessage(response);
                }
            }
        }
    }



    /**
     * Adds an incoming search-request message to queue.
     */
    public void addSearchRequest(SearchRequestMessage searchRequestMessage) {
        // if (!searchRequests.contains(searchRequestMessage)) {
        //     searchRequests.add(searchRequestMessage);
        // } else {

        // }
        searchRequests.add(searchRequestMessage);
    }
} 