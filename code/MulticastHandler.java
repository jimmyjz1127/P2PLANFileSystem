package code;

import code.message.*;

import java.net.*;
import java.util.Arrays;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.charset.StandardCharsets;

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
    private ExecutorService executorService;


    private AdvertisementReceiver advertisementReceiver;

    /**
     * Constructor for MulticastHandler.
     * @param configuration : the configuration of the current multicast node.
     */
    public MulticastHandler(Configuration configuration) {
        this.configuration = configuration;

        try {
            // Initialize multicast socket with configurations
            multicastEndpoint = new MulticastEndpoint(this.configuration);
            configuration.log.writeLog("Multicast Endpoint Created : " + this.configuration.mAddr6 + ":" + this.configuration.mPort);

            // Join the multicast group 
            multicastEndpoint.join();
            configuration.log.writeLog("Joined Multicast Group : " + this.configuration.mGroup);

            // Create threadpool
            executorService = Executors.newFixedThreadPool(7);

            // Create advertisement receiver an add to threadpool
            advertisementReceiver = new AdvertisementReceiver(this);
            executorService.submit(advertisementReceiver); 

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
            Message message = rxMessage();

            if (message != null) {
                switch (message.getType()) {
                    case "advertisement":
                        advertisementReceiver.addAdvertisement(message);
                        break;
                }
            }
        }
    }

    public void stopExecutorService() {
        executorService.shutdown();
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
                String searchFileString   = payload[2];

                SearchResultMessage searchResultMessage = 
                                        new SearchResultMessage(searchFileString, responseSerialNo, responseIdentifier, timestamp, identifier, serialNo);

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


    


}