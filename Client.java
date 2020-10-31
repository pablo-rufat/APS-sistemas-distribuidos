import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Client extends JFrame implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;
    private JTextArea text;
    private JTextField txtMsg;
    private JButton btnSend;
    private JButton btnExit;
    private JLabel lblHistory;
    private JLabel lblMsg;
    private JPanel pnlContent;
    private Socket socket;
    private OutputStream output;
    private Writer outputWriter;
    private BufferedWriter bufferWriter;
    private JTextField txtIP;
    private JTextField txtPort;
    private JTextField txtName;

    public Client() throws IOException{
        JLabel lblMessage = new JLabel("Verify!");
        txtIP = new JTextField("127.0.0.1");
        txtPort = new JTextField("8080");
        txtName = new JTextField("Client");
        Object[] texts = {lblMessage, txtIP, txtPort, txtName };
        JOptionPane.showMessageDialog(null, texts);

        pnlContent = new JPanel();
        text = new JTextArea(10,20);
        text.setEditable(false);
        text.setBackground(new Color(240,240,240));

        txtMsg = new JTextField(20);
        lblHistory = new JLabel("History");
        lblMsg = new JLabel("Message");
        btnSend = new JButton("Send");
        btnSend.setToolTipText("Send Message");
        btnExit = new JButton("Exit");

        btnExit.setToolTipText("Exit Chat");
        btnSend.addActionListener(this);
        btnExit.addActionListener(this);
        btnSend.addKeyListener(this);
        txtMsg.addKeyListener(this);

        JScrollPane scroll = new JScrollPane(text);

        text.setLineWrap(true);
        pnlContent.add(lblHistory);
        pnlContent.add(scroll);
        pnlContent.add(lblMsg);
        pnlContent.add(txtMsg);
        pnlContent.add(btnExit);
        pnlContent.add(btnSend);
        pnlContent.setBackground(Color.LIGHT_GRAY);

        text.setBorder(BorderFactory.createEtchedBorder(Color.BLUE,Color.BLUE));
        txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));

        setTitle(txtName.getText());
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(250,300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void connect() throws IOException{
        socket = new Socket(txtIP.getText(),Integer.parseInt(txtPort.getText()));
        output = socket.getOutputStream();
        outputWriter = new OutputStreamWriter(output);
        bufferWriter = new BufferedWriter(outputWriter);
        bufferWriter.write(txtName.getText()+"\r\n");
        bufferWriter.flush();
    }

    public void sendMessage(String msg) throws IOException{
        if(msg.equals("Exit")){
            bufferWriter.write(txtName.getText() + " disconnected \r\n");
            text.append(txtName.getText() + " disconnected \r\n");
        }else{
            bufferWriter.write(msg + "\r\n");
            text.append( " You said: " + txtMsg.getText() + "\r\n");
        }
        bufferWriter.flush();
        txtMsg.setText("");
    }

    public void listen() throws IOException{

        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";

        while(!"Exit".equalsIgnoreCase(msg))

        if(bfr.ready()){
            msg = bfr.readLine();
            if(msg.equals("Exit"))
                text.append("Server down! \r\n");
            else
                text.append(msg + "\r\n");
        }
    }

    public void exit() throws IOException{
        sendMessage("Exit");
        bufferWriter.close();
        outputWriter.close();
        output.close();
        socket.close();

        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(e.getActionCommand().equals(btnSend.getActionCommand()))
                sendMessage(txtMsg.getText());
            else
                if(e.getActionCommand().equals(btnExit.getActionCommand()))
                exit();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            try {
                sendMessage(txtMsg.getText());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {}

    @Override
    public void keyTyped(KeyEvent arg0) {}

    public static void main(String []args) throws IOException{
        Client app = new Client();
        app.connect();
        app.listen();
    }

}