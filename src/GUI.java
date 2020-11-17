import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

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
    JTree rmtFT = new JTree();
    JTree locFT = new JTree();


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

        // Remote panel
        serverPanel.setLayout(new BorderLayout());
        // Address bar
        Panel rmtAddBarPanel = new Panel();
        rmtAddBarPanel.setLayout(new GridLayout(1, 2));
        Label rmtAddBarLabel = new Label("Remote Path: ");
        rmtAddBarPanel.add(rmtAddBarLabel);
        rmtAddBarPanel.add(rmtPath);
        serverPanel.add(rmtAddBarPanel, BorderLayout.NORTH);

        // Local panel
        clientPanel.setLayout(new BorderLayout());
        // Address bar
        Panel locAddBarPanel = new Panel();
        locAddBarPanel.setLayout(new GridLayout(1, 2));
        Label locAddBarLabel = new Label("Local Path: ");
        locAddBarPanel.add(locAddBarLabel);
        locAddBarPanel.add(locPath);
        clientPanel.add(locAddBarPanel, BorderLayout.NORTH);

        container.add(menuPanel, BorderLayout.NORTH);
        Panel filePanel = new Panel();
        filePanel.setLayout(new GridLayout(1, 2));
        filePanel.add(serverPanel);
        filePanel.add(clientPanel);
        container.add(filePanel, BorderLayout.CENTER);


        this.setVisible(true);
        JDialog loginDialog = new LoginDialog(this, "Login", true, main);
        loadTrees();

    }


    /*
      Returns a JTree representing the file structure
     */
    static JTree buildTree(File rootFile) {
        FileNode rootNode = new FileNode(rootFile);
        createChildren(rootFile, rootNode);
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        JTree tree = new JTree(treeModel);
        return tree;
    }

    static void createChildren(File rootFile, DefaultMutableTreeNode node) {
        File[] files = rootFile.listFiles();
        if (files == null) return;

        for (File file : files) {
            DefaultMutableTreeNode childNode =
                    new DefaultMutableTreeNode(new FileNode(file));
            node.add(childNode);
            if (file.isDirectory()) {
                createChildren(file, childNode);
            }
        }
    }

    void loadTrees() {
        // File tree
        // Reload local tree
        locFT = buildTree(new File("."));  // TODO: set download path
        // Fetch remote tree
        try {
            FileShare.sendMsg(main.fileShareClient.dout, new Message(MessageType.TREE, ""));
            Message reply = FileShare.receiveMsg(main.fileShareClient.din);
            if (reply.type == MessageType.SUCCESS) {
                // Transmitting JTree object
                ObjectInputStream ois = new ObjectInputStream(main.fileShareClient.din);
                rmtFT = (JTree) ois.readObject();
            } else {
                JOptionPane.showMessageDialog(null, reply.body);
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

        // Actions
        locFT.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode slctNode = (DefaultMutableTreeNode) ((JTree) e.getSource()).getLastSelectedPathComponent();
                TreeNode[] nodes = slctNode.getPath();
                String filepath = "";
                for (int i = 0; i < nodes.length - 1; i++) {
                    filepath += nodes[i].toString() + File.separator;
                }
                if (nodes.length != 0) {
                    filepath += nodes[nodes.length - 1].toString();
                }

                locPath.setText(filepath);
            }
        });

        rmtFT.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode slctNode = (DefaultMutableTreeNode) ((JTree) e.getSource()).getLastSelectedPathComponent();
                TreeNode[] nodes = slctNode.getPath();
                String filepath = "";
                for (int i = 0; i < nodes.length - 1; i++) {
                    filepath += nodes[i].toString() + File.separator;
                }
                if (nodes.length != 0) {
                    filepath += nodes[nodes.length - 1].toString();
                }

                rmtPath.setText(filepath);
            }
        });

        clientPanel.add(locFT, BorderLayout.CENTER);
        serverPanel.add(rmtFT, BorderLayout.CENTER);
        this.setVisible(true);
    }

}
