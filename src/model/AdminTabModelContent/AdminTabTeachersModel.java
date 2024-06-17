package model.AdminTabModelContent;

import other.Crypto;
import other.Department;
import other.Teacher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AdminTabTeachersModel {
    private DefaultTableModel myModel;
    private Connection connection;
    private String firstColumn = "ФИО";
    private String secondColumn = "Пароль";
    private String thirdColumn = "Кафедра";
    private ArrayList<Teacher> teachersListFromDb = new ArrayList<>();
    private ArrayList<Department> departments = new ArrayList<>();
    private int selectedDepartmentIndex = -1;
    private String information;

    public void setSelectedDepartmentIndex(int selectedDepartmentIndex) {
        this.selectedDepartmentIndex = selectedDepartmentIndex;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public DefaultTableModel getMyModel() {
        return myModel;
    }

    public void setMyModel(DefaultTableModel myModel) {
        this.myModel = myModel;
    }

    public AdminTabTeachersModel(Connection connection){
        this.connection = connection;
    }

    public void addColumns(){
        myModel.addColumn(firstColumn);
        myModel.addColumn(secondColumn);
        myModel.addColumn(thirdColumn);
    }

    private void clearTable(){
        while (myModel.getRowCount() > 0) {
            myModel.removeRow(0);
        }
        clearStudentsArray();
    }

    private void clearStudentsArray(){
        while(!teachersListFromDb.isEmpty())
            teachersListFromDb.remove(0);
    }

    public boolean fillTable(){
        PreparedStatement preparedStatement;
        StringBuilder query = new StringBuilder();
        ResultSet res;
        String fullName, password, department;
        int id;
        clearTable();
        query.append("SELECT * FROM teachers join department on teachers.department = department.id_department");
        if(selectedDepartmentIndex != -1)
            query.append(" where department = ").append(departments.get(selectedDepartmentIndex).getId());
        query.append(" order by fullname;");
        try {
            preparedStatement = connection.prepareStatement(query.toString());
            res = preparedStatement.executeQuery();
            while (res.next()){
                id = res.getInt(1);
                fullName = res.getString(2);
                password = res.getString(4);
                department = res.getString(6);
                myModel.addRow(new Object[]{fullName, password, department});
                teachersListFromDb.add(new Teacher(fullName, id));
            }
        } catch (SQLException e) {
            information = "Не найдено ни одного преподавателя. Добавьте нового";
            return false;
        }
        return true;
    }

    public ResultSet getDepartmentsFromDb(){
        PreparedStatement preparedStatement;
        String query;
        ResultSet res;
        query = "SELECT * from department order by name";
        try {
            preparedStatement = connection.prepareStatement(query);
            res = preparedStatement.executeQuery();
            while(res.next()){
                departments.add(new Department(res.getString(2), res.getInt(1)));
            }
            res = preparedStatement.executeQuery();
        } catch (SQLException ex) {
            res = null;
        }
        return res;
    }

    public boolean addNewTeacher(String surname, String name, String middleName, String password){
        StringBuilder fullName = new StringBuilder();
        Crypto crypto = new Crypto();
        String query;
        ResultSet res;
        PreparedStatement preparedStatement;
        fullName.append(surname).append(" ").append(name).append(" ").append(middleName);

        for(int i = 0; i < teachersListFromDb.size(); i++){
            if(fullName.toString().equals(teachersListFromDb.get(i).getFullName())){
                information = "Преподаватель с таким ФИО уже существует";
                return false;
            }
        }

        query = "INSERT INTO teachers (fullname, department, password) VALUES (?, ?, ?)";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, fullName.toString());
            preparedStatement.setInt(2, departments.get(selectedDepartmentIndex).getId());
            preparedStatement.setString(3, crypto.encrypt(password));
            preparedStatement.execute();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе добавления нового преподавателя в БД";
            return false;
        }
        query = "SELECT * FROM teachers  join department on teachers.department = department.id_department " +
                "where teachers.department = ? and teachers.fullname = ? order by fullname";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, departments.get(selectedDepartmentIndex).getId());
            preparedStatement.setString(2, fullName.toString());
            res = preparedStatement.executeQuery();
            while(res.next()){
                teachersListFromDb.add(new Teacher(res.getString(2), res.getInt(1)));
                myModel.addRow(new Object[]{res.getString(2), res.getString(4), res.getString(6)});
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе добавления нового преподавателя в таблицу";
            return false;
        }
        return true;
    }

    public boolean editTeacher(String surname, String name, String middleName, String password, int selectedRowIndex){
        StringBuilder fullName = new StringBuilder();
        Crypto crypto = new Crypto();
        String query;
        PreparedStatement preparedStatement;
        fullName.append(surname).append(" ").append(name).append(" ").append(middleName);

        query = "UPDATE teachers SET fullname = ?, password = ? WHERE (id_teachers = ?);";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, fullName.toString());
            preparedStatement.setString(2, crypto.encrypt(password));
            preparedStatement.setInt(3, teachersListFromDb.get(selectedRowIndex).getId());
            preparedStatement.execute();
            myModel.setValueAt(fullName, selectedRowIndex, 0);
            myModel.setValueAt(password, selectedRowIndex, 1);
            teachersListFromDb.set(selectedRowIndex, new Teacher(fullName.toString(), teachersListFromDb.get(selectedRowIndex).getId()));
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе обновления данных преподавателя";
            return false;
        }
        return true;
    }

    public String[] splitFullName(String fullName){
        return fullName.split(" ");
    }

    public boolean removeRows(int [] rows){
        PreparedStatement preparedStatement;
        StringBuilder query = new StringBuilder();

        for(int i = 0; i < Arrays.stream(rows).count(); i++) {
            query.append("DELETE FROM teachers WHERE (id_teachers = ").append(teachersListFromDb.get(rows[i]).getId()).append(");");
        }

        try {
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.execute();
        } catch (SQLException e){
            information = "Невозможно удалить преподавателя. Сначала необходимо удалить тесты, принадлежащие преподавателю";
            return false;
        }

        for(int i = 0; i < Arrays.stream(rows).count(); i++) {
            myModel.removeRow(rows[i] - i);
            teachersListFromDb.remove(rows[i] - i);
        }
        return true;
    }
}
