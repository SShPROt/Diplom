package model;

import other.Test;
import other.Year;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestConstructorModel {
    private String information;
    private Connection connection;
    private int selectedDepartmentID;
    private ArrayList<Test> testList = new ArrayList<>();
    private DefaultListModel myModel = new DefaultListModel<>();
    private int selectedTeacherId;

    public Connection getConnection() {
        return connection;
    }

    public ArrayList<Test> getTestList() {
        return testList;
    }

    public DefaultListModel getMyModel() {
        return myModel;
    }

    public void setMyModel(DefaultListModel myModel) {
        this.myModel = myModel;
    }


    public String getInformation() {
        return information;
    }

    public TestConstructorModel(Connection connection, int selectedDepartmentID, int selectedTeacherId){
        this.connection = connection;
        this.selectedDepartmentID = selectedDepartmentID;
        this.selectedTeacherId = selectedTeacherId;
    }

    private void clearList(){
        while (myModel.getSize() > 0) {
            myModel.removeElementAt(0);
        }
        clearTestArray();
    }

    private void clearTestArray(){
        while(!testList.isEmpty())
            testList.remove(0);
    }
    public boolean fillList(boolean showAll){
        String query;
        PreparedStatement preparedStatement;
        ResultSet res;
        clearList();
        try {
            if(showAll){
                query = "SELECT * FROM tests where department = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, selectedDepartmentID);
            }
            else {
                query = "SELECT * FROM tests where department = ? and autor = ?";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, selectedDepartmentID);
                preparedStatement.setInt(2, selectedTeacherId);
            }
            res = preparedStatement.executeQuery();
            while (res.next())
            {
                testList.add(new Test(res.getString(2), res.getInt(1)));
                myModel.addElement(res.getString(2));
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе получения списка тестов";
            return false;
        }
        return true;
    }

    public boolean createTest(String testName){
        String query;
        ResultSet res;
        PreparedStatement preparedStatement;

        for(int i = 0; i < testList.size(); i++){
            if(testList.get(i).getName().equals(testName)){
                information = "Тест с таким названием уже существует";
                return false;
            }
        }

        query = "INSERT INTO tests (name, department, autor, gradeSys, test_time) VALUES (?, ?, ?, ?, ?);";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, testName);
            preparedStatement.setInt(2, selectedDepartmentID);
            preparedStatement.setInt(3, selectedTeacherId);
            preparedStatement.setString(4, "85,65,50,00");
            preparedStatement.setString(5, "00:00:00");
            preparedStatement.execute();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе создания нового теста в БД";
            return false;
        }

        try {
            query = "SELECT * FROM tests where name = ? and department = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, testName);
            preparedStatement.setInt(2, selectedDepartmentID);
            res = preparedStatement.executeQuery();
            while(res.next()){
                testList.add(new Test(res.getString(2), res.getInt(1)));
                myModel.addElement(res.getString(2));
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе добавления нового теста в таблицу";
            return false;
        }
        return true;
    }

    public boolean editTest(String testName, int selectedRowIndex){
        String query;
        PreparedStatement preparedStatement;

        query = "UPDATE tests SET name = ? WHERE (id_test = ?)";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, testName);
            preparedStatement.setInt(2, testList.get(selectedRowIndex).getId());
            preparedStatement.execute();
            myModel.setElementAt(testName, selectedRowIndex);
            testList.set(selectedRowIndex, new Test(testName, testList.get(selectedRowIndex).getId()));
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе обновления названия теста";
            return false;
        }
        return true;
    }

    public boolean removeTest(int selectedTestIndex){
        PreparedStatement preparedStatement;
        String query;
        query = "DELETE FROM results WHERE (test = ?);";
        query += "DELETE FROM questions WHERE (test = ?);";
        query += "DELETE FROM tests WHERE (id_test = ?);";

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, testList.get(selectedTestIndex).getId());
            preparedStatement.setInt(2, testList.get(selectedTestIndex).getId());
            preparedStatement.setInt(3, testList.get(selectedTestIndex).getId());
            preparedStatement.execute();
        } catch (SQLException e){
            information = "Произошла неизвестная ошибка при удалении теста.";
            return false;
        }
        myModel.removeElementAt(selectedTestIndex);
        testList.remove(selectedTestIndex);
        return true;
    }
}
