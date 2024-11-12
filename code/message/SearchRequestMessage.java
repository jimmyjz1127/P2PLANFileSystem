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


public class SearchRequestMessage extends Message {
    private String searchType;        // Search Type : none, path, path-filename, path-filename-substring
    private String searchQuery;       // The search query

    /**
     * Constructor for SearchRequestMessage.
     * @param searchType : the type of search being done (none,path,path-filename,path-filename-substring).
     * @param searchQuery : the query being searched. 
     */
    public SearchRequestMessage(String searchType, String searchQuery) {
        super();

        this.searchType = searchType;
        this.searchQuery = searchQuery;
    }

    /**
     * Returns the a string indicating the type of message
     */
    public String getType() {
        return "search-request";
    }

    /**
     * Getter function for search type 
     */
    public String getSearchType() {
        return searchType;
    }

    /**
     * Getter function for searchQuery.
     */
    public String getSearchQuery() {
        return searchQuery;
    }
}