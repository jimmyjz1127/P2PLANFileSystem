import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

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
    private String username;
    private String hostname;
    private String identifier;

    private final ConcurrentHashMap<String, AdvertisementMessage> advertisements;
    // private final ScheduledExecutorService scheduler;

    /**
     * Constructor for AdvertisementReceiver runnable task.
     */
    public AdvertisementReceiver(MulticastHandler multicastHandler) {
        this.multicastHandler = multicastHandler;
        this.configuration = multicastHandler.configuration;
        this.advertisements = new ConcurrentHashMap<>();

        try {
            this.username = System.getProperty("user.name");
            this.hostname = InetAddress.getLocalHost().getCanonicalHostName();
            this.identifier = this.username + "@" + this.hostname;
            System.out.println(identifier);
        } catch (UnknownHostException e) {
            System.out.println("Message() : unknown hostname: " + e.getMessage());
        }
    }

    /**
     * Intermittently remove expired advertisements from the data structure. 
     */
    @Override
    public void run() {
        removeExpiredAdvertisements();
    }

    /**
     * Removes any received advertisements that have expired.
     */
    public void removeExpiredAdvertisements(){
        advertisements.entrySet().removeIf(entry -> 
            (System.currentTimeMillis() - entry.getValue().timestampToDate(entry.getValue().getTimestamp()).getTime()) > configuration.maximumAdvertisementPeriod
        );
    } 
        
    

    /**
     * Adds/replaces advertisement in storage.
     * @param advertisementMessage : the newly recieved advertisement.
     */
    public void addAdvertisement(AdvertisementMessage advertisementMessage) {
        if (!identifier.equals(advertisementMessage.getIdentifier())) {
            String id = advertisementMessage.getIdentifier() + ":" + advertisementMessage.getServerPort();
            advertisements.put(id, advertisementMessage);
        }
        
    }

    /**
     * Returns a string of all advertisements (should be displayed when user enters ":node")
     */
    public void getAdvertisementsString() {
        int count = 0;

        for (AdvertisementMessage message : advertisements.values()) {
            System.out.println("[" + count++ + "]");
            System.out.println("     Identifier : " + message.getIdentifier());
            System.out.println("     Port : " + message.getServerPort());
            System.out.println("     Services : " + message.getServicesString());
        }
    }


}