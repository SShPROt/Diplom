package model;

import other.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestingModel {
    private Connection connection;
    private String information;
    private int selectedDepartmentId;
    private int selectedYearIndex = -1;
    private int selectedGroupIndex = -1;
    private int selectedTestIndex = -1;
    private ArrayList<Year> years = new ArrayList<>();
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<Test> tests = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private DefaultTableModel studentModel;
    private DefaultTableModel testModel;
    private TableColumnModel columnModel;
    private Map<String, ClientHandler> clients = new HashMap<>();
    private ArrayList<String[]> data;
    private ArrayList<Results> resultsFromDb = new ArrayList<>();
    private ArrayList<Results> resultsFromTable = new ArrayList<>();

    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<Results> getResultsFromTable() {
        return resultsFromTable;
    }

    public DefaultTableModel getTestModel() {
        return testModel;
    }

    public void setTestModel(DefaultTableModel testModel) {
        this.testModel = testModel;
    }

    public Map<String, ClientHandler> getClients() {
        return clients;
    }

    public TableColumnModel getColumnModel() {
        return columnModel;
    }

    public void setColumnModel(TableColumnModel columnModel) {
        this.columnModel = columnModel;
    }

    public DefaultTableModel getStudentModel() {
        return studentModel;
    }

    public void setStudentModel(DefaultTableModel studentModel) {
        this.studentModel = studentModel;
    }

    public void setSelectedYearIndex(int selectedYearIndex) {
        this.selectedYearIndex = selectedYearIndex;
    }

    public void setSelectedGroupIndex(int selectedGroupIndex) {
        this.selectedGroupIndex = selectedGroupIndex;
    }

    public void setSelectedTestIndex(int selectedTestIndex) {
        this.selectedTestIndex = selectedTestIndex;
    }

    public String getInformation() {
        return information;
    }

    public TestingModel(Connection connection, int selectedDepartmentId){
        this.connection = connection;
        this.selectedDepartmentId = selectedDepartmentId;
    }

    public ResultSet getYearsFromDB(){
        String query = "SELECT * FROM year_of_entering";
        ResultSet res;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            res = preparedStatement.executeQuery();
            while(res.next()){
                years.add(new Year(res.getInt("year"),res.getInt("id_year")));
            }
            res = preparedStatement.executeQuery();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе получения списка учебных лет";
            return null;
        }
        return res;
    }

    public ResultSet getGroupsFromDB(){
        String query = "SELECT * FROM student_group WHERE department = ?";
        ResultSet res;
        PreparedStatement preparedStatement = null;
        clearArray(groups);
        try {
            if(selectedYearIndex != -1) {
                query += " and year = ?";
            }
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedDepartmentId);
            if(selectedYearIndex != -1) {
                preparedStatement.setInt(2, years.get(selectedYearIndex).getId());
            }
            res = preparedStatement.executeQuery();
            while (res.next()){
                groups.add(new Group(res.getString("name"), res.getInt("id_group")));
            }
            res = preparedStatement.executeQuery();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе получения списка учебных групп";
            return null;
        }
        return res;
    }

    public ResultSet getTestsFromDB(){
        String query = "SELECT * FROM tests WHERE department = ?";
        ResultSet res;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, selectedDepartmentId);
            res = preparedStatement.executeQuery();
            while (res.next()){
                tests.add(new Test(res.getString("name"), res.getInt("id_test")));
            }
            res = preparedStatement.executeQuery();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе получения списка тестов";
            return null;
        }
        return res;
    }

    private void clearArray(ArrayList array){
        while (!array.isEmpty()){
            array.remove(0);
        }
    }

    private void clearTable(){
        while (!students.isEmpty()){
            students.remove(0);
        }while (studentModel.getRowCount() > 0)
            studentModel.removeRow(0);
    }

    public void addStudentTableColumns(){
        studentModel.addColumn("ФИО");
    }

    public void addTestTableColumns(){
        testModel.addColumn("ФИО");
        testModel.addColumn("Готовность");
        testModel.addColumn("Действие");
        testModel.addColumn("Оценка");
        testModel.addColumn("Результат");
        testModel.addColumn("Прошедшее время");
    }

    public void editColumns(boolean preparation){
        if(!preparation) {
            columnModel.removeColumn(columnModel.getColumn(1));
            studentModel.setColumnCount(1);
        }
        else {
            studentModel.addColumn("Готовность");
            for(int i = 0; i < studentModel.getRowCount(); i++){
                studentModel.setValueAt("Не подключён", i, 1);
            }
        }
    }

    public boolean fillTable(){
        String query = "SELECT * FROM students WHERE student_group = ? order by fullname";
        ResultSet res;
        PreparedStatement preparedStatement = null;
        clearTable();
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, groups.get(selectedGroupIndex).getId());
            res = preparedStatement.executeQuery();
            while(res.next()){
                students.add(new Student(res.getString("fullname"), res.getInt("id_student")));
                studentModel.addRow(new Object[]{res.getString("fullname")});
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе получения списка студентов";
            return false;
        }
        return true;
    }

    public void startTest() {
        for (int i = 0; i < studentModel.getRowCount(); i++) {
            String user = (String) studentModel.getValueAt(i, 0);
            String status = (String) studentModel.getValueAt(i, 1);
            if ("Подключён".equals(status) && clients.containsKey(user)) {
                clients.get(user).sendMessage("Начало");
                String[] info = getTestInfo();
                clients.get(user).sendMessage(info[0]);
                clients.get(user).sendMessage(info[1]);
                clients.get(user).sendObject(data);
            }
        }
    }

    public void cancelTest() {

        for (ClientHandler handler : clients.values()) {
            handler.sendMessage("Тест отменён");
            handler.closeConnection();
        }
        clients.clear();

        resetStatuses();
        for(int i = 0; i < resultsFromTable.size(); i++)
            resultsFromTable.remove(0);
    }

    private void resetStatuses() {
        for (int i = 0; i < studentModel.getRowCount(); i++) {
            studentModel.setValueAt("Не подключён", i, 1);
        }
    }

    public void setUserStatus(String username, String status, boolean startTest) {
        if(startTest)
            for (int i = 0; i < testModel.getRowCount(); i++) {
                if (username.equals(testModel.getValueAt(i, 0))) {
                    testModel.setValueAt(status, i, 1);
                    studentModel.setValueAt(status, i, 1);
                }
            }
        else
            for (int i = 0; i < studentModel.getRowCount(); i++) {
                if (username.equals(studentModel.getValueAt(i, 0))) {
                    studentModel.setValueAt(status, i, 1);
                }
            }
    }

    public void showConnectionRequest(String username, ClientHandler handler) {
        JFrame connectionFrame = new JFrame("Новый запрос на подключение");
        connectionFrame.setSize(300, 150);
        connectionFrame.setLayout(new BorderLayout());

        JLabel label = new JLabel("Позволить студенту " + username + " присоединиться?");
        connectionFrame.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton allowButton = new JButton("Разрешить");
        JButton denyButton = new JButton("Отказать");

        allowButton.addActionListener(e -> {
            setUserStatus(username, "Подключён", true);
            clients.put(username, handler);
            connectionFrame.dispose();
            startTest();
        });

        denyButton.addActionListener(e -> {
            handler.sendMessage("Отказ");
            handler.closeConnection();
            connectionFrame.dispose();
        });

        buttonPanel.add(allowButton);
        buttonPanel.add(denyButton);

        connectionFrame.add(buttonPanel, BorderLayout.SOUTH);
        connectionFrame.setVisible(true);
    }

    public void copyModel(){
        while(testModel.getRowCount() > 0)
            testModel.removeRow(0);
        for (int i = 0; i < studentModel.getRowCount(); i++) {
            Object[] rowData = new Object[studentModel.getColumnCount()];
            for (int j = 0; j < studentModel.getColumnCount(); j++) {
                rowData[j] = studentModel.getValueAt(i, j);
            }
            testModel.addRow(rowData);
        }
    }

    public void getQuestionsFromDatabase() {
        data = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        String query = "SELECT question, answers, answer, type, score FROM questions WHERE test = ?";
        try{
            ResultSet res;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, tests.get(selectedTestIndex).getId());
            res = preparedStatement.executeQuery();

            while (res.next()) {
                String question = res.getString("question"),
                        answers = res.getString("answers"),
                        answer = res.getString("answer"),
                        type = res.getString("type"),
                        score = res.getString("score");
                data.add(new String[]{question, answers, answer, type, score});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String[] getTestInfo(){
        PreparedStatement preparedStatement = null;
        String query = "SELECT test_time, gradeSys FROM tests WHERE id_test = ?";
        String time = "00:00:00";
        String gradeSys = "";
        String[] info = new String[]{time, gradeSys};
        try{
            ResultSet res;
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, tests.get(selectedTestIndex).getId());
            res = preparedStatement.executeQuery();

            while (res.next()) {
                time = res.getString("test_time");
                gradeSys = res.getString("gradeSys");
            }
            info = new String[]{time, gradeSys};
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return info;
    }

    public void setResult(String user, String grade, String score, String time){
        for (int i = 0; i < testModel.getRowCount(); i++) {
            if (user.equals(testModel.getValueAt(i, 0))) {
                testModel.setValueAt(grade, i, 3);
                testModel.setValueAt(score, i, 4);
                testModel.setValueAt(time, i, 5);
            }
        }
    }

    public void initializeResultList(){
        for (int i = 0; i < studentModel.getRowCount(); i++) {
            resultsFromTable.add(new Results(tests.get(selectedTestIndex).getId(), students.get(i).getId(), groups.get(selectedGroupIndex).getId(),
                    " ", " ", "00:00:00"));
        }
    }

    public void saveResultsOnDB(){
        PreparedStatement preparedStatement = null;
        String query;
        ResultSet res;
        try{
            query = "SELECT * FROM results join students on results.student = students.id_student WHERE results.test = ? and results.student_group = ?  order by students.fullname";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, tests.get(selectedTestIndex).getId());
            preparedStatement.setInt(2, groups.get(selectedGroupIndex).getId());
            res = preparedStatement.executeQuery();
            if(!res.isBeforeFirst()){
                for(int i = 0; i < students.size(); i ++){
                    query = "INSERT INTO results (test, student, student_group, score, grade, time) VALUES (?, ?, ?, ?, ?, ?); ";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, resultsFromTable.get(i).getTestId());
                    preparedStatement.setInt(2, resultsFromTable.get(i).getStudentId());
                    preparedStatement.setInt(3, resultsFromTable.get(i).getGroupId());
                    preparedStatement.setString(4, resultsFromTable.get(i).getScore());
                    preparedStatement.setString(5, resultsFromTable.get(i).getGrade());
                    preparedStatement.setString(6, resultsFromTable.get(i).getTime());
                    preparedStatement.executeUpdate();
                }
                for (int i = 0; i < resultsFromTable.size(); i++)
                    resultsFromTable.remove(0);
            }
            else{
                while (res.next()) {
                    resultsFromDb.add(new Results(res.getInt("id_result"), res.getInt("test"),
                            res.getInt("student"), res.getInt("student_group"),
                            res.getString("score"), res.getString("grade"), res.getString("time")));
                }
                for(int i = 0; i < resultsFromDb.size(); i++){
                    int scoreDb = 0, scoreTable = 0;
                    String[] scoreDbStr, scoreTableStr;
                    if(!resultsFromTable.get(i).getGrade().equals(" ")) {
                        scoreTableStr = resultsFromTable.get(i).getScore().split("/");
                        scoreTable = Integer.parseInt(scoreTableStr[0]);
                        if (!resultsFromDb.get(i).getGrade().equals(" ")) {
                            scoreDbStr = resultsFromDb.get(i).getScore().split("/");
                            scoreDb = Integer.parseInt(scoreDbStr[0]);
                        }
                        if (scoreDb < scoreTable) {
                            resultsFromDb.get(i).setGrade(resultsFromTable.get(i).getGrade());
                            resultsFromDb.get(i).setScore(resultsFromTable.get(i).getScore());
                            resultsFromDb.get(i).setTime(resultsFromTable.get(i).getTime());
                        }
                    }
                    query = "UPDATE results SET test = ?, student = ?, student_group = ?, score = ?, grade = ?, time = ? WHERE (id_result = ?);";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, resultsFromDb.get(i).getTestId());
                    preparedStatement.setInt(2, resultsFromDb.get(i).getStudentId());
                    preparedStatement.setInt(3, resultsFromDb.get(i).getGroupId());
                    preparedStatement.setString(4, resultsFromDb.get(i).getScore());
                    preparedStatement.setString(5, resultsFromDb.get(i).getGrade());
                    preparedStatement.setString(6, resultsFromDb.get(i).getTime());
                    preparedStatement.setInt(7, resultsFromDb.get(i).getId());
                    preparedStatement.executeUpdate();
                }
                for (int i = 0; i < resultsFromDb.size(); i++) {
                    resultsFromDb.remove(0);
                    resultsFromTable.remove(0);
                }
            }
        } catch (Exception ex) {
            System.out.println("Не найдено");
        }
    }

    public void finishTest(){
        for (ClientHandler handler : clients.values()) {
            handler.sendMessage("Экстренное завершение");
        }
    }
    public void closeConnection(){
        for (ClientHandler handler : clients.values()) {
            handler.ExtracloseConnection();
        }
        clients.clear();
        resetStatuses();}
}
