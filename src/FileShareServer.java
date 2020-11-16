import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FileShareServer {
    ServerSocket svrSocket;
    DatagramSocket udpSocket;

    public FileShareServer(int tcpPort, int udpPort) throws IOException {
        System.out.println("Server created");
        svrSocket = new ServerSocket(tcpPort);
        udpSocket = new DatagramSocket(udpPort);

        // Create listening thread
        Thread listeningThread = new Thread(() -> {
            System.out.printf("Listening at port %d...", tcpPort);
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

        // Create UDP listening thread
        Thread udpListen = new Thread(() -> {
            while (true) {  // TODO: isDiscoverable
                try {
                    System.out.println("Waiting for UDP connection");
                    Object[] udpRcvd = Message.udpReceive(udpSocket);  // {Message, InetAddress, int}
                    Message msg = (Message) udpRcvd[0];
                    InetAddress srcAdd = (InetAddress) udpRcvd[1];
                    int srcPort = (Integer) udpRcvd[2];

                    if (msg.type == MessageType.DISCOVERY) {
                        Message.udpSend(udpSocket, srcAdd, srcPort, new Message(MessageType.SUCCESS, InetAddress.getLocalHost().getHostName()));
                    }
                } catch (NegativeArraySizeException e) {
                    // Corrupted user datagram received
                    // Ignore
                } catch (StreamCorruptedException e) {
                    // Corrupted user datagram received
                    // Ignore
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        udpListen.start();
    }

    private void serve(Socket clSocket) throws IOException {
        System.out.printf("Established a connection to host %s:%d\n\n", clSocket.getInetAddress(),
                clSocket.getPort());
        // Get stream
        DataInputStream din = new DataInputStream(clSocket.getInputStream());
        DataOutputStream dout = new DataOutputStream(clSocket.getOutputStream());

        while (true) {
            // Get request type
            Message msg = FileShare.receiveMsg(din);
            switch (msg.type) {
                case DOWNLOAD:
                    String filename = msg.body;
                    sendFile(dout, filename);
                    break;
            }
        }
    }

    private void sendList() {

    }

    private void makeDirectory() {

    }

    private void sendFile(DataOutputStream dout, String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            FileShare.sendMsg(dout, new Message(MessageType.FAILURE, "File not exist"));
        } else {
            // Start file transmission
            FileInputStream fin = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            // Transmit fSize
            long fSize = f.length();
            dout.writeLong(fSize);
            // Transmit file content
            while (fSize > 0) {
                int read = fin.read(buffer);
                dout.write(buffer, 0, read);
                fSize -= read;
            }
        }

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

        File authFile = new File("authorized_users");
        if (authFile.isFile()) {
            try {
                FileInputStream fin = new FileInputStream(authFile);
                Scanner fsc = new Scanner(fin);
                String line;
                while (fsc.hasNextLine()) {
                    line = fsc.nextLine().trim();
                    String[] userInfo = line.split(" ", 2);
                    if (userInfo.length != 2) {
                        continue;   // In case of corrupted authorized_user file
                    }
                    if (userInfo[0].equals(username) && userInfo[1].equals(password)) {
                        return true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
