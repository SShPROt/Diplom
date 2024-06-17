package model;

import other.Crypto;
import other.DefaultDataForAuthorization;
import other.Department;

import javax.swing.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;

public class AuthorizationModel {
    private DefaultDataForAuthorization data = new DefaultDataForAuthorization();
    private Connection connection;
    private String nameDb = "university";
    private String teachersTableName = "teachers";
    private boolean authorizationComplete = false;
    private int selectedDepartmentIndex;
    private int selectedTeacherId;
    private String information;
    private ArrayList<Department> departmentListFromDb = new ArrayList<>();

    public ArrayList<Department> getDepartmentListFromDb() {
        return departmentListFromDb;
    }

    public int getSelectedTeacherId() {
        return selectedTeacherId;
    }

    public String getInformation() {
        return information;
    }

    public int getDepartmentIndex() {
        return selectedDepartmentIndex;
    }

    public void setDepartment(int department) {
        this.selectedDepartmentIndex = department;
    }


    public boolean isAuthorizationComplete() {
        return authorizationComplete;
    }
    public AuthorizationModel() {
    }

    public Connection getConnection() {
        return connection;
    }
    public DefaultDataForAuthorization getDefaultData(){
        return data;
    }

    public boolean attemptConnect(String ip, String port, String login, String password){
        StringBuilder url = new StringBuilder();
        url.append("jdbc:mysql://").append(ip).append(":").append(port).append("/").append(nameDb).append("?allowMultiQueries=true");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url.toString(), login, password);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            JOptionPane.showMessageDialog(null, new RuntimeException(ex));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, new RuntimeException(ex));
            connection = null;
            return false;
        }
        return true;
    }
    public ResultSet getDepartmentsFromDb(){
        Statement statement;
        StringBuilder query = new StringBuilder();
        ResultSet res;
        query.append("select * from department order by name");
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(query.toString());
            while(res.next()){
                departmentListFromDb.add(new Department(res.getString(2), res.getInt(1)));
            }
            res = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            res = null;
        }
        return res;
    }

    public boolean compareUser(String surname, String name, String middleName, String password){
        StringBuilder fullName = new StringBuilder();
        Statement statement;
        StringBuilder query = new StringBuilder();
        ResultSet res;
        Crypto crypto = new Crypto();
        fullName.append(surname).append(" ").append(name).append(" ").append(middleName);
        query.append("SELECT * FROM ").append(teachersTableName).append(" inner join department on department.id_department = ")
                .append(teachersTableName).append(".department where department.id_department = \"")
                .append(departmentListFromDb.get(selectedDepartmentIndex).getId()).append("\"");
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(query.toString());
            if (res.isBeforeFirst()) {
                while (res.next()) {
                    if (fullName.toString().equals(res.getString(2))) {
                        if (password.equals(crypto.decrypt(res.getString(4)))) {
                            selectedTeacherId = res.getInt(1);
                            information = "Вход успешно выполнен.";
                            authorizationComplete = true;
                            return true;
                        }
                    }
                }
                information = "Неверные ФИО/пароль.";
            }
            else information = "В системе нет зарегистрированных преподавателей";
        } catch (SQLException ex) {
            information = "Возникла ошибка при отправке запроса. Некорректный запрос.";
        }
        return false;
    }
}
