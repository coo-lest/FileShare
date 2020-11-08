import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileShareServer {
    ServerSocket svrSocket;

    public FileShareServer(int port) throws IOException {
        System.out.println("Server created");
        svrSocket = new ServerSocket(port);

        // Create listening thread
        Thread listeningThread = new Thread(() -> {
            System.out.printf("Listening at port %d...", port);
            while (true) {
                try {
                    Socket clSocket = svrSocket.accept();

                    // Create connection thread
                    Thread connThread = new Thread(() -> {
                        try {
                            // User authentication
                            DataOutputStream dout = new DataOutputStream(clSocket.getOutputStream());
                            DataInputStream din = new DataInputStream((clSocket.getInputStream()));
                            byte[] buffer = new byte[1024];

                            // Request username
                            System.out.println("waiting for username...");
                            String username = FileShare.receiveMsg(din).body;
                            // Request password
                            System.out.println("waiting for password...");

                            FileShare.sendMsg(dout, new Message(MessageType.REQUEST, "Password: "));

                            String password = FileShare.receiveMsg(din).body;
                            // Verify
                            if (verifyUser(username, password)) {
                                // Send success message
                                FileShare.sendMsg(dout, new Message(MessageType.SUCCESS, "Login successful"));
                                // Start serving the client socket
                                serve(clSocket);

                            } else {
                                // Abort the connection
                                FileShare.sendMsg(dout, new Message(MessageType.FAILURE, "Wrong username or password"));
                            }
                        } catch (Exception e) {
                            // System.err.println("connection dropped.");
                            e.printStackTrace();
                        }
                    });
                    connThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        listeningThread.start();
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

    private boolean verifyUser(String username, String password) {
        System.out.println("Received username: " + username);
        System.out.println("Received password: " + password);
        return true;
    }

}
