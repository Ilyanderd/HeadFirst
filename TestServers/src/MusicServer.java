import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class MusicServer {
    ArrayList<ObjectOutputStream> clientOutputStreams;

    public static void main(String[] args) {
        new MusicServer().go();
    }

    public class ClientHandler implements Runnable {
        ObjectInputStream inputStream;
        Socket clientSocket;

        public ClientHandler(Socket socket) {
            try {
                clientSocket = socket;
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void run() {
            Object o1 = null;
            Object o2 = null;

            try {
                while ((o1 = inputStream.readObject()) != null) {
                    o2 = inputStream.readObject();

                    System.out.println("read two objects");
                    tellEveryone(o1,o2);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }

    public void go() {
        clientOutputStreams = new ArrayList<ObjectOutputStream>();

        try {
            ServerSocket serverSock = new ServerSocket(4242);

            while (true) {
                Socket clientSocket = serverSock.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(outputStream);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();

                System.out.println("got a connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tellEveryone(Object one, Object two) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {
                ObjectOutputStream outputStream = (ObjectOutputStream) it.next();
                outputStream.writeObject(one);
                outputStream.writeObject(two);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}