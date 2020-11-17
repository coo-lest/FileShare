import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

public class FileNode extends DefaultMutableTreeNode {

    File file;

    public FileNode(File file) {
        super(file.getName());
        this.file = file;
    }

    @Override
    public String toString() {
        String name = file.getName();
        if (name.equals("")) {
            return file.getAbsolutePath();
        } else {
            return name;
        }
    }
}


