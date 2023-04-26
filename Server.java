import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static final String DISCOVER_REQUEST = "DISCOVER_FUIFSERVER_REQUEST";
    private static final String DISCOVER_RESPONSE = "DISCOVER_FUIFSERVER_RESPONSE";
    private static final String CONNECT_REQUEST = "CONNECT_REQUEST";
    private static final String CONNECT_ACCEPT = "CONNECT_ACCEPT";
    private static final String CONNECT_DECLINE = "CONNECT_DECLINE";
    private static final String MESSAGE = "MESSAGE";

    private List<ClientInfo> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        Thread discoveryThread = new Thread(new Server());
        discoveryThread.start();
    }

    @Override
    public void run() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(8881, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");
            while (true) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                int dataLength = packet.getLength();
                byte[] data = Arrays.copyOf(packet.getData(), dataLength);
                String message = new String(data).trim();

                if (message.startsWith(DISCOVER_REQUEST)) {
                    byte[] sendData = DISCOVER_RESPONSE.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                    System.out.println("Connected");
                } else if (message.startsWith(CONNECT_REQUEST)) {
                    String newClientName = message.substring(CONNECT_REQUEST.length()).trim();
                    ClientInfo newClient = new ClientInfo(newClientName, packet.getAddress(), packet.getPort());
                    clients.add(newClient);

                    byte[] sendData = (CONNECT_ACCEPT + " " + newClientName).getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);
                    System.out.println("User '" + newClientName + "' connected.");

                } else if (message.startsWith(MESSAGE)) {
                    String[] parts = message.substring(MESSAGE.length()).trim().split(":::", 2);
                    String username = parts[0];
                    String content = parts[1];
                    Message receivedMessage = new Message(username, content);

                    System.out.println(receivedMessage.getUsername() + ": " + receivedMessage.getContent());

                    String serializedMessage = MESSAGE + " " + receivedMessage.getUsername() + ":::" + receivedMessage.getContent();
                    byte[] sendData = serializedMessage.getBytes();

                    for (ClientInfo client : clients) {
                        if (!client.getUsername().equals(username)) {
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort());
                            socket.send(sendPacket);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

