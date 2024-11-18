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
    private MulticastHandler multicastHandler;
    private Configuration configuration;




    /**
     * 
     */
    @Override 
    public void run() {
        // do something
    }
}

