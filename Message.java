import java.io.Serializable;

public class Message implements Serializable {
    private String username;
    private String content;

    public Message(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return username + ": " + content;
    }
}
