package code; 


import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Runnable task for managing recieved advertisements from other nodes.
 * Manages storage and cleaning of stored advertisements - to ensure 
 * the ":nodes" command of UI returns all up-to-date advertisments.
 * 
 * @author 190015412
 * @since November 2024
 */
public class AdvertisementReceiver implements Runnable {
    private MulticastHandler multicastHandler;
    private Configuration configuration;

    private final ConcurrentHashMap<String, AdvertisementMessage> advertisements;
    private final ScheduledExecutorService scheduler;

    /**
     * Constructor for AdvertisementReceiver runnable task.
     */
    public AdvertisementReceiver(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        this.advertisements = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Intermittently remove expired advertisements from the data structure. 
     */
    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(this::removeExpiredAdvertisements, 0, configuration.sleepTime, TimeUnit.MILLISECONDS)
    }

    /**
     * Removes any received advertisements that have expired.
     */
    public void removeExpiredAdvertisements() {
        advertisments.entrySet().removeIf(entry -> 
            (System.currentTimeMillis() - entry.timestampToDate(entry.getTimestamp()).getTime()) > configuration.maximumAdvertisementPeriod
        );
    }

    /**
     * Adds/replaces advertisement in storage.
     * @param advertisementMessage : the newly recieved advertisement.
     */
    public void addAdvertisement(AdvertisementMessage advertisementMessage) {
        String id = advertisementMessage.getIdentifier() + ":" + advertisementMessage.getServerPort();
        advertisements.put(id, advertisementMessage);
    }

    /**
     * Returns a string of all advertisements (should be displayed when user enters ":node")
     */
    public String getAdvertisementsString() {
        String output = "Advertisements : \n";

        int count = 0;
        for (AdvertisementMessage message : advertisements) {
            output += "[" + count++ + "]\n";
            output += "     Identifier : " + message.getIdentifier();
            output += "     Port : " + message.getServerPort();
            output += "     Services : " + message.getServicesString();
            output += "\n";
        }

        return output;
    }


}