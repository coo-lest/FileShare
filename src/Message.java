import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {
    MessageType type;
    String body;

    public Message(MessageType type, String body) {
        this.type = type;
        this.body = body;
    }

    public Message(byte[] msgBytes) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(msgBytes);
            ObjectInputStream is = new ObjectInputStream(in);
            Message msg = (Message) is.readObject();
            this.type = msg.type;
            this.body = msg.body;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(this);
        out.flush();
        return out.toByteArray();
    }
}

/*
 List of msgNos and corresponding meaning
    0: (String) Success
    1: (String) Fail
    ...

 */
