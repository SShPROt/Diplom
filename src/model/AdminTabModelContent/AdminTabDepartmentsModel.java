package model.AdminTabModelContent;

import other.Crypto;
import other.Department;
import other.Teacher;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class AdminTabDepartmentsModel {
    private DefaultTableModel myModel;
    private String firstColumn = "Кафедра";
    private Connection connection;
    private String information;
    private ArrayList<Department> departments = new ArrayList<>();

    public String getInformation() {
        return information;
    }

    public DefaultTableModel getMyModel() {
        return myModel;
    }

    public void setMyModel(DefaultTableModel myModel) {
        this.myModel = myModel;
    }

    public void addColumns(){
        myModel.addColumn(firstColumn);
    }
    public AdminTabDepartmentsModel(Connection connection){
        this.connection = connection;
    }

    private void clearTable(){
        while (myModel.getRowCount() > 0) {
            myModel.removeRow(0);
        }
        clearStudentsArray();
    }

    private void clearStudentsArray(){
        while(!departments.isEmpty())
            departments.remove(0);
    }

    public boolean fillTable(){
        PreparedStatement preparedStatement;
        String query;
        ResultSet res;
        clearTable();
        query = "SELECT * from department order by name";
        try {
            preparedStatement = connection.prepareStatement(query);
            res = preparedStatement.executeQuery();
            while(res.next()){
                departments.add(new Department(res.getString(2), res.getInt(1)));
                myModel.addRow(new Object[]{res.getString(2)});
            }
        } catch (SQLException ex) {
            information = "Не найдено ни одной кафедры. Создайте новую";
            return false;
        }
        return true;
    }

    public boolean addDepartment(String department){
        String query;
        ResultSet res;
        PreparedStatement preparedStatement;

        for(int i = 0; i < departments.size(); i++){
            if(department.equals(departments.get(i).getName())){
                information = "Кафедра с таким названием уже существует";
                return false;
            }
        }

        query = "INSERT INTO department (name) VALUES (?)";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, department);
            preparedStatement.execute();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе добавления новой кафедры в БД";
            return false;
        }
        query = "SELECT * FROM department where name = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, department);
            res = preparedStatement.executeQuery();
            while(res.next()){
                departments.add(new Department(res.getString(2), res.getInt(1)));
                myModel.addRow(new Object[]{res.getString(2)});
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе добавления новой кафедры в таблицу";
            return false;
        }
        return true;
    }

    public boolean editDepartment(String department, int selectedRowIndex){
        String query;
        PreparedStatement preparedStatement;

        query = "UPDATE department SET name = ? WHERE (id_department = ?)";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, department);
            preparedStatement.setInt(2, departments.get(selectedRowIndex).getId());
            preparedStatement.execute();
            myModel.setValueAt(department, selectedRowIndex, 0);
            departments.set(selectedRowIndex, new Department(department, departments.get(selectedRowIndex).getId()));
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе обновления названия кафедры";
            return false;
        }
        return true;
    }

    public boolean removeRows(int [] rows){
        PreparedStatement preparedStatement;
        StringBuilder query = new StringBuilder();

        for(int i = 0; i < Arrays.stream(rows).count(); i++) {
            query.append("DELETE FROM department WHERE (id_department = ").append(departments.get(rows[i]).getId()).append(");");
        }

        try {
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.execute();
        } catch (SQLException e){
            information = "Невозможно удалить кафедру. Сначала необходимо удалить учебные группы, преподавателей и тесты, принадлежащие этой кафедре";
            return false;
        }

        for(int i = 0; i < Arrays.stream(rows).count(); i++) {
            myModel.removeRow(rows[i] - i);
            departments.remove(rows[i] - i);
        }
        return true;
    }
}
