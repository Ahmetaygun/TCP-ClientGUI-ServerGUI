import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCPClientGUI {
    private Socket socket = null;
    private PrintWriter out;
    private Scanner input;
    private JTextArea chatArea;
    private JTextField messageField;

    public TCPClientGUI() {
        JFrame frame = new JFrame("TCP Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        messagePanel.setBackground(Color.LIGHT_GRAY);
        messageField = new JTextField();
        messageField.setBackground(Color.WHITE);
        messagePanel.add(messageField, BorderLayout.CENTER);

        // Enter
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(Color.GREEN);
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        messagePanel.add(sendButton, BorderLayout.EAST);
        panel.add(messagePanel, BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            input = new Scanner(socket.getInputStream());

            Thread receiveThread = new Thread(this::receiveMessage);
            receiveThread.start();
        } catch (IOException ex) {
            chatArea.append("Could not connect to server: " + ex.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        out.println(message);
        messageField.setText("");
    }

    private void receiveMessage() {
        while (true) {
            if (input.hasNextLine()) {
                String message = input.nextLine();
                chatArea.append("Server: " + message + "\n");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TCPClientGUI();
            }
        });
    }
}
