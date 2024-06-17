package other;

public class Results {
    private int id;
    private int testId;
    private String test;
    private int studentId;
    private String student;
    private int groupId;
    private String group;
    private String score;
    private String grade;
    private String time;

    public String getTest() {
        return test;
    }

    public String getStudent() {
        return student;
    }

    public String getGroup() {
        return group;
    }

    public int getId() {
        return id;
    }

    public int getTestId() {
        return testId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getScore() {
        return score;
    }

    public String getGrade() {
        return grade;
    }

    public String getTime() {
        return time;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Results(int testId, int studentId, int groupId, String score, String grade, String time){
        this.testId = testId;
        this.studentId = studentId;
        this.groupId = groupId;
        this.score = score;
        this.grade = grade;
        this.time = time;
    }
    public Results(int id, int testId, int studentId, int groupId,  String score, String grade, String time){
        this.id = id;
        this.testId = testId;
        this.studentId = studentId;
        this.groupId = groupId;
        this.score = score;
        this.grade = grade;
        this.time = time;
    }
}
