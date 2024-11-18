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
 * 
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



    public String getType() {
        return "download-result";
    }
}
