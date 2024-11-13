package code;

import code.message.*;

import java.net.*;
import java.util.Arrays;
import java.text.ParseException;

/**
 * Class for handling multicast connections.
 * Implements Runnable to allow handling mulitple incoming flows using threading.
 */

public class MulticastHandler implements Runnable {
    // private MulticastSocket multicastSocket;    // socket to multicast group
    private MulticastEndpoint multicastEndpoint; 
    private Configuration configuration;


    public MulticastHandler(Configuration configuration) {
        this.configuration = configuration;

        try {
            // Initialize multicast socket with configurations
            multicastEndpoint = new MulticastEndpoint(this.configuration);
            configuration.log.writeLog("Multicast Endpoint Created : " + this.configuration.mAddr6 + ":" + this.configuration.mPort);

            // Join the multicast group 
            multicastEndpoint.join();
            configuration.log.writeLog("Joined Multicast Group : " + this.configuration.mGroup);


        } catch (Exception e) {
            System.err.println("MulticastHandler() : " + e.getMessage());
        }
    }

    /**
     * 
     */
    @Override
    public void run() {
        while (true) {

        }
    }


    public Message rxMessage() {
        // Setup buffer with max-size from configuration 
        byte[] buffer = new byte[configuration.maximumMessageSize];

        // Read from group into buffer
        multicastEndpoint.rx(buffer);

        // decode bytes into string 
        String message = new String(buffer, StandardCharsets.US_ASCII).trim();



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


    public Message 


}