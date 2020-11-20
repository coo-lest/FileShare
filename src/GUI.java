import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    Button goBtn = new Button("Go");
    Button backBtn = new Button("Back");
    TextField rmtPath = new TextField();
    TextField locPath = new TextField();
    JTree rmtFT = new JTree();
    JTree locFT = new JTree();
    ScrollPane rmtScroll = new ScrollPane();
    ScrollPane locScroll = new ScrollPane();
    File locWorkDir = new File(".");


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
        rmtPath.setEditable(false);
        rmtAddBarPanel.add(rmtPath);
        serverPanel.add(rmtAddBarPanel, BorderLayout.NORTH);
        serverPanel.add(rmtScroll);

        // Local panel
        clientPanel.setLayout(new BorderLayout());
        // Address bar
        Panel locAddBarPanel = new Panel();
        locAddBarPanel.setLayout(new GridLayout(1, 3));
        Label locAddBarLabel = new Label("Local Path: ");
        locAddBarPanel.add(locAddBarLabel);
        locPath.setEditable(false);
        locAddBarPanel.add(locPath);
        Panel navPanel = new Panel(new GridLayout(1, 2));
        navPanel.add(goBtn);
        navPanel.add(backBtn);
        locAddBarPanel.add(navPanel);
        clientPanel.add(locAddBarPanel, BorderLayout.NORTH);
        clientPanel.add(locScroll);

        container.add(menuPanel, BorderLayout.NORTH);
        Panel filePanel = new Panel();
        filePanel.setLayout(new GridLayout(1, 2));
        filePanel.add(serverPanel);
        filePanel.add(clientPanel);
        container.add(filePanel, BorderLayout.CENTER);

        // Button actions
        upBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upload();
            }
        });

        downBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                download();
            }
        });

        delBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete();
            }
        });

        rnBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rename();
            }
        });

        detBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detail();
            }
        });

        mkdirBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mkdir();
            }
        });

        refBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });

        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        goBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                go();
            }
        });

        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                back();
            }
        });

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
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode, true);

        JTree tree = new JTree(treeModel);
        return tree;
    }

    static void createChildren(File rootFile, DefaultMutableTreeNode node) {
        File[] files = rootFile.listFiles();
        if (files == null) return;

        for (File file : files) {
            DefaultMutableTreeNode childNode =
                    new DefaultMutableTreeNode(new FileNode(file), file.isDirectory());
            node.add(childNode);
            if (file.isDirectory()) {
                createChildren(file, childNode);
            }
        }
    }

    void loadTrees() {
        locScroll.remove(locFT);
        rmtScroll.remove(rmtFT);
        // File tree
        // Reload local tree
        locFT = buildTree(locWorkDir);
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
                TreeNode[] nodes = null;
                try {
                    nodes = slctNode.getPath();
                } catch (Exception e1) {
                    // Ignore
                }
                // Update path in the textfield
                String filepath = "";
                if (nodes != null) {
                    for (int i = 0; i < nodes.length - 1; i++) {
                        filepath += nodes[i].toString() + File.separator;
                    }
                    if (nodes.length != 0) {
                        filepath += nodes[nodes.length - 1].toString();
                    }
                }

                locPath.setText(filepath);
            }
        });

        rmtFT.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode slctNode = (DefaultMutableTreeNode) ((JTree) e.getSource()).getLastSelectedPathComponent();
                TreeNode[] nodes = null;
                try {
                    nodes = slctNode.getPath();
                } catch (Exception e1) {
                    // Ignore
                }
                // Update path in the textfield
                String filepath = "";
                if (nodes != null) {
                    for (int i = 0; i < nodes.length - 1; i++) {
                        filepath += nodes[i].toString() + File.separator;
                    }
                    if (nodes.length != 0) {
                        filepath += nodes[nodes.length - 1].toString();
                    }
                }

                rmtPath.setText(filepath);
            }
        });

        locScroll.add(locFT, BorderLayout.CENTER);
        rmtScroll.add(rmtFT, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void upload() {
        String path = relativePath(rmtPath.getText());
        try {
            System.out.println(locWorkDir.getCanonicalPath() + relativePath(locPath.getText()));
            main.fileShareClient.upload(locWorkDir.getCanonicalPath() + relativePath(locPath.getText()), path);
            loadTrees();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    void download() {
        String path = relativePath(rmtPath.getText());
        try {
            main.fileShareClient.download(path, locWorkDir.getCanonicalPath() + relativePath(locPath.getText()));
            loadTrees();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    void delete() {
        String path = relativePath(rmtPath.getText());
        try {
            main.fileShareClient.delete(path);
            main.fileShareClient.rmdir(path);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        loadTrees();
    }

    void rename() {
        JDialog renameDialog = new JDialog(this, "Rename");
        renameDialog.setLocationRelativeTo(this);
        renameDialog.setSize(new Dimension(200, 100));
        renameDialog.setLayout(new GridLayout(3, 1));
        TextField tf = new TextField();
        Label lb = new Label("New name:");
        Button btn = new Button("Confirm");
        renameDialog.add(lb);
        renameDialog.add(tf);
        renameDialog.add(btn);
        renameDialog.setVisible(true);

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String path = relativePath(rmtPath.getText());
                    Message res = main.fileShareClient.rename(path, new File(path).getParent() + File.separator + tf.getText());
                    if (res.type != MessageType.SUCCESS) {
                        JOptionPane.showMessageDialog(null, res.body);
                        renameDialog.dispose();
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                renameDialog.dispose();
                loadTrees();
            }
        });
    }

    void detail() {
        String path = relativePath(rmtPath.getText());
        try {
            String detail = main.fileShareClient.detail(path);
            JOptionPane.showMessageDialog(null, detail);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }

    }

    void mkdir() {
        JDialog dirNameDialog = new JDialog(this, "Folder name");
        dirNameDialog.setLocationRelativeTo(this);
        dirNameDialog.setSize(new Dimension(200, 100));
        dirNameDialog.setLayout(new GridLayout(3, 1));
        TextField tf = new TextField();
        Label lb = new Label("Folder name: ");
        Button btn = new Button("Confirm");
        dirNameDialog.add(lb);
        dirNameDialog.add(tf);
        dirNameDialog.add(btn);
        dirNameDialog.setVisible(true);
        tf.setText("New Folder");

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String path = relativePath(rmtPath.getText());
                    main.fileShareClient.mkdir(path + File.separator + tf.getText());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
                dirNameDialog.dispose();
                loadTrees();
            }
        });
    }

    void refresh() {
        loadTrees();
    }

    void logout() {
        try {
            main.fileShareClient.socket.close();
        } catch (Exception e) {
            // Ignore
        }
        main.fileShareClient.isConnected = false;
        JDialog loginDialog = new LoginDialog(this, "Login", true, main);
        refresh();
    }

    void go() {
        locWorkDir = new File(locPath.getText());
        loadTrees();
    }

    void back() {
        locWorkDir = new File("..");
        loadTrees();
    }

    private String relativePath(String str) {
        String root = str.split("\\\\")[0];
        return str.replaceFirst(root, ".");
    }
}
