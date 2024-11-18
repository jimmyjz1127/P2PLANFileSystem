import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 
 */
public class SearchResponseReceiver implements Runnable {
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String BLUE = "\033[34m";

    private MulticastHandler multicastHandler;
    private Configuration configuration;
    private final BlockingQueue<Message> searchResponses;

    /**
     * 
     */
    public SearchResponseReceiver(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        this.searchResponses = new LinkedBlockingQueue<>();
    }


    /**
     * 
     */
    @Override 
    public void run() {
        while (!searchResponses.isEmpty()) {
            Message responseMessage = searchResponses.poll();
            processSearchResults(responseMessage);
        }
    }

    /**
     * Takes a search-response message and prints out its details according to type or response.
     * @param responseMessage : a search-response message either of type SearchResultMessage or SearchErrorMessage
     */
    public void processSearchResults(Message responseMessage) {
        if (responseMessage.getType().equals("search-result")) {
            String result = ((SearchResultMessage) responseMessage).getSearchResultString();
            System.out.println(GREEN + "[Search Result] : " + RESET + BLUE + result + RESET + " @ " + BLUE + responseMessage.getIdentifier() + RESET);
        } else if (responseMessage.getType().equals("search-error")) {
            System.out.println(RED + "[Search Error] : No Result @ " + RESET + BLUE + responseMessage.getIdentifier() + RESET);
        }
    }

    /**
     * Adds an incoming search-response message to queue.
     */
    public void addSearchResponse(Message searchResponseMessage) {
        searchResponses.add(searchResponseMessage);
    }
}