package code.message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * AdvertisementMessage child class of Message class.
 */
public class AdvertisementMessage extends Message {
    // private String service;                     // beacon, search, download, ...
    private int serverPort;                     // port the server (which sent advertisement) is listening on  
    private boolean searchPossible = false;     // boolean for whether search capability is possible
    private boolean downloadPossible = false;   // boolean for whether download capability is possible 

    /**
     * Constructor for tx AdvertisementMessage.
     * @param configuration : the current machine's configuration.
     */
    public AdvertisementMessage(Configuration configuration) {
        super();

        this.serverPort = configuration.mPort;
        this.searchPossible = configuration.search;
        this.downloadPossible = configuration.download;
        this.searchType = configuration.searchType;
    }

    /**
     * Constructor for rx AdvertisementMessage. 
     * @param serverPort : the port of the remote server that sent advertisement.
     * @param timestamp  : the timestamp of the incoming advertisement.
     * @param identifier : the identifier of the server that sent advertisement.
     * @param serialNo   : serialNo of received advertisement.
     */
    public AdvertisementMessage(int serverPort, String timestamp, String identifier, long serialNo) {
        String[] identifierArr = identifier.split("@");

        String username = identifierArr[0];
        String hostname = identifierArr[1];

        super(username, hostname, timestamp, identifier, serialNo);

        this.serverPort = serverPort;
    }

    /**
     * Setter function for serverPort.
     * @param serverPort : the server port 
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Setter function for searchPossible.
     * @param searchPossible : boolean true or false
     */
    public void setSearchPossible(boolean searchPossible) {
        this.searchPossible = searchPossible;
    }

    /**
     * Setter function for downloadPossible.
     * @param downloadPossible : boolean true or false
     */
    public void setDownloadPossible(boolean downloadPossible) {
        this.downloadPossible = downloadPossible;
    }

    /**
     * Setter function for searchType.
     * @param searchType : string "none", "path", "path-filename", "path-filename-substring"
     */
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    /**
     * Returns the a string indicating the type of message
     */
    public String getType() {
        return "advertisement";
    }

    /**
     * Getter function for server port.
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * 
     */
    public boolean isSearchPossible() {
        return searchPossible;
    }

    /**
     * 
     */
    public boolean isDownloadPossible() {
        return downloadPossible;
    }

    /**
     * 
     */
    public String getSearchType() {
        return searchType;
    }
}