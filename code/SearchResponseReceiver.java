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
            System.out.println("Search Result : " + result + " @ " + responseMessage.getIdentifier());
        } else if (responseMessage.getType().equals("search-error")) {
            System.out.println("Search Error : No Result @ " + responseMessage.getIdentifier());
        }
    }

    /**
     * Adds an incoming search-response message to queue.
     */
    public void addSearchResponse(Message searchResponseMessage) {
        searchResponses.add(searchResponseMessage);
    }
}