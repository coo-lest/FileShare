import javafx.scene.layout.Pane;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    public LoginDialog() {
        this.setTitle("Login");
        this.setSize(new Dimension(400, 300));

        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        Panel hostPanel = new Panel();
        hostPanel.setLayout(new BorderLayout());

        Panel opPanel = new Panel();
        opPanel.setLayout(null);

        container.add(hostPanel, BorderLayout.WEST);
        container.add(opPanel, BorderLayout.EAST);


        this.setVisible(true);
    }
}
