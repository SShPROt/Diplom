package model;

import other.*;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ResultsModel {
    private Connection connection;
    private int selectedDepartmentId;
    private DefaultTableModel studentModel;
    private String information;
    private ArrayList<Year> years = new ArrayList<>();
    private ArrayList<Group> groups = new ArrayList<>();
    private ArrayList<Test> tests = new ArrayList<>();
    private ArrayList<Results> results = new ArrayList<>();
    private int selectedYearIndex = -1;
    private int selectedGroupIndex = -1;
    private int selectedTestIndex = -1;

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

    public ResultsModel(Connection connection, int selectedDepartmentId){
        this.connection = connection;
        this.selectedDepartmentId = selectedDepartmentId;
    }

    public DefaultTableModel getStudentModel() {
        return studentModel;
    }

    public void setStudentModel(DefaultTableModel studentModel) {
        this.studentModel = studentModel;
    }

    public void addStudentTableColumns(){
        studentModel.addColumn("Название теста");
        studentModel.addColumn("Группа");
        studentModel.addColumn("ФИО");
        studentModel.addColumn("Оценка");
        studentModel.addColumn("Результат");
        studentModel.addColumn("Затраченное время");
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
            query += " order by name";
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
        String query = "SELECT test, name, student_group FROM university.results join university.tests on results.test = tests.id_test where student_group = ? group by test  order by name;";
        ResultSet res;
        PreparedStatement preparedStatement = null;
        clearArray(tests);
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, groups.get(selectedGroupIndex).getId());
            res = preparedStatement.executeQuery();
            while (res.next()){
                tests.add(new Test(res.getString("name"), res.getInt("test")));
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

    public boolean fillTable(){
        String query;
        ResultSet res;
        PreparedStatement preparedStatement = null;
        clearTable();
        query = "SELECT * FROM university.results join university.student_group on results.student_group = student_group.id_group " +
                "join university.tests on results.test = tests.id_test " +
                "join university.students on students.id_student = results.student where id_group = ?";
        if(selectedTestIndex != -1){
            query += " and id_test = ?";
        }
        query += " order by tests.name;";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, groups.get(selectedGroupIndex).getId());
            if(selectedTestIndex != -1){
                preparedStatement.setInt(2, tests.get(selectedTestIndex).getId());
            }
            res = preparedStatement.executeQuery();
            while(res.next()){
                studentModel.addRow(new Object[]{res.getString("tests.name"), res.getString("student_group.name"),
                        res.getString("fullname"), res.getString("score"), res.getString("grade"), res.getString("time")});
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе получения списка студентов";
            return false;
        }
        return true;
    }

    private void clearTable(){
        while (studentModel.getRowCount() > 0)
            studentModel.removeRow(0);
    }
}
