package sandbox;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCopy {
    public static void main(String[] args) throws IOException {
        String home = System.getProperty("user.home");

        FileInputStream in = new FileInputStream(home + "/.bashrc");
        FileOutputStream out = new FileOutputStream(home + "/backup.txt");

        byte[] buffer = new byte[256];
        int n;
        while ((n = in.read(buffer)) != -1) {
            System.out.println(n + " bytes read");
            out.write(buffer, 0, n);
            System.out.println(n + " bytes written");
        }

        out.close();
        in.close();
    }
}
