package illinois.nao.nao.User;

/**
 * Created by franklinye on 12/6/16.
 */

public class PostEvent {

    public enum Type {
        TEXT, IMAGE, AUDIO, VIDEO;
    }

    private String author;

    public Type getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    private Type type;

    public PostEvent() {

    }

    public PostEvent(String author, Type type) {
        this.author = author;
        this.type = type;
    }

}
