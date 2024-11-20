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
 * Class for DownloadRequestMessage
 * Stores all protocol information for a download-request message.
 * 
 * @author 190015412 
 * @since November 2024
 */
public class DownloadRequestMessage extends Message { 
    private String fileString; // the path to file to download 
    private String targetIdentifier;

    /**
     * Constructor for tx DownloadRequestMessage 
     * 
     * @param fileString : the string of file located on remote machine to download to current machine
     * @param targetIdentifier : the identifier of the target machine to download from
     */
    public DownloadRequestMessage(String fileString, String targetIdentifier) {
        super();

        this.fileString = fileString;
        this.targetIdentifier = targetIdentifier;
    }

    /**
     * Constructor for the rx DownloadRequestMessage
     * 
     * @param fileString : the fileString of file on current machine which remote machine wants to download 
     * @param targetIdentifier : the identifier of current machine 
     * @param timestamp : timestamp of incoming search request message 
     * @param identifier : the identifier of remote machine that sent incoming download-request 
     * @param serialNo : the serial number of the download-request message.
     */
    public DownloadRequestMessage(String fileString, String targetIdentifier, String timestamp, String identifier, long serialNo) {
        super(identifier.split("@")[0], identifier.split("@")[1], timestamp, identifier, serialNo);

        this.fileString = fileString;
        this.targetIdentifier = targetIdentifier; // should be current machine's identfier
    }


    /**
     * Returns the a string indicating the type of message
     */
    public String getType() {
        return "download-request";
    }

    /**
     * Getter for filepath
     * @return : the filepath as string 
     */
    public String getFileString() {
        return fileString;
    }

    /**
     * Getter for target identifier.
     * 
     * @return targetidentifier as string 
     */
    public String getTargetIdentifier() {
        return targetIdentifier;
    }


    /**
     * To String 
     */
    @Override
    public String toString() {
        String header = ":" + getIdentifier() + ":" + getSerialNo() + ":" + getTimestamp();
        String payload = ":download-request:" + targetIdentifier + ":" + fileString + ":";

        return header + payload;
    }
}