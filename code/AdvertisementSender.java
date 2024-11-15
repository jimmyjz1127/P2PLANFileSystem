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

    private final ScheduledExecutorService scheduler;

    /**
     * Constructor for AdvertisementSender Runnable task for sending out advertisements 
     * to Multicast group.
     * 
     * @param multicastHandler : the MulticastHandler thread coordinator.
     */
    public AdvertisementSender(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        his.scheduler = Executors.newSingleThreadScheduledExecutor();
    }


    /**
     * Send out advertisement message to multicast group at an interval.
     */
    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(this::txAdvertisement, 0, configuration.sleepTime, TimeUnit.MILLISECONDS)
    }

    /**
     * Function for sending tx advertisement to other nodes in multicast group.
     * To be called by thread scheduler.
     */
    public void txAdvertisement() {
        AdvertisementMessage txMessage = new AdvertisementMessage(configuration);

        multicastHandler.txMessage(txMessage)
    }
}