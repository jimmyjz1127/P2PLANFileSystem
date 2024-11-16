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
    private String searchString;       // The search string to query

    /**
     * Constructor for tx SearchRequestMessage.
     * @param searchString : the query being searched. 
     */
    public SearchRequestMessage(String searchString) {
        super();

        this.searchString = searchString;
    }

    /**
     * Constructor for rx SearchRequestMessage.
     * @param searchString  : the string to query on current machine's directory.
     * @param timestamp     : the timestamp of the incoming advertisement.
     * @param identifier    : the identifier of the server that sent advertisement.
     * @param serialNo      : serialNo of received advertisement.
     */
    public SearchRequestMessage(String searchString, String timestamp, String identifier, long serialNo) {
        super(identifier.split("@")[0], identifier.split("@")[1], timestamp, identifier, serialNo);

        this.searchString = searchString;
    }

    /**
     * Returns the a string indicating the type of message
     */
    public String getType() {
        return "search-request";
    }

    /**
     * Getter function for searchQuery.
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * Equals method for determining if two requests,
     *  (potentially sent from different timestamps) are the same.
     */
    @Override
    public boolean equals(Object obj) {
        // Check reference equality
        if (this == obj) {
            return true;
        }

        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }

        SearchRequestMessage msg = (SearchRequestMessage) obj;

        return (msg.getIdentifier() == this.getIdentifier()) &&
               (msg.getSerialNo() == this.getSerialNo())
    }

    /**
     * 
     */
    @Override
    public String toString() {
        String header = ":" + getIdentifier() + ":" + getSerialNo() + ":" + getTimeStamp();
        String payload = ":search-request:" + searchString + ":";

        return header + payload;
    }
 }