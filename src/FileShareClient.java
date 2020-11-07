import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class FileShareClient {
    Socket socket;
    String svrIp = "localhost";
    int svrPort = 9999;

    public FileShareClient() throws IOException {
        System.out.println("Client created");
        socket = new Socket(svrIp, svrPort);
        System.out.println("Connection established");
        DataInputStream din = new DataInputStream(socket.getInputStream());
        while (true) {
            byte[] buffer = new byte[1024];
            din.read(buffer);
            System.out.println(new String(buffer));
        }

    }

    void login(String username, String password) {

    }
}
