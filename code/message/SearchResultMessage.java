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
 * Class for SearchResultMessage.
 */
public class SearchResultMessage extends Message {
    private String searchFilePath; 


    /**
     * Constructor for SearchResultMessage.
     * @param identifier        : response identifier (should be same as request).
     * @param serialNo          : serial number of message (should be same as request).
     * @param searchFilePath    : the filepath to the files corresponding to searchquery in request.
     */
    public SearchResultMessage(String identifier, long serialNo, String searchFilePath) {
        super();

        setIdentifier(identifier);
        setSerialNo(serialNo);
        this.searchFilePath = searchFilePath;
    }

    /**
     * Getter function for searchFilePath.
     */
    public String getSearchFilePath() {
        return searchFilePath;
    }

    /**
     * Returns the type of message.
     */
    public String getType() {
        return "search-result";
    }
}