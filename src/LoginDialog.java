import com.sun.scenario.effect.impl.sw.java.JSWBoxBlurPeer;
import javafx.scene.layout.Pane;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginDialog extends JDialog {
    LoginDialog thisDialog = this;
    FileShare main;  // Pointer to main class
    JList hostList;
    // TextFields
    TextField addressField = new TextField();
    TextField usernameField = new TextField();
    JPasswordField passwordField = new JPasswordField();
    // Labels
    Label addressLabel = new Label("Address:");
    Label usernameLabel = new Label("Username:");
    Label passwordLabel = new Label("Password:");

    public LoginDialog(JFrame frame, String title, boolean modal, FileShare main) {
        super(frame, title, modal);
        this.main = main;
        this.setTitle("Login");
        this.setSize(new Dimension(500, 400));
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Set container
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(1, 2));

        // Display hosts
        Panel hostPanel = new Panel();
        hostPanel.setLayout(new BorderLayout());

        JLabel hlLabel = new JLabel("Online Hosts");
        // Host list
        try {
            main.fileShareClient.discover();  // Fetch list
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        hostList = new JList(main.fileShareClient.hostList.toArray());
        JScrollPane hlPane = new JScrollPane(hostList);
        hostPanel.add(hlLabel, BorderLayout.NORTH);
        hostPanel.add(hlPane, BorderLayout.CENTER);

        // Operation panel
        Panel opPanel = new Panel();
        opPanel.setLayout(new GridLayout(9, 1));


        opPanel.add(addressLabel);
        opPanel.add(addressField);
        opPanel.add(usernameLabel);
        opPanel.add(usernameField);
        opPanel.add(passwordLabel);
        opPanel.add(passwordField);

        // Buttons
        JButton scanBtn = new JButton("Scan");
        JButton loginBtn = new JButton("Login");
        JButton exitBtn = new JButton("Exit");

        opPanel.add(scanBtn);
        opPanel.add(loginBtn);
        opPanel.add(exitBtn);

        container.add(opPanel, BorderLayout.WEST);
        container.add(hostPanel, BorderLayout.EAST);

        // Actions
        scanBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    main.fileShareClient.discover();  // Fetch list
                } catch (IOException ioException) {

                }
                hostList.setListData(main.fileShareClient.hostList.toArray());
                thisDialog.setVisible(true);
            }
        });

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = addressField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                login(address, username, password);
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        hostList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try {
                    addressField.setText(main.fileShareClient.hostList.get(hostList.getSelectedIndex()).address.getHostAddress());
                } catch (Exception ex) {
                    // Ignore
                }
            }

        });
        this.setVisible(true);
    }

    private void login(String address, String username, String password) {
        try {
            Message authRes = main.fileShareClient.login(username, address, password);
            if (authRes.type == MessageType.SUCCESS) {
                JOptionPane.showMessageDialog(null, "Login successful");
                thisDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(null, authRes.body);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Connection error");
        }
    }
}
