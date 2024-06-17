package other;

public class Student {
    private String fullName;
    private int id;

    public Student(String fullName, int id){
        this.fullName= fullName;
        this.id = id;
    }

    public Student(String fullName){
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public int getId() {
        return id;
    }
}
