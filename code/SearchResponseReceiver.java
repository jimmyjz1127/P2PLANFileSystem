import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Runnable task for managing rx search-response messaages from other remote nodes
 * in multicast group.
 * 
 * Takes incoming response messages, queues them, then processes them (prints them out)
 * 
 * @author 190015412
 * @since November 2024
 */
public class SearchResponseReceiver implements Runnable {
    /**
     * ANSI escape codes for colored cmd output
     */
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31;1m";
    public static final String GREEN = "\033[32;1m";
    public static final String BLUE = "\033[36;1m";
    public static final String REVERSED = "\u001b[7m";

    private MulticastHandler multicastHandler;
    private Configuration configuration;
    private final BlockingQueue<Message> searchResponses;

    /**
     * Constructor for SearchResponseReceiver.
     */
    public SearchResponseReceiver(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        this.searchResponses = new LinkedBlockingQueue<>();
    }


    /**
     * Print out search-resposnes messages while there are any in the queue
     */
    @Override 
    public void run() {
        while (!searchResponses.isEmpty()) {
            Message responseMessage = searchResponses.poll();
            processSearchResponses(responseMessage);
        }
    }

    /**
     * Takes a search-response message and prints out its details according to type or response.
     * @param responseMessage : a search-response message either of type SearchResultMessage or SearchErrorMessage
     */
    public void processSearchResponses(Message responseMessage) {
        if (responseMessage.getType().equals("search-result")) {
            String result = ((SearchResultMessage) responseMessage).getSearchResultString();
            System.out.println(GREEN + "[Search Result] : " + RESET + REVERSED + result + RESET + " @ " + BLUE + responseMessage.getIdentifier() + RESET);
        } else if (responseMessage.getType().equals("search-error")) {
            System.out.println(RED + "[Search Error] :" +  RESET + " No Result @ " + BLUE + responseMessage.getIdentifier() + RESET);
        }
    }

    /**
     * Adds an incoming search-response message to queue.
     */
    public void addSearchResponse(Message searchResponseMessage) {
        searchResponses.add(searchResponseMessage);
    }

    /**
     * 
     */
    public boolean isEmpty() {
        return searchResponses.isEmpty();
    }
}