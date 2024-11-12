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
    private String service;                     // beacon, search, download, ...
    private int serverPort;                     // port number of server listening for incoming connections  
    private boolean searchPossible = false;     // boolean for whether search capability is possible
    private boolean downloadPossible = false;   // boolean for whether download capability is possible 
    private String searchType;                  // Search Type : none, path, path-filename, path-filename-substring

    public AdvertisementMessage(Configuration configuration) {
        super();

        this.serverPort = configuration.mPort;
        this.searchPossible = configuration.search;
        this.downloadPossible = configuration.download;
        this.searchType = configuration.searchType;
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