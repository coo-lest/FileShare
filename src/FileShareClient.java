import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class FileShareClient {
    Socket socket;
    DatagramSocket udpSocket;
    String svrIp;
    boolean isConnected;
    int svrPort = 9999;
    int udpSvrPort = 9998;
    DataInputStream din;
    DataOutputStream dout;

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
            }
            System.out.println(isConnected);
        } catch (IOException e) {
            System.err.println("Connection error");
        }

    }

    void discover() throws IOException {
        udpSocket = new DatagramSocket();
        // Send discovery packet
        Message discMsg = new Message(MessageType.DISCOVERY, "");
        Message.udpSend(udpSocket, InetAddress.getByName("255.255.255.255"), udpSvrPort, discMsg);

        // Receive response
        DatagramPacket resPacket = new DatagramPacket(new byte[1024], 1024);
        while (true) {
            Object[] udpRcvd = Message.udpReceive(udpSocket);  // {Message, InetAddress, int}
            Message msg = (Message) udpRcvd[0];
            InetAddress srcAdd = (InetAddress) udpRcvd[1];
            int srcPort = (Integer) udpRcvd[2];
            System.out.println(msg.type);
            System.out.println(srcAdd);
            System.out.println(srcPort);
        }

    }


}
