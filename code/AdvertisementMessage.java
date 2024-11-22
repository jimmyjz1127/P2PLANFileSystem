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
 * AdvertisementMessage child class of Message class.
 * 
 * @author 190015412
 * @since November 2024
 */
public class AdvertisementMessage extends Message {
    private int serverPort;                     // port the server (which sent advertisement) is listening on  
    private boolean searchPossible = false;     // boolean for whether search capability is possible
    private boolean downloadPossible = false;   // boolean for whether download capability is possible 

    /**
     * Constructor for tx AdvertisementMessage.
     * @param configuration : the current machine's configuration.
     */
    public AdvertisementMessage(Configuration configuration) {
        super();

        this.serverPort = configuration.mPort;
        this.searchPossible = configuration.search;
        this.downloadPossible = configuration.download;
    }

    /**
     * Constructor for rx AdvertisementMessage. 
     * @param serverPort : the port of the remote server that sent advertisement.
     * @param timestamp  : the timestamp of the incoming advertisement.
     * @param identifier : the identifier of the server that sent advertisement.
     * @param serialNo   : serialNo of received advertisement.
     */ 
    public AdvertisementMessage(int serverPort, String timestamp, String identifier, long serialNo) {
        // (username, hostname, timestamp, identifier, serialNo)
        super(identifier.split("@")[0], identifier.split("@")[1], timestamp, identifier, serialNo);

        this.serverPort = serverPort;
    }

    /**
     * Setter function for serverPort.
     * @param serverPort : the server port 
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Setter function for searchPossible.
     * @param searchPossible : boolean true or false
     */
    public void setSearchPossible(boolean searchPossible) {
        this.searchPossible = searchPossible;
    }

    /**
     * Setter function for downloadPossible.
     * @param downloadPossible : boolean true or false
     */
    public void setDownloadPossible(boolean downloadPossible) {
        this.downloadPossible = downloadPossible;
    }

    /**
     * Returns the a string indicating the type of message
     */
    public String getType() {
        return "advertisement";
    }

    /**
     * Getter function for server port.
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Returns whether the searchPossible attribute.
     * @return the boolean searchPossible attribute.
     */
    public boolean isSearchPossible() {
        return searchPossible;
    }

    /**
     * Returns whether the downloadPossible attribute.
     * @return the boolean downloadPossible attribute.
     */
    public boolean isDownloadPossible() {
        return downloadPossible;
    }

    /**
     * Returns a string of capable services as should be displayed
     * in an advertisement message.
     * @return String of format <service>=<true or false>, <service>=<true of false>
     */
    public String getServicesString() {
        return "search=" + searchPossible + "," + "download=" + downloadPossible;
    }

    /**
     * 
     */
    @Override
    public String toString() {
        String header = ":" + getIdentifier() + ":" + getSerialNo() + ":" + getTimestamp();
        String payload = ":advertisement:" + getServerPort() + ":" + getServicesString() + ":";

        return header + payload;
    }

    /**
     * Main method for debugging and testing purposes.
     */
    public static void main(String[] args) {
        Configuration conf = new Configuration("filetreebrowser.properties");
        AdvertisementMessage msg = new AdvertisementMessage(conf);

        System.out.println(msg.toString());
    }
}
