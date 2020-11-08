import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileShareClient {
    Socket socket;
    String svrIp;
    boolean isConnected;
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

    }


}
