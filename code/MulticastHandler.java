import java.io.File;
import java.net.*;
import java.util.Arrays;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit ;
import java.util.ArrayList;

/**
 * Class for handling multicast connections.
 * Implements Runnable to allow handling mulitple incoming flows 
 * of various types (advertisement, search-request, etc) using threading.
 * 
 * @author 190015412
 * @since Novemeber 2024
 */

public class MulticastHandler implements Runnable {
    /**
     * ANSI Escape codes for colored CMD output
     */
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31;1m";
    public static final String GREEN = "\033[32;1m";
    public static final String BLUE = "\033[36;1m";


    private MulticastEndpoint multicastEndpoint; 
    public Configuration configuration;
    private ScheduledExecutorService scheduler;

    public AdvertisementReceiver advertisementReceiver;
    public AdvertisementSender advertisementSender;
    public SearchRequestReceiver searchRequestReceiver;
    public SearchResponseReceiver searchResponseReceiver;
    public DownloadRequestReceiver downloadRequestReceiver;
    public DownloadResponseReceiver downloadResponseReceiver;

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

            // Join the multicast group k
            multicastEndpoint.join();
            configuration.log.writeLog(configuration.identifier + " Joined Multicast Group");

            // Create scheduled threadpool
            scheduler = Executors.newScheduledThreadPool(6);

            // Create advertisement receiver an add to threadpool
            advertisementReceiver = new AdvertisementReceiver(this);
            // Schedule the advertisement receiver task (of removing expired advertisements)
            scheduler.scheduleAtFixedRate(advertisementReceiver, 0, configuration.sleepTime, TimeUnit.MILLISECONDS);
            
            // Create advertisement sender and add to threadpool
            advertisementSender = new AdvertisementSender(this);
            // Schedule the advertisement sender to send out ad at an interval
            scheduler.scheduleAtFixedRate(advertisementSender, 0, configuration.sleepTime, TimeUnit.MILLISECONDS);

            // Create search request receiever and add to threadpool
            searchRequestReceiver = new SearchRequestReceiver(this);
            // Schedule the receiver to process received search-requests at some interval 
            scheduler.scheduleAtFixedRate(searchRequestReceiver, 0, configuration.sleepTime, TimeUnit.MILLISECONDS);

            // Create search response receiver task to process incoming search responses
            searchResponseReceiver = new SearchResponseReceiver(this);
            // submit task to threadpool to run constantly 
            scheduler.scheduleAtFixedRate(searchResponseReceiver, 0, configuration.sleepTime, TimeUnit.MILLISECONDS);

            // Create download request receiver runnable task to process incoming download requests
            downloadRequestReceiver = new DownloadRequestReceiver(this);
            // submit task to threadpool to run at an interval
            scheduler.scheduleAtFixedRate(downloadRequestReceiver, 0, configuration.sleepTime, TimeUnit.MILLISECONDS);

            // Create download response receiver task to process incoming download responses
            downloadResponseReceiver = new DownloadResponseReceiver(this);
            // Submit task to threadpool to run 
            scheduler.scheduleAtFixedRate(downloadResponseReceiver, 0, configuration.sleepTime, TimeUnit.MILLISECONDS);

