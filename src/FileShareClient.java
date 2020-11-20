import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class FileShareClient {
    Socket socket;
    DatagramSocket udpSocket;
    String svrIp;
    boolean isConnected;
    int svrPort = 9999;
    int udpSvrPort = 9998;
    DataInputStream din;
    DataOutputStream dout;
    List<Host> hostList = new LinkedList<>();

    public FileShareClient() throws IOException {
        System.out.println("Client created");

//        Thread dinThread = new Thread(() -> {
//            while (true) {
//                byte[] buffer = new byte[1024];
//                try {
//                    din.read(buffer);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.print(new String(buffer));
//            }
//        });
//        dinThread.start();

    }

    Message login(String username, String address, String password) throws IOException {
        svrIp = address;
        socket = new Socket(svrIp, svrPort);
        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());

        FileShare.sendMsg(dout, new Message(MessageType.DATA, username));
        // Wait for request for password
        System.out.print(FileShare.receiveMsg(din).body);

        // Get and send password
        FileShare.sendMsg(dout, new Message(MessageType.DATA, password));

        // Wait for authentication
        Message authRes = FileShare.receiveMsg(din);

        return authRes;

    }

    void login(String username, String address) {
        try {
            svrIp = address;
            socket = new Socket(svrIp, svrPort);
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            FileShare.sendMsg(dout, new Message(MessageType.DATA, username));
            // Wait for request for password
            System.out.print(FileShare.receiveMsg(din).body);

            // Get and send password
            String password = FileShare.scanner.nextLine();
            FileShare.sendMsg(dout, new Message(MessageType.DATA, password));

            // Wait for authentication
            Message authRes = FileShare.receiveMsg(din);

            System.out.println(authRes.body);
            if (authRes.type == MessageType.SUCCESS) {
                isConnected = true;
            } else {
                isConnected = false;
                socket.close();
            }
            System.out.println(isConnected);
        } catch (IOException e) {
            System.err.println("Connection error");
        }

    }

    void discover() throws IOException {
        hostList.clear();
        // Listening response
        udpSocket = new DatagramSocket();
        Thread recResThread = new Thread(() -> {
            DatagramPacket resPacket = new DatagramPacket(new byte[1024], 1024);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Object[] udpRcvd = Message.udpReceive(udpSocket);
                    Message msg = (Message) udpRcvd[0];
                    InetAddress srcAdd = (InetAddress) udpRcvd[1];
                    int srcPort = (Integer) udpRcvd[2];

                    if (msg.type == MessageType.SUCCESS) {
                        Host host = new Host(srcAdd, srcPort, msg.body);
                        // Add new host to hostList
                        if (!hostList.contains(host)) {
                            System.out.println(host);
                            hostList.add(host);
                        }
                    }
                } catch (StreamCorruptedException sce) {
                    // Corrupt user datagram received
                    // Ignore
                } catch (NegativeArraySizeException nase) {
                    // Corrupt user datagram received
                    // Ignore
                } catch (SocketException e) {
                    // Socket closed
                    // Normal
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        recResThread.start();

        // Send discovery packet
        System.out.println("Scanning online hosts...");
        int count = 0;
        while (count++ < 5) {
            try {
                Message discMsg = new Message(MessageType.DISCOVERY, "");
                Message.udpSend(udpSocket, InetAddress.getByName("255.255.255.255"), udpSvrPort, discMsg);
                Thread.sleep(new Random().nextInt(100) + 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        recResThread.interrupt();
        udpSocket.close();
        System.out.println("Finished scanning");
    }

    void download(String filename, String savePath) throws IOException {
        if (!isConnected) {
            System.out.println("Not connected to any host. Please login.");
            return;
        }
        // Send DOWNLOAD request
        FileShare.sendMsg(dout, new Message(MessageType.DOWNLOAD, filename));
        // Receive reply from server
        Message reply = FileShare.receiveMsg(din);
        // If transmission can be started
        if (reply.type == MessageType.SUCCESS) {
            // Create directories and file
            File f = new File(savePath, new File(filename).getName());
            File dir = f.getParentFile();
            dir.mkdirs();
            // Start transmission
            FileOutputStream fout = new FileOutputStream(f);
            byte[] buffer = new byte[1024];
            long fSize = din.readLong();
            while (fSize > 0) {
                int read = din.read(buffer);
                fout.write(buffer, 0, read);
                fSize -= read;
            }
            fout.close();
        } else {
            System.out.println(reply.body);
        }
    }

    void upload(String filename, String uploadPath) throws IOException {
        if (!isConnected) {
            System.out.println("Not connected to any host. Please login.");
            return;
        }

        // Check local file
        File f = new File(filename);
        System.out.println("FILENAME: " + f.getName());
        if (!f.exists()) {
            System.out.println("File not exists");
            return;
        } else if (f.isDirectory()) {
            System.out.println(filename + " is a directory");
            return;
        }

        // Send UPLOAD request
        FileShare.sendMsg(dout, new Message(MessageType.UPLOAD, uploadPath + "/" + f.getName()));

        // Receive reply from server
        Message reply = FileShare.receiveMsg(din);
        if (reply.type == MessageType.SUCCESS) {
            // Start transmission
            FileInputStream fin = new FileInputStream(f);
            byte[] buffer = new byte[1024];
            // Transmit fSize
            long fSize = f.length();
            dout.writeLong(fSize);
            // Transmit file content
            while (fSize > 0) {
                int read = fin.read(buffer);
                dout.write(buffer, 0, read);
                fSize -= read;
            }
        } else {
            System.out.println(reply.body);
        }
    }

    void mkdir(String dirName) throws IOException {
        FileShare.sendMsg(dout, new Message(MessageType.MKDIR, dirName));
        Message res = FileShare.receiveMsg(din);
        if (res.type != MessageType.SUCCESS) {
            System.out.println(res.body);
        }
    }

    String detail(String filename) throws IOException {
        FileShare.sendMsg(dout, new Message(MessageType.DETAIL, filename));
        Message res = FileShare.receiveMsg(din);
        if (res.type == MessageType.SUCCESS) {
            System.out.println(res.body);
        } else {
            System.out.println(res.body);
        }
        return res.body;
    }

    Message rename(String oldName, String newName) throws IOException {
        FileShare.sendMsg(dout, new Message(MessageType.RENAME, oldName + "\\?" + newName));
        Message res = FileShare.receiveMsg(din);
        if (res.type != MessageType.SUCCESS) {
            System.out.println(res.body);
        }
        return res;
    }

    void delete(String filename) throws IOException {
        FileShare.sendMsg(dout, new Message(MessageType.DELETE, filename));
        Message res = FileShare.receiveMsg(din);
        if (res.type != MessageType.SUCCESS) {
            System.out.println(res.body);
        }
    }

    void rmdir(String dirName) throws IOException {
        FileShare.sendMsg(dout, new Message(MessageType.RMDIR, dirName));
        Message res = FileShare.receiveMsg(din);
        if (res.type != MessageType.SUCCESS) {
            System.out.println(res.body);
        }
    }

    void cd(String dirName) throws IOException {
        FileShare.sendMsg(dout, new Message(MessageType.CD, dirName));
        Message res = FileShare.receiveMsg(din);
        if (res.type != MessageType.SUCCESS) {
            System.out.println(res.body);
        }

    }
}
