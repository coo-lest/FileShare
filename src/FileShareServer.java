
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class FileShareServer {

    ServerSocket svrSocket;
    DatagramSocket udpSocket;

    public FileShareServer(int tcpPort, int udpPort) throws IOException {
        System.out.println("Server created");
        svrSocket = new ServerSocket(tcpPort);
        udpSocket = new DatagramSocket(udpPort);
        Socket clSocket = svrSocket.accept();

        // Create listening thread
        Thread listeningThread = new Thread(() -> {
            System.out.printf("Listening at port %d...", tcpPort);
            while (true) {
                try {

                    // Create connection thread
                    Thread connThread = new Thread(() -> {
                        try {
                            // User authentication
                            DataOutputStream dout = new DataOutputStream(clSocket.getOutputStream());
                            DataInputStream din = new DataInputStream((clSocket.getInputStream()));
                            byte[] buffer = new byte[1024];

                            // Request username
                            System.out.println("waiting for username...");
                            String username = FileShare.receiveMsg(din).body;
                            // Request password
                            System.out.println("waiting for password...");

                            FileShare.sendMsg(dout, new Message(MessageType.REQUEST, "Password: "));

                            String password = FileShare.receiveMsg(din).body;
                            // Verify
                            if (verifyUser(username, password)) {
                                // Send success message
                                FileShare.sendMsg(dout, new Message(MessageType.SUCCESS, "Login successful"));
                                // Start serving the client socket
                                serve(clSocket);

                            } else {
                                // Abort the connection
                                FileShare.sendMsg(dout, new Message(MessageType.FAILURE, "Wrong username or password"));
                            }
                        } catch (Exception e) {
                            // System.err.println("connection dropped.");
                            e.printStackTrace();
                        }
                    });
                    connThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        listeningThread.start();

        // Create UDP listening thread
        Thread udpListen = new Thread(() -> {
            while (true) {  // TODO: isDiscoverable
                try {
                    System.out.println("Waiting for UDP connection");
                    Object[] udpRcvd = Message.udpReceive(udpSocket);  // {Message, InetAddress, int}
                    Message msg = (Message) udpRcvd[0];
                    InetAddress srcAdd = (InetAddress) udpRcvd[1];
                    int srcPort = (Integer) udpRcvd[2];

                    if (msg.type == MessageType.DISCOVERY) {
                        Message.udpSend(udpSocket, srcAdd, srcPort, new Message(MessageType.SUCCESS, InetAddress.getLocalHost().getHostName()));
                    }
                } catch (NegativeArraySizeException e) {
                    // Corrupted user datagram received
                    // Ignore
                } catch (StreamCorruptedException e) {
                    // Corrupted user datagram received
                    // Ignore
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        udpListen.start();
    }

    private void serve(Socket clSocket) {
        System.out.printf("Established a connection to host %s:%d\n\n", clSocket.getInetAddress(),
                clSocket.getPort());
        while (true) {
            // Get stream

            // Get request type
            // Respond (call the private functions)
        }
    }

    private void sendList() {

    }

    private void makeDirectory(String file_path) {
        new File(file_path).mkdirs();
    }

    private void sendFile(String file, Socket socket) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];

        while (fis.read(buffer) > 0) {
            dos.write(buffer);
        }

        fis.close();
        dos.close();
    }

    private void receiveFile(String File, Socket clientSock) throws Exception {
        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
        FileOutputStream fos = new FileOutputStream(File);
        byte[] buffer = new byte[4096];

        int filesize = 15123; // Send file size in separate msg
        int read = 0;
        int totalRead = 0;
        int remaining = filesize;
        while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }

        fos.close();
        dis.close();
    }

    private void deleteFile(String file) {
        new File(file).delete();
    }

    private void deleteDirectory(String file) {
        new File(file).delete();
    }

    private void rename(String fname, String newfname) {
        File new_fname = new File(newfname);
        new File(fname).renameTo(new_fname);

    }

    private void detail(String filename) throws IOException {
        File file = new File(filename);
        System.out.println("name : " + file.getName());
        System.out.println("size (bytes) : " + file.length());
        System.out.println("absolute path? : " + file.isAbsolute());
        System.out.println("exists? : " + file.exists());
        System.out.println("hidden? : " + file.isHidden());
        System.out.println("dir? : " + file.isDirectory());
        System.out.println("file? : " + file.isFile());
        System.out.println("modified (timestamp) : " + file.lastModified());
        System.out.println("readable? : " + file.canRead());
        System.out.println("writable? : " + file.canWrite());
        System.out.println("executable? : " + file.canExecute());
        System.out.println("parent : " + file.getParent());
        System.out.println("absolute file : " + file.getAbsoluteFile());
        System.out.println("absolute path : " + file.getAbsolutePath());
        System.out.println("canonical file : " + file.getCanonicalFile());
        System.out.println("canonical path : " + file.getCanonicalPath());
    }

    private boolean verifyUser(String username, String password) {
        System.out.println("Received username: " + username);
        System.out.println("Received password: " + password);

        File authFile = new File("authorized_users");
        if (authFile.isFile()) {
            try {
                FileInputStream fin = new FileInputStream(authFile);
                Scanner fsc = new Scanner(fin);
                String line;
                while (fsc.hasNextLine()) {
                    line = fsc.nextLine().trim();
                    String[] userInfo = line.split(" ", 2);
                    if (userInfo.length != 2) {
                        continue;   // In case of corrupted authorized_user file
                    }
                    if (userInfo[0].equals(username) && userInfo[1].equals(password)) {
                        return true;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
