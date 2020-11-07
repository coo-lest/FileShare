import java.io.IOException;

public class FileShare {
    final int SVR_TCP = 9999;
    FileShareServer fileShareServer;
    FileShareClient fileShareClient;

    public FileShare() throws IOException {
        fileShareServer = new FileShareServer(SVR_TCP);
        fileShareClient = new FileShareClient();
    }

    void cli() {
        // cli goes here
    }

    public static void main(String[] args) throws Exception{
        FileShare fileShare = new FileShare();

    }
}
