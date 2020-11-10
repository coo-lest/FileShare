import java.net.InetAddress;

public class Host {
    InetAddress address;
    int port;
    String name;

    public Host(InetAddress address, int port, String name) {
        this.address = address;
        this.port = port;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        Host host = (Host) o;
        return this.address.equals(host.address) &&
                this.port == host.port;
    }

    @Override
    public String toString() {
        return name + "\t" + address.toString() + ":" + port;
    }
}
