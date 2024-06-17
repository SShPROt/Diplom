package model.AdminTabModelContent;

import other.Department;
import other.Year;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class AdminTabYearsModel {

    private DefaultTableModel myModel;
    private String firstColumn = "Год";
    private Connection connection;
    public DefaultTableModel getMyModel() {
        return myModel;
    }

    public void setMyModel(DefaultTableModel myModel) {
        this.myModel = myModel;
    }

    public void addColumns(){
        myModel.addColumn(firstColumn);
    }
    public AdminTabYearsModel(Connection connection){
        this.connection = connection;
    }

    private String information;
    private ArrayList<Year> years = new ArrayList<>();

    public String getInformation() {
        return information;
    }

    private void clearTable(){
        while (myModel.getRowCount() > 0) {
            myModel.removeRow(0);
        }
        clearStudentsArray();
    }

    private void clearStudentsArray(){
        while(!years.isEmpty())
            years.remove(0);
    }

    public boolean fillTable(){
        PreparedStatement preparedStatement;
        String query;
        ResultSet res;
        clearTable();
        query = "SELECT * from year_of_entering order by year";
        try {
            preparedStatement = connection.prepareStatement(query);
            res = preparedStatement.executeQuery();
            while(res.next()){
                years.add(new Year(res.getInt(2), res.getInt(1)));
                myModel.addRow(new Object[]{res.getInt(2)});
            }
        } catch (SQLException ex) {
            information = "Не найдено ни одного учебного года. Добавьте новый";
            return false;
        }
        return true;
    }

    public boolean addYear(int year){
        String query;
        ResultSet res;
        PreparedStatement preparedStatement;

        for(int i = 0; i < years.size(); i++){
            if(year == years.get(i).getYear()){
                information = "Такой учебный год уже существует";
                return false;
            }
        }

        query = "INSERT INTO year_of_entering (year) VALUES (?)";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, year);
            preparedStatement.execute();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе добавления нового учебного года в БД";
            return false;
        }
        query = "SELECT * FROM year_of_entering where year = ?";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, year);
            res = preparedStatement.executeQuery();
            while(res.next()){
                years.add(new Year(res.getInt(2), res.getInt(1)));
                myModel.addRow(new Object[]{res.getString(2)});
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе добавления нового учебного года в таблицу";
            return false;
        }
        return true;
    }

    public boolean editYear(int year, int selectedRowIndex){
        String query;
        PreparedStatement preparedStatement;

        query = "UPDATE year_of_entering SET year = ? WHERE (id_year = ?)";
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, year);
            preparedStatement.setInt(2, years.get(selectedRowIndex).getId());
            preparedStatement.execute();
            myModel.setValueAt(year, selectedRowIndex, 0);
            years.set(selectedRowIndex, new Year(year, years.get(selectedRowIndex).getId()));
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе обновления учебного года";
            return false;
        }
        return true;
    }

    public boolean removeRows(int [] rows){
        PreparedStatement preparedStatement;
        StringBuilder query = new StringBuilder();

        for(int i = 0; i < Arrays.stream(rows).count(); i++) {
            query.append("DELETE FROM year_of_entering WHERE (id_year = ").append(years.get(rows[i]).getId()).append(");");
        }

        try {
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.execute();
        } catch (SQLException e){
            information = "Невозможно удалить учебный год. Сначала необходимо удалить учебные группы";
            return false;
        }

        for(int i = 0; i < Arrays.stream(rows).count(); i++) {
            myModel.removeRow(rows[i] - i);
            years.remove(rows[i] - i);
        }
        return true;
    }
}
