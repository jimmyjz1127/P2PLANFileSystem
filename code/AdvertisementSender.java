package code; 


import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Runnable Task for sending out advertisements intermittently for current machine on a thread.
 * 
 * @author : 190015412
 * @since : November, 2024
 */
public class AdvertisementSender implements Runnable {
    private MulticastHandler multicastHandler;
    private Configuration configuration;

    /**
     * Constructor for AdvertisementSender Runnable task for sending out advertisements 
     * to Multicast group.
     * 
     * @param multicastHandler : the MulticastHandler thread coordinator.
     */
    public AdvertisementSender(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
    }


    /**
     * Send out advertisement message to multicast group task.
     */
    @Override
    public void run() {
        AdvertisementMessage txMessage = new AdvertisementMessage(configuration);

        multicastHandler.txMessage(txMessage)
    }
}