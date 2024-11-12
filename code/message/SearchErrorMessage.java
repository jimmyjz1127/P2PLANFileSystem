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


public class SearchErrorMessage extends Message {

    /**
     * Constructor for search error.
     * @param identifier : the identifier of message (should be same as request).
     * @param serialNo   : the serialNo of the message (should be same as request).
     */
    public SearchErrorMessage(String identifier, long serialNo) {
        super();

        setIdentifier(identifier);
        setSerialNo(serialNo);
    }


    /**
     * Returns the a string indicating the type of message
     */
    public String getType() {
        return "search-error";
    }
}