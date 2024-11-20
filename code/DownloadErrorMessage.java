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
 * Class for download-error message 
 * Contains protocol information for download-error message.
 * 
 * @author 190015412 
 * @since November 2024
 */
public class DownloadErrorMessage extends Message { 
    // private String filepath; 
    private String responseIdentifier;
    private long responseSerialNo;

    /**
     * Constructor to tx download-error message
     * 
     * @param responseIdentifier : identifier of rx download-request 
     * @param responseSerialNo : the serial number used in corresponding rx download-request
     */
    public DownloadErrorMessage(String responseIdentifier, long responseSerialNo) {
        super();

        setSerialNo(responseSerialNo);

        this.responseIdentifier = responseIdentifier;
        this.responseSerialNo = responseSerialNo;
    }

    /**
     * Constructor for rx download-error message 
     * 
     * @param responseIdentifier : identifier for corresponding tx download-request 
     * @param responseSerialNo   : serial number of corresponding tx download-request 
     * @param timestamp : timestamp of download-error message
     * @param identifier : identifier of sender of download-error 
     * @param serialNo : should be same as corresponding tx download-request
     */
    public DownloadErrorMessage(String responseIdentifier, long responseSerialNo, String timestamp, String identifier, long serialNo) {
        super(identifier.split("@")[0], identifier.split("@")[1], timestamp, identifier, serialNo);

        this.responseIdentifier = responseIdentifier;
        this.responseSerialNo = responseSerialNo;
    }


    /**
     * Returns the type of message as string 
     */
    public String getType() {
        return "download-error";
    }

     /**
     * Getter for response identifier.
     * 
     * @return responseIdentifier as string 
     */
    public String getResponseIdentifier() {
        return responseIdentifier;
    }

    /**
     * Getter for response serial No
     * 
     * @return : the serial number 
     */
    public long getResponseSerialNo() {
        return responseSerialNo;
    }

    /**
     * To string to convert message into protocol string format
     */
    @Override
    public String toString() {
        String header = ":" + getIdentifier() + ":" + responseSerialNo + ":" + getTimestamp();
        String payload = ":download-result:" + responseIdentifier + ":" + responseSerialNo + ":";

        return header + payload;
    }
}