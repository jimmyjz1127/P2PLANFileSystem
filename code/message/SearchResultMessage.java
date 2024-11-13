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
    private String searchFileString;    // The search string query 
    private String responseIdentifier;  // The response identifier 
    private long   responseSerialNo;    // the response serial number


    /**
     * Constructor for tx SearchResultMessage.
     * @param responseIdentifier   : the identifier of the host the sent received search-request
     * @param responseSerialNo     : the serial number used in the received search request - used to generate <response-id>
     * @param searchFileString     : the filepath to the files corresponding to searchquery in request.
     */
    public SearchResultMessage(String responseIdentifier, long responseSerialNo, String searchFileString) {
        super();

        this.searchFileString = searchFileString;
        this.responseIdentifier = responseIdentifier;
        this.responseSerialNo = responseSerialNo;
    }

    /**
     * Constructor for rx SearchResultMessage.
     * @param searchFileString   : the result file string returned by remote machine.
     * @param responseSerialNo   : serial number used for original search-request
     * @param responseIdentifier : the identifier used by current machine for search-request.
     * @param timestamp          : the timestamp of the incoming advertisement.
     * @param identifier         : the identifier of the server that sent advertisement.
     * @param serialNo           : serialNo of received advertisement.
     */
    public SearchResultMessage(String searchFileString, long responseSerialNo, String responseIdentifier, 
                               String timestamp, String identifier, long serialNo) {

        String[] identifierArr = identifier.split("@");

        String username = identifierArr[0];
        String hostname = identifierArr[1];

        super(username, hostname, timestamp, identifier, serialNo);

        this.searchFileString = searchFileString;
        this.responseSerialNo = responseSerialNo;
        this.responseIdentifier = responseIdentifier;
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