            Thread t = new Thread(this);
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
                        advertisementReceiver.addAdvertisement((AdvertisementMessage) message);
                        break;
                    case "search-request" :
                        searchRequestReceiver.addSearchRequest((SearchRequestMessage) message);
                        break;
                    case "search-result" :
                        searchResponseReceiver.addSearchResponse(message);
                        break;
                    case "search-error" :
                        searchResponseReceiver.addSearchResponse(message);
                        break;
                    case "download-request" :
                        downloadRequestReceiver.addDownloadRequest((DownloadRequestMessage) message);
                        break;
                    case "download-result" :
                        downloadResponseReceiver.addDownloadResponse(message);
                        break;
                    case "download-error" :
                        downloadResponseReceiver.addDownloadResponse(message);
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
        if (multicastEndpoint.rx(buffer) != MulticastEndpoint.PktType.none) {
            // decode bytes into string 
            String messageString = new String(buffer, StandardCharsets.US_ASCII).trim();

            Message message = parseMessageString(messageString);

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
        Long responseSerialNo;
        String responseIdentifier;

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
            case "search-request" :
                String searchString = payload[0]; // the search string to query 

                // only if current machine has search capability will it respond to search-request
                if (configuration.search) {
                    SearchRequestMessage searchRequestMessage = 
                                        new SearchRequestMessage(searchString, timestamp, identifier, serialNo);

                    return searchRequestMessage;
                }
                return null;
               
            case "search-result" :
                // should be <current machine's identifier> : <response serialNo>
                responseIdentifier = payload[0]; 
                
                if (responseIdentifier.equals(configuration.identifier)) {
                     // should be same serial no as used for tx search-request
                    responseSerialNo   = Long.parseLong(payload[1]); 
                    String searchResultString = payload[2];

                    SearchResultMessage searchResultMessage = 
                                            new SearchResultMessage(searchResultString, responseSerialNo, responseIdentifier, timestamp, identifier, serialNo);

                    return searchResultMessage;
                }
                return null;
               
            case "search-error" :
                // should be <current machine's identifier> : <response serialNo>
                responseIdentifier = payload[0]; 

                if (responseIdentifier.equals(configuration.identifier)) {
                     // should be same serial no as used for tx search-request (responseSerialNo == serialNo)
                    responseSerialNo   = Long.parseLong(payload[1]); 

                    SearchErrorMessage searchErrorMessage = 
                                            new SearchErrorMessage(responseIdentifier, responseSerialNo, timestamp, identifier, serialNo);

                    return searchErrorMessage;
                }
                return null;
            case "download-request" :
                String targetIdentifier = payload[0];

                // If download-request was intended for current machine and current machine does have download capability
                if (targetIdentifier.equals(configuration.identifier) && configuration.download) {
                    String fileString = payload[1];

                    DownloadRequestMessage downloadRequestMessage = 
                                            new DownloadRequestMessage(fileString, targetIdentifier, timestamp, identifier, serialNo);
                    return downloadRequestMessage;
                }
                return null;
            case "download-result" :
                responseIdentifier = payload[0];

                if (responseIdentifier.equals(configuration.identifier)) {
                    responseSerialNo = Long.parseLong(payload[1]);
                    String fileString = payload[2];
                    int fileTransferPort = Integer.parseInt(payload[3]);

                    DownloadResultMessage downloadResultMessage = 
                                        new DownloadResultMessage(fileString, responseIdentifier, responseSerialNo, fileTransferPort, timestamp, identifier,serialNo);
                    return downloadResultMessage;
                }
                return null;
            case "download-error" :
                responseIdentifier = payload[0];

                if (responseIdentifier.equals(configuration.identifier)) {
                    responseSerialNo = Long.parseLong(payload[1]);
                    int numMatchingFiles = Integer.parseInt(payload[2]);

                    DownloadErrorMessage downloadErrorMessage = 
                                        new DownloadErrorMessage(responseIdentifier, responseSerialNo, numMatchingFiles, timestamp, identifier, serialNo);
                    return downloadErrorMessage;                    
                }
                return null;                
        } // end switch 
        return null;
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
    public void txSearchRequest(String searchString)  {
        SearchRequestMessage searchRequestMessage = new SearchRequestMessage(searchString);

        txMessage(searchRequestMessage);
    }


    /**
     * Method to send out tx download-request message 
     * In response to user using :download command
     */
    public void txDownloadRequest(String fileString, String targetIdentifier) {
        AdvertisementMessage targetAdvertisement = advertisementReceiver.getAdvertisementMessage(targetIdentifier);

        // Check that download is possible for target machine
        if (targetAdvertisement != null && !targetAdvertisement.isDownloadPossible()) {
            System.out.println(RED + "[DOWNLOAD ERROR]" + RESET + " : " + BLUE + targetIdentifier + RESET + " does not have download capability.");
            return;
        } 

        // Check that we have received an advertisement from the given target-identifier
        if (advertisementReceiver.getAdvertisementMessage(targetIdentifier) == null) {
            System.out.println(RED + "[DOWNLOAD ERROR]" + RESET + " : target " + BLUE + targetIdentifier + RESET + " does not exist in multicast group.");
            return; 
        }

        DownloadRequestMessage downloadRequestMessage = new DownloadRequestMessage(fileString, targetIdentifier);
        txMessage(downloadRequestMessage);
    }

    /**
     * Method to retrieve all files and directories that substring-match a search string.
     * @param searchString : the search string provided by search-request.
     * @return ArrayList of files whose paths/file name substring match the given search string.
     */
    public ArrayList<File> getMatchingFiles(String searchString) {
        ArrayList<File> matchingFiles = new ArrayList<>();

        // instantiate file object around directory to perform directory walk
        // File rootDirectory = new File(thisDir); // thisDir = root_dir
        File rootDirectory = new File(configuration.rootDir);

        // Make sure the given directory path is valid
        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            System.err.println("FileTreeBrowser.getMatchingFiles() : Error - invalid directory path.");
            return null;
        } 

        searchDirectory(rootDirectory, searchString, "", matchingFiles);
        return matchingFiles;
    }

    /**
     * Helper function which recursively looks through a directory for files/sub-directories that 
     * match a given search string.
     * 
     * @param currentDirectory : the current directory to perform the search in.
     * @param searchString : the string to perform substring matching to
     * @param relativePath : the current relative path built so far 
     * @param matchingFiles : ArrayList of files whose paths/file name substring match the given search string.
     */
    public void searchDirectory(File currentDirectory, String searchString, 
                                     String relativePath, ArrayList<File> matchingFiles) {
        // Obtain all files/subdirectories in current directory
        File[] files = currentDirectory.listFiles();

        if (files == null) {
            return;
        }

        // Iterate through files 
        for (File file : files ) {
            String currentRelativePath = 
                        relativePath.isEmpty() ? file.getName() : relativePath + File.separator + file.getName();

            // if we get a substring match
            if (file.getAbsolutePath().contains(searchString)) {
                matchingFiles.add(file);
            }

            // if directory, further explore the sub-directory
            if (file.isDirectory()) {
                searchDirectory(file, searchString, currentRelativePath, matchingFiles);
            }
        }
  
  }


}