import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FileShareClient {
    Socket socket;
    DatagramSocket udpSocket;
    String svrIp;
    boolean isConnected;
    int svrPort = 9999;
    int udpSvrPort = 9998;
    DataInputStream din;
    DataOutputStream dout;
    List<Host> hostList = new LinkedList<>();

    public FileShareClient() throws IOException {
        System.out.println("Client created");

//        Thread dinThread = new Thread(() -> {
//            while (true) {
//                byte[] buffer = new byte[1024];
//                try {
//                    din.read(buffer);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.print(new String(buffer));
//            }
//        });
//        dinThread.start();

    }

    void login(String username, String address) {
        try {
            svrIp = address;
            socket = new Socket(svrIp, svrPort);
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            FileShare.sendMsg(dout, new Message(MessageType.DATA, username));
            // Wait for request for password
            System.out.print(FileShare.receiveMsg(din).body);

            // Get and send password
            String password = FileShare.scanner.nextLine();
            FileShare.sendMsg(dout, new Message(MessageType.DATA, password));

            // Wait for authentication
            Message authRes = FileShare.receiveMsg(din);

            System.out.println(authRes.body);
            if (authRes.type == MessageType.SUCCESS) {
                isConnected = true;
            } else {
                isConnected = false;
                socket.close();
            }
            System.out.println(isConnected);
        } catch (IOException e) {
            System.err.println("Connection error");
        }

    }

    void discover() throws IOException {
        hostList.clear();
        // Listening response
        udpSocket = new DatagramSocket();
        Thread recResThread = new Thread(() -> {
            DatagramPacket resPacket = new DatagramPacket(new byte[1024], 1024);
            while (true) {
                try {
                    Object[] udpRcvd = Message.udpReceive(udpSocket);
                    Message msg = (Message) udpRcvd[0];
                    InetAddress srcAdd = (InetAddress) udpRcvd[1];
                    int srcPort = (Integer) udpRcvd[2];

                    if (msg.type == MessageType.SUCCESS) {
                        Host host = new Host(srcAdd, srcPort, msg.body);
                        // Add new host to hostList
                        if (!hostList.contains(host)) {
                            System.out.println(host);
                            hostList.add(host);
                        }
                    }

                } catch (StreamCorruptedException sce) {
                    // Corrupt user datagram received
                    // Ignore
                } catch (NegativeArraySizeException nase) {
                    // Corrupt user datagram received
                    // Ignore
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        recResThread.start();

        // Send discovery packet
        System.out.println("Scanning online hosts...");
        int count = 0;
        while (count++ < 5) {
            try {
                Message discMsg = new Message(MessageType.DISCOVERY, "");
                Message.udpSend(udpSocket, InetAddress.getByName("255.255.255.255"), udpSvrPort, discMsg);
                Thread.sleep(new Random().nextInt(100) + 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        recResThread.interrupt();

        System.out.println("Finished scanning");
    }


}
