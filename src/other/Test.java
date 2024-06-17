package other;

public class Test {
    private String name;
    private int id;

    public Test(String name, int id){
        this.name= name;
        this.id = id;
    }

    public Test(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
