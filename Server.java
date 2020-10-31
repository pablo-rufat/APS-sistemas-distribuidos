import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Server extends Thread {
    private static ArrayList<BufferedWriter> clients;
    private static ServerSocket server;
    private String name;
    private Socket connection;
    private InputStream input;
    private InputStreamReader inputReader;
    private BufferedReader bufferReader;

    public Server(Socket connection) {
        this.connection = connection;
        try {
            input = connection.getInputStream();
            inputReader = new InputStreamReader(input);
            bufferReader = new BufferedReader(inputReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try{
            String message;
            OutputStream output = this.connection.getOutputStream();
            Writer writer = new OutputStreamWriter(output);
            BufferedWriter bufferWriter = new BufferedWriter(writer);

            clients.add(bufferWriter);

            name = message = bufferReader.readLine();

            while(!"exit".equalsIgnoreCase(message) && message != null) {
                    message = bufferReader.readLine();
                    sendToAll(bufferWriter, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(BufferedWriter output, String message) throws IOException {
        BufferedWriter newWriter;

        for(BufferedWriter buffer : clients) {
            newWriter = (BufferedWriter)buffer;
            if (!(output == newWriter)) {
                buffer.write(name + " says: " + message + "\r\n");
                buffer.flush();
            }
        }
    }

    public static void main(String []args) {
        try {
            server = new ServerSocket(Integer.parseInt(args[0]));
            clients = new ArrayList<BufferedWriter>();
            System.out.println("Server active at port " + args[0]);

            while(true){
                System.out.println("Waiting connection...");
                Socket con = server.accept();
                System.out.println("Client connected...");
                Thread t = new Server(con);
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}