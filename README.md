# CS4105 Practical 2 : Discover and Download - LAN-based, distributed, multicast, file-download application
## Matriculation ID : 190015412
## Date : November 22, 2024

## Instructions 
1) Navigate to `code` directory
2) To compile files, enter command : `make`
3) To remove `.class` files, enter command :`make clean`
4) To run application and launch command line interface run : `java FileTreeBrowser`

## Files & Directories in `code/` 
| Name | Description |
|------|-------------|
| `downloads/` | Contains all files downloaded from other nodes using `:download` command  |
| `logs/` | Contains logs of IPv6 protocol messages  |
| `root_dir/` | Local filesystem directory of searchable & downloadable files/subdirectories  |
| `AdvertisementMessage.java` | Message object for storing protocol information on advertisement messages |
| `AdvertisementReceiver.java` | Runnable task for handling and processing received Advertisement messages  |
| `AdvertisementSender.java` | Runnable task for sending out advertisements about current machine to other nodes in multicast group  |
| `ByteReader.java` | For reading in byte-level data from inputstreams  |
| `Configuration.java` | For implementing `Configuration` object storing configuration data about current machine  |
| `cs4105_p2_protocol_specification.txt` | Protocol specification file  |
| `DownloadErrorMessage.java` | Message object for storing protocol information on `<download-error>` messages  |
| `DownloadRequestMessage.java` | Message object for storing protocol information on `<download-request>` messages |
| `DownloadResponseReceiver.java` | Runnable Task for handing and processing received `<download-response>` messages such as `<download-result>` and `<download-error>`  |
| `DownloadResultMessage.java` | Message object for storing protocol information on `<download-result>` messages  |
| `FileClient.java` | Runnable Task to support connecting to a remote LAN file server through TCP unicast to download files from a remote node in multicast group  |
| `FileServer.java` | Runnable task to support creating an ephemeral unicast TCP socket for serving files to remote clients  |
| `FileTreeBrowser.java` | Implements the command line interface entry point to application  |
| `filetreebrowser.properties` | Configuration file  |
| `LogFileWriter.java` | Implements a log file writer for writing log data to a log file  |
| `MakeFile` | MakeFile for building/compiling application   |
| `Message.java` | Message object parent class. All other message classes extend this class. Stores message protocol information common to all message types.  |
| `MulticastEndpoint.java` | Implements multicast socket object  |
| `MulticastHandler.java` | Runnable Task that coordinates all other Runnable Tasks in a threadpool. Can be viewed as the main thread of the application which takes in messages and delegates them to other Runnable Tasks to handle and process. |
| `SearchErrorMessage.java` | Message object for storing protocol information on `<search-error>` messages   |
| `SearchRequestMessage.java` | Message object for storing protocol information on `<search-request>` messages  |
| `SearchRequestReceiver.java` | Runnable Task for handling and processing received `<search-request>` messages  |
| `SearchResponseReceiver.java` | Runnable Task for handling and processing received `<search-response>` messages  |
| `SearchResultMessage.java` | Message object for storing protocol information about `<search-result>` messages  |

