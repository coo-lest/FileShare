import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class FileShare {
    final int SVR_TCP = 9999;
    FileShareServer fileShareServer;
    FileShareClient fileShareClient;
    static Scanner scanner;

    public FileShare() throws IOException {
        fileShareServer = new FileShareServer(SVR_TCP);
        fileShareClient = new FileShareClient();
        scanner = new Scanner(System.in);
    }

    void cli() throws IOException {
        while (true) {
            // Read command
            System.out.print("> ");
            String[] cmd = scanner.nextLine().trim().split("\\s+");

            switch (cmd[0]) {
                case "login":
                    if (cmd.length != 2) {
                        System.out.println("Invalid number of arguments");
                    }

                    String[] loginInfo = cmd[1].split("@");  // cmd[1] format username@address
                    if (loginInfo.length != 2) {
                        System.out.println("Invalid argument \"" + cmd[1] + "\"");
                        System.out.println("Usage: login username@address");
                    }
                    login(loginInfo[0], loginInfo[1]);
                    break;


                default:
                    System.out.println("Unknown command");
                    break;
            }
        }
    }

    private void login(String username, String address) throws IOException {
        fileShareClient.login(username, address);
    }



    static String receiveMsg(DataInputStream din) throws IOException {
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

    static void sendMsg(DataOutputStream dout, String msg) throws IOException {
        dout.writeInt(msg.length());
        dout.writeBytes(msg);
    }

    public static void main(String[] args) throws Exception{
        FileShare fileShare = new FileShare();
        fileShare.cli();
    }
}
