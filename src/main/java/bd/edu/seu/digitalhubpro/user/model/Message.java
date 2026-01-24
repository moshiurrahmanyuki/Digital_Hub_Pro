package bd.edu.seu.digitalhubpro.user.model;

public class Message {
    private String type;
    private String text;

    public Message(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Message success(String text) {
        return new Message(text, "success");
    }

    public static Message error(String text) {
        return new Message(text, "error");
    }
}
