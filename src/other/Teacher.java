package other;

public class Teacher {
    private String fullName;
    private int id;

    public Teacher(String fullName, int id){
        this.fullName= fullName;
        this.id = id;
    }

    public Teacher(String fullName){
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public int getId() {
        return id;
    }
}
