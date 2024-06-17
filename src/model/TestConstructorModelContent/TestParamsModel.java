package model.TestConstructorModelContent;

import other.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestParamsModel {
    private Connection connection;
    private int selectedTestIndex;
    private ArrayList<Test> testsList;
    private String information;

    public String getInformation() {
        return information;
    }

    public TestParamsModel(Connection connection, ArrayList<Test> testsList, int selectedTestIndex){
        this.connection = connection;
        this.selectedTestIndex = selectedTestIndex;
        this.testsList = testsList;
    }

    public boolean saveParamsOnDB(String grades, String time){
        String query = "UPDATE tests SET gradeSys = ?, test_time = ? WHERE (id_test = ?);";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, grades);
            preparedStatement.setString(2, time);
            preparedStatement.setInt(3, testsList.get(selectedTestIndex).getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе обновления параметров в БД";
            return false;
        }
        return true;
    }

    public ResultSet getDataFromDb(){
        String query = "SELECT * FROM tests where id_test = ?";
        PreparedStatement preparedStatement = null;
        ResultSet res;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, testsList.get(selectedTestIndex).getId());
            res = preparedStatement.executeQuery();
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе получения данных полей из бд";
            return null;
        }
        return res;
    }
}
