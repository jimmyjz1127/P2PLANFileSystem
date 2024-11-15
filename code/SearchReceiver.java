package code;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Runnable task for managing received search-request messages from other nodes.
 * In charge of receiving incoming requests, storing them, and then responding to them
 * with search-result messages.
 * 
 * 
 * @author 190015412
 * @since November 2024
 */
public class SearchReceiver implements Runnable {
    private MulticastHandler multicastHandler;
    private Configuration configuration;
    private FileTreeBrowser fileTreeBrowser;
    private final BlockingQueue<SearchRequestMessage> searchRequests;

    /**
     * 
     */
    public SearchReceiver(MulticastHandler multicastHandler, FileTreeBrowser fileTreeBrowser) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        this.fileTreeBrowser = fileTreeBrowser;
        this.searchRequests = new ConcurrentHashMap<>();
    }

    /**
     * 
     */
    @Override
    public void run() {

    }


    /**
     * Function for processing search requests
     * This includes processing the information in the rx search-request,
     * then sending out a corresponding search-response. 
     */
    public void processSearchRequests() {
        SearchRequestMessage msg = searchRequests.take();

        // Shouldn't ever be null, but check just in case
        if (msg != null) {
            // the query (filename, filepath, substring)
            String searchString = msg.getSearchString();

            // Other attributes 
            String identifier = msg.getIdentifier();
            String username   = msg.getUsername();
            String hostname   = msg.getHostname();
            String serialNo   = msg.getSerialNo();
            String timestamp  = msg.getTimeStamp();


        }
    }


    /**
     * Given a search string, returns the absolute path (as string) of all 
     * files and directories that substring-match the search string 
     * 
     * @param searchString : the string being queried (filename, filepath, substring)
     * @return ArrayList of matched results.
     */
    public ArrayList<String> searchForFile(String searchString) {

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