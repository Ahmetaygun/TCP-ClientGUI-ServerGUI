import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerUI extends JFrame {

    private JTextArea statusTextArea;
    private JButton startStopButton;

    private ServerSocket serverSocket;
    private boolean isRunning = false;

    private static final int PORT = 1234;

    public TCPServerUI() {
        setTitle("TCP Sunucu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        statusTextArea = new JTextArea();
        statusTextArea.setEditable(false);

        startStopButton = new JButton("Başlat");
        startStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isRunning) {
                    startServer();
                    startStopButton.setText("Durdur");
                } else {
                    stopServer();
                    startStopButton.setText("Başlat");
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startStopButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(statusTextArea, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            appendStatus("Sunucu TCP Socket oluşturuldu. Bağlantı bekleniyor...");
            isRunning = true;

            Thread serverThread = new Thread(new Runnable() {
                public void run() {
                    while (isRunning) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            appendStatus(clientSocket.toString() + " bağlandı.");
                            handleClient(clientSocket);
                        } catch (IOException ex) {
                            if (isRunning) {
                                appendStatus("Bağlantı hatası: " + ex.getMessage());
                            }
                        }
                    }
                }
            });
            serverThread.start();
        } catch (IOException ex) {
            appendStatus("Bağlantı hatası: " + ex.getMessage());
        }
    }

    private void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                appendStatus("Sunucu durduruldu.");
            }
        } catch (IOException ex) {
            appendStatus("Bağlantı hatası: " + ex.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            while (isRunning) {
                String receivedMessage = readMessage(clientSocket);
                if (receivedMessage.equalsIgnoreCase("exit")) {
                    break;
                }
                appendStatus( " istemci: " + receivedMessage);
                sendMessage(clientSocket, receivedMessage.toUpperCase());
            }
            clientSocket.close();
            appendStatus(clientSocket.toString() + " bağlantısı kapatıldı.");
        } catch (IOException ex) {
            appendStatus("Istemci baglantisi kesildi: " + ex.getMessage());
        }
    }

    private String readMessage(Socket clientSocket) throws IOException {
        return new java.util.Scanner(clientSocket.getInputStream()).nextLine();
    }

    private void sendMessage(Socket clientSocket, String message) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println(message);
    }

    private void appendStatus(String message) {
        statusTextArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TCPServerUI().setVisible(true);
            }
        });
    }
}
