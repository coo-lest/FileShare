import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    FileShare main;  // Pointer to main class
    public GUI(FileShare main) {
        this.main = main;
        this.setTitle("FileShare");
        this.setSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        JDialog loginDialog = new LoginDialog(this, "Login", true, main);

    }





}
