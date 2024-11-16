package code;

import code.message.*;

import java.net.*;
import java.util.Arrays;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class for handling multicast connections.
 * Implements Runnable to allow handling mulitple incoming flows 
 * of various types (advertisement, search-request, etc) using threading.
 * 
 * @author 190015412
 * @since Novemeber 2024
 */

public class MulticastHandler implements Runnable {
    private MulticastEndpoint multicastEndpoint; 
    private Configuration configuration;
    private ScheduledExecutorService scheduler;
    private FileTreeBrowser fileTreeBrowser;


    private AdvertisementReceiver advertisementReceiver;
    private AdvertisementSender advertisementSender;
    private SearchRequestReceiver searchRequestReceiver;
    private SearchResultReceiver searchResultReceiver;

    /**
     * Constructor for MulticastHandler.
     * @param configuration : the configuration of the current multicast node.
     * @param fileTreeBrowser : FileTreeBrowser object (associated with configuration object)
     */
    public MulticastHandler(Configuration configuration, FileTreeBrowser fileTreeBrowser) {
        this.configuration = configuration;
        this.fileTreeBrowser = fileTreeBrowser;

        try {
            // Initialize multicast socket with configurations
            multicastEndpoint = new MulticastEndpoint(this.configuration);
            configuration.log.writeLog("Multicast Endpoint Created : " + this.configuration.mAddr6 + ":" + this.configuration.mPort);

            // Join the multicast group 
            multicastEndpoint.join();
            configuration.log.writeLog("Joined Multicast Group : " + this.configuration.mGroup);

            // Create scheduled threadpool
            scheduler = Executors.newScheduledThreadPool(7);



            // Create advertisement receiver an add to threadpool
            advertisementReceiver = new AdvertisementReceiver(this);
            // Schedule the advertisement receiver task (of removing expired advertisements)
            scheduler.scheduleAtFixedRate(advertisementReceiver, 0, configuration.sleepTime, TimeUnit.MILLISECONDS);

            // Create advertisement sender and add to threadpool
            advertisementSender = new AdvertisementSender(this);
            // Schedule the advertisement sender to send out ad at an interval
            scheduler.scheduleAtFixedRate(advertisementSender, 0, configuration.sleepTime, TimeUnit.MILLISECONDS);

            // Create search request receiever and add to threadpool
            searchRequestReceiver = new SearchRequestReceiver(this, fileTreeBrowser);
            // Schedule the receiver to process received search-requests at some interval 
            scheduler.scheduleAtFixedRate(searchRequestReceiver, 0, configuration.sleepTime, TimeUNit.MILLISECONDS);

            // Create search response receiver task which process incoming search responses
            searchResponseReceiver = new SearchResponseReceiver(this);
            // submit task to threadpool to run constantly 
            scheduler.submit(searchResponseReceiver);


            /**
             * Todo : implement other receiver handlers
             */

            Thread t = new Thead(this);
            t.start();


        } catch (Exception e) {
            System.err.println("MulticastHandler() : " + e.getMessage());
        }
    }

    /**
     * Waits for incoming messagse and delegates handling of message to
     * the appropriate thread based on the message type.
     */
    @Override
    public void run() {
        while (true) {
            // Receive incoming message
            Message message = rxMessage();

            if (message != null) {
                // Determine message type and delegate to appropriate task thread
                switch (message.getType()) {
                    case "advertisement":
                        advertisementReceiver.addAdvertisement(message);
                        break;
                    case "search-request" :
                        searchRequestReceiver.addSearchRequest(message);
                        break;
                    case "" :
                        searchResponseReceiver.addSearchResponse(message);
                        break;
                }
            }
        }
    }

    public void stopExecutorService() {
        scheduler.shutdown();
    }


