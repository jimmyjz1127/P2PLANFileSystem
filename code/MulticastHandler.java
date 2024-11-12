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

        String identifier = components[1];
        String serialNo = components[2];
        String timestamp = components[3];
        String messageType = components[4];

        String[] payload = Arrays.copyOfRange(components, 5, components.length);

        switch (messageType) {
            case "advertisement" :
                Message advertisementMessage = new AdvertisementMessage(this.configuration);

                int port = Integer.parseInt(payload[0]);

                // Obtain array of services 
                String[] services = payload[1].split(",");

                for (String service : services) {
                    String[] servicePair = service.split("=");
                    String serviceName = servicePair[0];
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

                break;
            case "search-result" :

                break;
            case "search-error" :

                break;
            case "download-request" :

                break;
            case "download-result" :

                break;
        }
    }


    public Message 


}