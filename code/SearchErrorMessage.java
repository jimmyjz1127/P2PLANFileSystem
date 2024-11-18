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
 * Class for SearchErrorMessage.
 */
public class SearchErrorMessage extends Message {
    String responseIdentifier; // the identifier of host that sent search-request.
    long responseSerialNo; 

    /**
     * Constructor for tx SearchErrorMessage.
     * @param responseIdentifier : the identifier of message (should be same as request).
     */
    public SearchErrorMessage(String responseIdentifier, long responseSerialNo) {
        super();

        setSerialNo(responseSerialNo); // set serialNo used to that used in rx search-request

        this.responseIdentifier = responseIdentifier;
        this.responseSerialNo = responseSerialNo;
    }

    /**
     * Constructor for rx SearchErrorMessage.
     * @param responseIdentifier : the identifier used by current machine for search-request.
     * @param responseSerialNo   : the same serial number used in tx search-request.
     * @param timestamp          : the timestamp of the incoming advertisement.
     * @param identifier         : should be the same as responseSerialNo.
     * @param serialNo           : serialNo of received advertisement.
     */
    public SearchErrorMessage(String responseIdentifier, long responseSerialNo, String timestamp, String identifier, long serialNo) {
        /**
         * responseSerialNo == serialNo
         */

        super(identifier.split("@")[0], identifier.split("@")[1], timestamp, identifier, serialNo);

        this.responseIdentifier = responseIdentifier;
        this.responseSerialNo = responseSerialNo;
    }


    /**
     * Returns the a string indicating the type of message
     */
    public String getType() {
        return "search-error";
    }

    /**
     * toString() method to convert message protocol data into appropriate message string.
     * <current identifier> : <serialNo of requester> : <current timestamp> : "search-error" : <identifier of requester> : 
     */
    @Override
    public String toString() {
        String header = ":" + getIdentifier() + ":" + responseSerialNo + ":" + getTimestamp();
        String payload = ":search-error:" + responseIdentifier + ":";

        return header + payload;
    }
}