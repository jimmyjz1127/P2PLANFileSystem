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
 * Contains all protocol information for a search-result message 
 * 
 * @author 190015412 
 * @since November 2024
 */
public class SearchResultMessage extends Message {
    private String searchResultString;  // The path to file matching request query
    private String responseIdentifier;  // The response identifier 
    private long   responseSerialNo;    // the response serial number


    /**
     * Constructor for tx SearchResultMessage.
     * @param responseIdentifier   : the identifier of the host the sent received search-request
     * @param responseSerialNo     : the serial number used in the received search request - used to generate <response-id>
     * @param searchResultString     : the filepath to the files corresponding to searchquery in request.
     */
    public SearchResultMessage(String responseIdentifier, long responseSerialNo, String searchResultString) {
        super();

        this.searchResultString = searchResultString;
        this.responseIdentifier = responseIdentifier;
        this.responseSerialNo = responseSerialNo;
    }

    /**
     * Constructor for rx SearchResultMessage.
     * @param searchResultString : the result file string returned by remote machine.
     * @param responseSerialNo   : serial number used for original search-request
     * @param responseIdentifier : the identifier used by current machine for search-request.
     * @param timestamp          : the timestamp of the incoming advertisement.
     * @param identifier         : the identifier of the server that sent advertisement.
     * @param serialNo           : serialNo of received advertisement.
     */
    public SearchResultMessage(String searchResultString, long responseSerialNo, String responseIdentifier, 
                               String timestamp, String identifier, long serialNo) {

        super(identifier.split("@")[0], identifier.split("@")[1], timestamp, identifier, serialNo);

        this.searchResultString = searchResultString;
        this.responseSerialNo = responseSerialNo;
        this.responseIdentifier = responseIdentifier;
    }

    /**
     * Getter function for searchResultString.
     */
    public String getSearchResultString() {
        return searchResultString;
    }

    /**
     * Returns the type of message.
     */
    public String getType() {
        return "search-result";
    }


    /**
     * toString method for converting message protocol information into suitable protocol message string.
     * : <current identifier> : <current serialNo> : <current timestamp> : search-result : <identifier of request> : <request serialNo> : <result string> : 
     */
    @Override 
    public String toString() {
        String header = ":" + getIdentifier() + ":" + getSerialNo() + ":" + getTimestamp();
        String payload = ":search-result:" + responseIdentifier + ":" + responseSerialNo + ":" + searchResultString + ":";
        return header + payload; 
    } 
}