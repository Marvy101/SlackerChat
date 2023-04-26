import java.net.InetAddress;

class ClientInfo {
    private String username;
    private InetAddress address;
    private int port;

    public ClientInfo(String username, InetAddress address, int port) {

  this.username = username;
          this.address = address;
          this.port = port;
          }

public String getUsername() {
        return username;
        }

public InetAddress getAddress() {
        return address;
        }

public int getPort() {
        return port;
        }
        }