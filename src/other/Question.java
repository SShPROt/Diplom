package other;

public class Question {
    private String name;
    private int id;
    private String type;

    public Question(String name, int id, String type){
        this.name= name;
        this.id = id;
        this.type = type;

    }

    public Question(String name, String type){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}