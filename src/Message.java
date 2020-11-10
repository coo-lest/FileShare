import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Message implements Serializable {
    MessageType type;
    String body;

    public Message(MessageType type, String body) {
        this.type = type;
        this.body = body;
    }

    public Message(byte[] msgBytes) throws IOException {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(msgBytes);
            ObjectInputStream is = new ObjectInputStream(in);
            Message msg = (Message) is.readObject();
            this.type = msg.type;
            this.body = msg.body;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(this);
            out.flush();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void udpSend(DatagramSocket udpSocket, InetAddress svrAdd, int svrPort, Message msg) throws IOException {
        byte[] msgBytes = msg.getBytes();

        // Convert msg size (int) to byte[]
        ByteBuffer bbf = ByteBuffer.allocate(Integer.BYTES);
        bbf.putInt(msgBytes.length);
        byte[] msgSizeBytes = bbf.array();

        // Send size packet
        DatagramPacket sizePacket = new DatagramPacket(msgSizeBytes, msgSizeBytes.length,
                svrAdd, svrPort);
        udpSocket.send(sizePacket);

        // Send msg packet
        DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length,
                svrAdd, svrPort);
        udpSocket.send(msgPacket);
    }

    public static Object[] udpReceive(DatagramSocket udpSocket) throws IOException, NegativeArraySizeException {
        // Get size packet
        DatagramPacket sizePacket = new DatagramPacket(new byte[Integer.BYTES], Integer.BYTES);
        udpSocket.receive(sizePacket);
        byte[] msgSizeBytes = sizePacket.getData();

        // Convert msg size (byte[]) to int
        ByteBuffer bbf = ByteBuffer.wrap(msgSizeBytes);
        int msgSize = bbf.getInt();

        // Get msg packet
        DatagramPacket msgPacket = new DatagramPacket(new byte[msgSize], msgSize);
        udpSocket.receive((msgPacket));
        byte[] msgBytes = msgPacket.getData();

        return new Object[]{new Message(msgBytes), msgPacket.getAddress(), msgPacket.getPort()};    // {Message, InetAddress, int}
    }
}

/*
 List of msgNos and corresponding meaning
    0: (String) Success
    1: (String) Fail
    ...

 */
