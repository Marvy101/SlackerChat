import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;

public class SlackerClient {
    private static final String DISCOVER_REQUEST = "DISCOVER_FUIFSERVER_REQUEST";
    private static final String DISCOVER_RESPONSE = "DISCOVER_FUIFSERVER_RESPONSE";
    private static final String CONNECT_REQUEST = "CONNECT_REQUEST";
    private static final String CONNECT_ACCEPT = "CONNECT_ACCEPT";
    private static final String CONNECT_DECLINE = "CONNECT_DECLINE";
    private static final String MESSAGE = "MESSAGE";

    private static JFrame frame;
    private static JTextArea chatArea;
    private static JTextField messageField;
    private static JButton sendButton;
    private static JScrollPane scrollPane;
    private static String username;
    private static DatagramSocket socket;
    private static InetAddress serverIp;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void createAndShowGUI() throws Exception {
        frame = new JFrame("Slacker Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        scrollPane = new JScrollPane(chatArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        bottomPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        bottomPanel.add(sendButton, BorderLayout.EAST);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Prompt for username
        username = JOptionPane.showInputDialog(frame, "Enter your username:");

        serverIp = findServer();
        if (serverIp == null) {
            JOptionPane.showMessageDialog(frame, "Server not found!");
            return;
        }

        socket = new DatagramSocket();

        byte[] connectData = (CONNECT_REQUEST + " " + username).getBytes();
        DatagramPacket connectPacket = new DatagramPacket(connectData, connectData.length, serverIp, 8881);
        socket.send(connectPacket);

        byte[] recvBuf = new byte[15000];
        DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
        socket.receive(receivePacket);

        String message = new String(receivePacket.getData()).trim();

        if (message.startsWith(CONNECT_ACCEPT)) {
            JOptionPane.showMessageDialog(frame, "Connected!");

            Thread messageListenerThread = new Thread(() -> {
                try {
                    while (true) {
                        byte[] recvBuf2 = new byte[15000];
                        DatagramPacket receivePacket2 = new DatagramPacket(recvBuf2, recvBuf2.length);
                        socket.receive(receivePacket2);

                        int dataLength = receivePacket2.getLength();
                        byte[] data = Arrays.copyOf(receivePacket2.getData(), dataLength);
                        String receivedMessage = new String(data).trim();

                        if (receivedMessage.startsWith(MESSAGE)) {
                            String[] parts = receivedMessage.substring(MESSAGE.length()).trim().split(":::", 2);
                            String username_again = parts[0];
                            String content = parts[1];
                            chatArea.append(username_again + ": " + content + "\n");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SlackerClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            messageListenerThread.start();

            sendButton        .addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        sendMessage();
                    } catch (IOException ex) {
                        Logger.getLogger(SlackerClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            messageField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        sendMessage();
                    } catch (IOException ex) {
                        Logger.getLogger(SlackerClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        } else {
            JOptionPane.showMessageDialog(frame, "Connection declined.");
        }
    }

    private static void sendMessage() throws IOException {
        String content = messageField.getText();
        Message msg = new Message(username, content);

        // Serialize the message object as a string
        String serializedMessage = MESSAGE + " " + msg.getUsername() + ":::" + msg.getContent();
        byte[] sendData = serializedMessage.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIp, 8881);
        socket.send(sendPacket);

        // Add the sent message to the chatArea
        chatArea.append(username + ": " + content + "\n");

        messageField.setText("");
    }

    private static InetAddress findServer() {
        try {
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = DISCOVER_REQUEST.getBytes();

            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8881);
                c.send(sendPacket);
            } catch (Exception e) {
            }

            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8881);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }
                }
            }

            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            // remove unnecessary bytes from packets
            int dataLength = receivePacket.getLength();
            byte[] data = Arrays.copyOf(receivePacket.getData(), dataLength);
            String message = new String(data).trim();

            if (message.equals(DISCOVER_RESPONSE)) {
                c.close();
                return receivePacket.getAddress();
            }

            c.close();
        } catch (IOException ex) {
            Logger.getLogger(SlackerClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}


