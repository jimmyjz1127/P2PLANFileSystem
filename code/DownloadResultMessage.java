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
 * Class for download-result message.
 * Contains protocol infomration for download-result message.
 * 
 * @author 190015412
 * @since November 2024
 */
public class DownloadResultMessage extends Message {
    private String filepath;            // Path of requested file 
    private String responseIdentifier;  // Identifier of requester 
    private long   responseSerialNo;    // Serial Number of download-request 
    private int    fileTransferPort;    // TCP port to connect to machine containing file 


    /**
     * Constructor for tx download-result message 
     * 
     * @param filepath : the filepath specified by corresponding rx download-request
     * @param responseIdentifier : the identifier of corresponding rx download-request 
     * @param responseSerialNo : the serial number of corresponding rx download-request message
     * @param fileTransferPort : port number on current machine for which remote machine can download file from 
     */
    public DownloadResultMessage(String filepath, String responseIdentifier, long responseSerialNo, int fileTransferPort) {
        super();

        this.filepath = filepath;
        this.responseIdentifier = responseIdentifier;
        this.responseSerialNo = responseSerialNo;
        this.fileTransferPort = fileTransferPort;
    }

    /**
     * Constructor for rx download-result message 
     * 
     * @param filepath : filepath from original tx download-request 
     * @param responseIdentifier : should be identifier of current machine
     * @param responseSerialNo : serial number of original tx download-request 
     * @param fileTransferPort : the port on remote machine for current machine to download file from 
     * @param timestamp : time stamp of rx download-message message
     * @param identifier : identifier of remote machine from which we want to download from
     * @param serialNo : the serial number of incoming download-result
     */
    public DownloadResultMessage(String filepath, String responseIdentifier, long responseSerialNo, int fileTransferPort,
                                 String timestamp, String identifier, long serialNo) {
        super(identifier.split("@")[0], identifier.split("@")[1], timestamp, identifier, serialNo);
        
        this.filepath = filepath;
        this.responseIdentifier = responseIdentifier;
        this.responseSerialNo = responseSerialNo;
        this.fileTransferPort = fileTransferPort;
    }

    /**
     * Returns the type of message as string 
     */
    public String getType() {
        return "download-result";
    }

    /**
     * Getter for filepath
     * @return : the filepath as string 
     */
    public String getFilepath() {
        return filepath;
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
     * Getter for the fileTransferPort
     */
    public int getFileTransferPort() {
        return fileTransferPort;
    }


    /**
     * To string to convert message into protocol string format
     */
    @Override
    public String toString() {
        String header = ":" + getIdentifier() + ":" + getSerialNo() + ":" + getTimeStamp();
        String payload = ":download-result:" + responseIdentifier + ":" + filepath + ":" + fileTransferPort + ":";
    }
 }
