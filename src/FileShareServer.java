import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileShareServer {
    ServerSocket svrSocket;

    public FileShareServer(int port) throws IOException {
        svrSocket = new ServerSocket(port);

        // Create listening thread
        Thread listeningThread = new Thread(() -> {
            while (true) {
                try {
                    System.out.printf("Listening at port %d...", port);
                    Socket clSocket = svrSocket.accept();
                    // User authentication
                    // ...

                    // Create connection thread
                    Thread connThread = new Thread(() -> {
                        try {
                            serve(clSocket);
                        } catch (Exception e) {
                            // System.err.println("connection dropped.");
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void serve(Socket clSocket) {
        System.out.printf("Established a connection to host %s:%d\n\n", clSocket.getInetAddress(),
                clSocket.getPort());
        while (true) {
            // Get stream

            // Get request type

            // Respond (call the private functions)

        }
    }

    private void sendList() {

    }

    private void makeDirectory() {

    }

    private void sendFile() {

    }

    private void receiveFile() {

    }

    private void deleteFile() {

    }

    private void deleteDirectory() {

    }

    private void rename() {

    }

    private void detail() {

    }

    private boolean verifyUser() {

        return false;
    }

}
