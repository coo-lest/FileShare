import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileShareClient {
    Socket socket;
    String svrIp;
    int svrPort = 9999;
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

    void login(String username, String address) throws IOException {
        svrIp = address;
        socket = new Socket(svrIp, svrPort);
        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());

        sendMsg(dout, username);

        System.out.println("message sent");
        // Wait for request for password
        System.out.print(receiveMsg(din));

        // Get and send password
        String password = FileShare.scanner.nextLine();
        sendMsg(dout, password);


    }

    private String receiveMsg(DataInputStream din) throws IOException {
        String msg = "";

        int msgLen = din.readInt();
        byte[] buffer = new byte[1024];
        while (msgLen > 0) {
            int read = din.read(buffer, 0, msgLen);
            msg += new String(buffer);
            msgLen -= read;
        }

        return msg;
    }

    private void sendMsg(DataOutputStream dout, String msg) throws IOException {
        dout.writeInt(msg.length());
        dout.writeBytes(msg);
    }
}
