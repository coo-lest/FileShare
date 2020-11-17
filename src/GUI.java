import javafx.scene.layout.Pane;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    FileShare main;  // Pointer to main class
    Container container;
    Panel menuPanel = new Panel();
    Panel serverPanel = new Panel();
    Panel clientPanel = new Panel();
    Button downBtn = new Button("Download");
    Button upBtn = new Button("Upload");
    Button delBtn = new Button("Delete");
    Button rnBtn = new Button("Rename");
    Button detBtn = new Button("Detail");
    Button mkdirBtn = new Button("New Folder");
    Button refBtn = new Button("Refresh");
    Button logoutBtn = new Button("Logout");
    TextField rmtPath = new TextField();
    TextField locPath = new TextField();


    public GUI(FileShare main) {
        this.main = main;
        this.setTitle("FileShare");
        this.setSize(new Dimension(800, 600));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        container = this.getContentPane();
        container.setLayout(new BorderLayout());

        menuPanel.setLayout(new GridLayout(2, 4));
        menuPanel.add(upBtn);
        menuPanel.add(downBtn);
        menuPanel.add(delBtn);
        menuPanel.add(rnBtn);
        menuPanel.add(detBtn);
        menuPanel.add(mkdirBtn);
        menuPanel.add(refBtn);
        menuPanel.add(logoutBtn);

        serverPanel.setLayout(new BorderLayout());
        // Address bar
        Panel rmtAddBarPanel = new Panel();
        rmtAddBarPanel.setLayout(new GridLayout(1, 2));
        Label rmtAddBarLabel = new Label("Remote Path: ");
        rmtAddBarPanel.add(rmtAddBarLabel);
        rmtAddBarPanel.add(rmtPath);
        serverPanel.add(rmtAddBarPanel, BorderLayout.NORTH);
        // File tree

        clientPanel.setLayout(new BorderLayout());
        // Address bar
        Panel locAddBarPanel = new Panel();
        locAddBarPanel.setLayout(new GridLayout(1, 2));
        Label locAddBarLabel = new Label("Local Path: ");
        locAddBarPanel.add(locAddBarLabel);
        locAddBarPanel.add(locPath);
        clientPanel.add(locAddBarPanel, BorderLayout.NORTH);
        // File tree

        container.add(menuPanel, BorderLayout.NORTH);
        Panel filePanel = new Panel();
        filePanel.setLayout(new GridLayout(1, 2));
        filePanel.add(serverPanel);
        filePanel.add(clientPanel);
        container.add(filePanel, BorderLayout.CENTER);

        this.setVisible(true);
        JDialog loginDialog = new LoginDialog(this, "Login", true, main);
    }





}
