import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileShare {
    final static int SVR_TCP = 9999;
    final static int SVR_UDP = 9998;
    FileShareServer fileShareServer;
    FileShareClient fileShareClient;
    static Scanner scanner;

    public FileShare() throws IOException {
        File rootConfig = new File("share_root");
        if (!rootConfig.isFile()) {
            System.out.println("Missing share_root file");
        }
        Scanner fs = new Scanner(rootConfig);
        String root = fs.nextLine();
        fileShareServer = new FileShareServer(SVR_TCP, SVR_UDP, root, this);
        fileShareClient = new FileShareClient();
        scanner = new Scanner(System.in);
    }

    void gui() {
        new GUI(this);
    }

    private void login(String username, String address) throws IOException {
        fileShareClient.login(username, address);
    }

    private void discover() throws IOException {
        fileShareClient.discover();
    }


    public static void main(String[] args) throws Exception {
        FileShare fileShare = new FileShare();
        fileShare.gui();
    }
}
