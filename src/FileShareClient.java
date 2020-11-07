import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class FileShareClient {
    Socket socket;
    String svrIp;
    int svrPort;

    public FileShareClient() throws IOException {
        socket = new Socket(svrIp, svrPort);
    }

}
