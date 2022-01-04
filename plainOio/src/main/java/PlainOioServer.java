import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class PlainOioServer {
    public void serve(int port) throws Exception {
        final ServerSocket socket = new ServerSocket(port);
        try {
            while (true) {
                Socket accept = socket.accept();
                System.out.println("Accepted connection from " + socket);
                new Thread(() -> {
                    OutputStream out;
                    try {
                        out = accept.getOutputStream();
                        out.write("Hi!\r\n".getBytes(StandardCharsets.UTF_8));
                        accept.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException ignored) {

                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
