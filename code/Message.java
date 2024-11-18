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
public abstract class Message {
    // Serial number for the message header
    private long serialNo;

    /**
     * Header Attributes
     */ 
    private String username;                // Identifier Field 
    private String hostname;                // Identifier Field 
    private String timestamp;               // Timestamp of header 
    private String identifier;              // The identifier of message (e.g FQDN)

    /**
     * Constructor for tx Message Object.
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
        this.serialNo = System.currentTimeMillis();
    }

    /**
     * Constructor for rx Message object.
     * @param username     : username in identifier of message.
     * @param hostname     : the hostname in identifier of message.
     * @param timestamp    : the timestamp of incoming message.
     * @param identifier   : the identifier of the remote machine that sent message.
     * @param serialNo     : unique serial number of message.
     */
    public Message(String username, String hostname, String timestamp, String identifier, long serialNo) {
        this.username = username;
        this.hostname = hostname;
        this.timestamp = timestamp;
        this.identifier = identifier;
        this.serialNo = serialNo;
    }


    /**
     * Converts a time stamp string in format of "yyyyMMdd-HHmmss.SSS" to Date object
     * @param timestamp : the timestamp in string format
     */
    public Date timestampToDate(String timestamp) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss.SSS");
            return simpleDateFormat.parse(timestamp);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Setter function for serialNo.
     */
    public void setSerialNo(long serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * Setter function for identifier.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Getter function the username.
     * @return : the username in identifier field
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Getter function of hostname.
     */
    public String getHostname() {
        return this.hostname;
    }

    /**
     * Getter function for timestamp string.
     */
    public String getTimestamp() {
        return this.timestamp;
    }

    /**
     * Getter function for serial number
     */
    public long getSerialNo() {
        return this.serialNo;
    }

    /**
     * Getter function for identifier.
     */
    public String getIdentifier() {
        return this.identifier;
    }
 
    /**
     * Abstract method for get type 
     * @return type of message : "advertisement", "search", "download"
     */
    public abstract String getType();

}

