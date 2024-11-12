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
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * Class implementation for Message object.
 * Used to represent and contain information for any protocol message.
 */
public abstract class Message() {
    // Serial number for the message header
    private static final long serialNo = System.currentTimeMillis();

    /**
     * Header Attributes
     */ 
    private String username;                // Identifier Field 
    private String hostname;                // Identifier Field 
    private String timestamp;               // Timestamp of header 
    private String identifier;              // The identifier of message (e.g FQDN)


    /**
     * Payload
     */
    // private String responseID;   // response identifier (same as identifier used in request) 

    /**
     * Constructor for Message Object.
     */
    public Message() {
        try {
            this.username = System.getProperty("user.name");
            this.hostname = InetAddress.getLocalHost().getCanonicalHostName();
            this.identifier = this.username + "@" + this.hostname;
        } catch (UnknownHostException e) {
            System.out.println("Message() : unknown hostname: " + e.getMessage());
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");
        this.timestamp = simpleDateFormat.format(new Date());
    }

    /**
     * Converts a time stamp string in format of "yyyyMMdd-HHmmss.SSS" to Date object
     * @param timestamp : the 
     */
    public Date timestampToDate(String timestamp) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");
        return simpleDateFormat.parse(timestamp);
    }

    /**
     * Abstract method for get type 
     * @return type of message : "advertisement", "search", "download"
     */
    public abstract String getType();
}