    /**
     * Given an incoming message (in bytes), reads into buffer, converts to string and extracts
     * message protocol information into appropriate Message object.
     * @return : message object containing protocol information of received message.
     */
    public Message rxMessage() {
        // Setup buffer with max-size from configuration 
        byte[] buffer = new byte[configuration.maximumMessageSize];

        // Read from group into buffer and check it is not none
        if (multicastEndpoint.rx(buffer) != MulticastEndpoint.PkyType.none) {
            // decode bytes into string 
            String message = new String(buffer, StandardCharsets.US_ASCII).trim();

            Message message = parseMessageString(message);

            if (message != null) {
                configuration.log.writeLog("rx-> " + message.toString());
            }

            return message;
        }

        return null;
    }

    /**
     * Given message string, breaks down into components and creates an appropriate Message object.
     * @param messageString : the message in string form.
     * @return Message object containing data from message string.
     */
    public Message parseMessageString(String messageString) {
        // Split string into components
        String[] components = messageString.split(":");

        if (components.length < 3) {
           return null;
        }

        String identifier  = components[1];
        long serialNo      = Long.parseLong(components[2]);
        String timestamp   = components[3];
        String messageType = components[4];

        String[] payload = Arrays.copyOfRange(components, 5, components.length);

        switch (messageType) {
            case "advertisement" :
                int port = Integer.parseInt(payload[0]);

                AdvertisementMessage advertisementMessage = 
                                        new AdvertisementMessage(port, timestamp, identifier, serialNo);

                // Obtain array of services 
                String[] services = payload[1].split(",");

                for (String service : services) {
                    String[] servicePair = service.split("=");
                    String serviceName   = servicePair[0];
                    boolean serviceValue = servicePair[1].equals("true");

                    if (serviceName.equals("search")) {
                        advertisementMessage.setSearchPossible(serviceValue);
                    } else if (serviceName.equals("download")) {
                        advertisementMessage.setDownloadPossible(serviceValue);
                    }
                }
                return advertisementMessage;
                break;
            case "search-request" :
                String searchString = payload[0]; // the search string to query 

                SearchRequestMessage searchRequestMessage = 
                                        new SearchRequestMessage(searchString, timestamp, identifier, serialNo);

                return searchRequestMessage;
                break;
            case "search-result" :
                // should be <current machine's identifier> : <response serialNo>
                String responseIdentifier = payload[0]; 
                // should be same serial no as used for tx search-request
                String responseSerialNo   = Long.parseLong(payload[1]); 
                String searchResultString = payload[2];

                SearchResultMessage searchResultMessage = 
                                        new SearchResultMessage(searchResultString, responseSerialNo, responseIdentifier, timestamp, identifier, serialNo);

                return searchResultMessage;
                break;
            case "search-error" :
                // should be <current machine's identifier> : <response serialNo>
                String responseIdentifier = payload[0]; 
                // should be same serial no as used for tx search-request (responseSerialNo == serialNo)
                String responseSerialNo   = Long.parseLong(payload[1]); 

                SearchErrorMessage searchErrorMessage = 
                                        new SearchErrorMessage(responseIdentifier, responseSerialNo, timestamp, identifier, serialNo);

                return searchErrorMessage;
                break;
            case "download-request" :

                break;
            case "download-result" :

                break;
        }
    }

    /**
     * Method for sending out a message into multicast group.
     * @param message : the Message object to send out to multicast group.
     * @return boolean indicating whether transmission was successful or not.
     */
    public boolean txMessage(Message message) {
        boolean done = false;
        byte buffer[];

        if (message != null) {
            String messageString = message.toString();
            buffer = messageString.getBytes(StandardCharsets.US_ASCII);
         
            // Check if message was successfully sent out
            if (multicastEndpoint.tx(MulticastEndpoint.PktType.ip6, buffer)) {
                done = true;
                configuration.log.writeLog("tx-> " + messageString);
            }         
        } else {
            System.err.println("MulticastHandler.txMessage() -> ERROR : Message was null.");
        }
        
        return done;
    }


    /**
     * Method to send out tx search-request message 
     * In resposne to user using ":search" command
     */
    public boolean txSearchRequest(String searchString)  {
        SearchRequestMessage searchRequestMessage = new SearchRequestMessage(searchString);

        txMessage(searchRequestMessage);
    }


}