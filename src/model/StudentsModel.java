package model;

import other.Department;
import other.Group;
import other.Student;
import other.Year;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class StudentsModel {
    private Connection connection;
    private final String firstColumn = "Фамилия";
    private final String secondColumn = "Имя";
    private final String thirdColumn = "Отчество";
    private DefaultTableModel myModel;
    private int selectedDepartmentIndex;
    private int selectedYearIndex = -1;
    private int selectedGroupIndex = -1;
    private ArrayList<Student> studentsListFromDb = new ArrayList<>();
    private ArrayList<Student> studentsListFromTable = new ArrayList<>();
    private ArrayList<Group> groupsListFromDb = new ArrayList<>();
    private ArrayList<Group> groupsListFromComboBox = new ArrayList<>();
    private ArrayList<Year> yearListFromDb = new ArrayList<>();
    private ArrayList<Department> departmentListFromDb = new ArrayList<>();
    private String information;

    public String getInformation() {
        return information;
    }

    public void setSelectedYearIndex(int selectedYearIndex) {
        this.selectedYearIndex = selectedYearIndex;
    }

    public int getSelectedGroupIndex() {
        return selectedGroupIndex;
    }

    public void setSelectedGroupIndex(int selectedGroupIndex) {
        this.selectedGroupIndex = selectedGroupIndex;
    }

    public DefaultTableModel getMyModel() {
        return myModel;
    }

    public void setMyModel(DefaultTableModel myModel) {
        this.myModel = myModel;
    }

    public StudentsModel(Connection connection, int selectedDepartmentIndex, ArrayList<Department> departments){
        this.connection = connection;
        this.selectedDepartmentIndex = selectedDepartmentIndex;
        this.departmentListFromDb = departments;
    }

    public ResultSet getYearsFromDb(){
        Statement statement;
        StringBuilder query = new StringBuilder();
        ResultSet res;
        query.append("select * from year_of_entering order by year");
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(query.toString());
            while(res.next()){
                yearListFromDb.add(new Year(res.getInt(2), res.getInt(1)));
            }
            res = statement.executeQuery(query.toString());

        } catch (SQLException ex) {
            res = null;
        }
        return res;
    }

    public ResultSet getGroupsFromDb(){
        Statement statement;
        StringBuilder query = new StringBuilder();
        ResultSet res;
        while(!groupsListFromDb.isEmpty())
            groupsListFromDb.remove(0);
        try {
            query.append("SELECT * FROM student_group where department = ").append(departmentListFromDb.get(selectedDepartmentIndex).getId())
                .append(" and year = ").append(yearListFromDb.get(selectedYearIndex).getId()).append(" order by name").append(";");
            statement = connection.createStatement();
            res = statement.executeQuery(query.toString());
            while (res.next()) {
                groupsListFromDb.add(new Group(res.getString(2), res.getInt(1)));
            }
            res = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            information = "Произошла ошибка на этапе загрузки списка групп";
            res = null;
        }
        return res;
    }

    public void addColumns(){
        myModel.addColumn(firstColumn);
        myModel.addColumn(secondColumn);
        myModel.addColumn(thirdColumn);
    }

    public void getGroupDataFromDb(){
        Statement statement;
        StringBuilder query = new StringBuilder();
        ResultSet res;
        String fullName;
        String [] splitFullName;
        query.append("SELECT * FROM students join student_group on student_group.id_group = students.student_group ")
                .append("where student_group.name = \"").append(groupsListFromDb.get(selectedGroupIndex).getName()).append("\" order by fullname");
        clearTable();
        try {
            statement = connection.createStatement();
            res = statement.executeQuery(query.toString());
            while (res.next()) {
                studentsListFromDb.add(new Student(res.getString(2), res.getInt(1)));
                studentsListFromTable.add(new Student(res.getString(2), res.getInt(1)));
                fullName = res.getString(2);
                splitFullName = fullName.split(" ");
                myModel.addRow(new Object[]{splitFullName[0], splitFullName[1], splitFullName[2]});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Не удалось отобразить таблицу");
        }
    }

    public boolean addRowInTable(String surname, String name, String middleName){
        StringBuilder fullName = new StringBuilder();
        fullName.append(surname).append(" ").append(name).append(" ").append(middleName);
        for(int i = 0; i < studentsListFromTable.size(); i++){
            if(fullName.toString().equals(studentsListFromTable.get(i).getFullName())){
                information = "Студент с таким ФИО в группе уже существует";
                return false;
            }
        }
        myModel.addRow(new Object[]{surname, name, middleName});
        studentsListFromTable.add(new Student(fullName.toString()));
        return true;
    }

    public void editRowInTable(String surname, String name, String middleName, int row){
        StringBuilder oldFullName = new StringBuilder();
        StringBuilder newFullName = new StringBuilder();
        oldFullName.append(myModel.getValueAt(row, 0)).append(" ").append(myModel.getValueAt(row, 1))
                .append(" ").append(myModel.getValueAt(row, 2));
        newFullName.append(surname).append(" ").append(name).append(" ").append(middleName);
        studentsListFromTable.set(row, new Student(newFullName.toString(), studentsListFromTable.get(row).getId()));
        myModel.setValueAt(surname, row, 0);
        myModel.setValueAt(name, row, 1);
        myModel.setValueAt(middleName, row, 2);
    }


    public void removeRows(int [] rows){
        for(int i = 0; i < Arrays.stream(rows).count(); i++) {
            studentsListFromTable.remove(rows[i] - i);
            myModel.removeRow(rows[i] - i);
        }
    }

    public boolean compareGroupName(String groupName){
        if(groupsListFromDb.isEmpty()) {
            if (groupName.equals(groupsListFromDb.get(selectedGroupIndex).getName())) {
                return false;

            }
            for (Group group : groupsListFromDb) {
                if (groupName.equals(group.getName())) {
                    information = "Группа с таким названием на кафедре уже сущетсвует";
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addGroup(String tableName){
        StringBuilder query = new StringBuilder();
        PreparedStatement preparedStatement;
        try {

            query.append("INSERT INTO student_group (name, department, year) VALUES (\"").append(tableName)
                    .append("\", ").append(departmentListFromDb.get(selectedDepartmentIndex).getId()).append(", ")
                    .append(yearListFromDb.get(selectedYearIndex).getId()).append(")");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            information = "Произошла ошибка на этапе сохранения названия группы";
            return false;
        }
        return true;
    }

    public boolean fillGroup(){
        StringBuilder query = new StringBuilder();
        PreparedStatement preparedStatement;
        int newGroupId = findMaxId();
        try {
            for(int i = 0; i < studentsListFromTable.size(); i++){
                query.append("INSERT INTO students (fullname, student_group) VALUES (\"").append(studentsListFromTable.get(i).getFullName())
                    .append("\", ").append(newGroupId).append("); ");
            }
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            information = "Произошла ошибка на этапе сохранения пользователей";
            return false;
        }
        return true;
    }

    private int findMaxId(){
        int max = 0;
        for(int i = 0; i < groupsListFromDb.size(); i++) {
            max = Math.max(max, groupsListFromDb.get(i).getId());
        }
        return max;
    }

    public void updateTable(String groupName){
        PreparedStatement preparedStatement;
        StringBuilder query = new StringBuilder();
        try {
            if (!groupName.equals(groupsListFromDb.get(selectedGroupIndex).getName())) {
                query.append("UPDATE student_group SET name = \"").append(groupName).append("\" WHERE (id_group = ")
                        .append(groupsListFromDb.get(selectedGroupIndex).getId()).append("); ");
            }

            for (int i = 0; i < studentsListFromTable.size(); i++) {
                if (studentsListFromTable.get(i).getId() != 0) {
                    for (int j = 0; j < studentsListFromDb.size(); j++) {
                        if (studentsListFromTable.get(i).getId() == studentsListFromDb.get(j).getId()) {
                            if (!(studentsListFromTable.get(i).getFullName().equals(studentsListFromDb.get(j).getFullName()))) {
                                query.append("UPDATE students SET fullname = \"").append(studentsListFromTable.get(i).getFullName())
                                        .append("\" WHERE (id_student = ").append(studentsListFromTable.get(i).getId()).append("); ");
                            }
                            studentsListFromDb.remove(j);
                            break;
                        }
                    }
                } else {
                    query.append("INSERT INTO students (fullname, student_group) VALUES (\"").append(studentsListFromTable.get(i).getFullName())
                            .append("\", ").append(groupsListFromDb.get(selectedGroupIndex).getId()).append("); ");
                }
            }
            for (int i = 0; i < studentsListFromDb.size(); i++) {
                query.append("delete from students where (id_student = ")
                        .append(studentsListFromDb.get(i).getId()).append("); ");
            }
            if (!query.toString().equals("")){
                preparedStatement = connection.prepareStatement(query.toString());
                preparedStatement.execute();
            }
            JOptionPane.showMessageDialog(null, "Таблица успешно обновлена");
        }catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Неверное название таблицы либо неверно заполнены поля ФИО");
            throw new RuntimeException(ex);
        }
    }

    public boolean compareYear(int newSelectedYearIndex){
        return newSelectedYearIndex == selectedYearIndex;
    }

    public void clearTable(){
        while (myModel.getRowCount() > 0) {
            myModel.removeRow(0);
        }
        clearStudentsArray();
    }

    private void clearStudentsArray(){
        while(!studentsListFromTable.isEmpty())
            studentsListFromTable.remove(0);
        while(!studentsListFromDb.isEmpty())
            studentsListFromDb.remove(0);
    }

    public boolean deleteStudents(){
        StringBuilder query = new StringBuilder();
        PreparedStatement preparedStatement;
        try {
            query.append("DELETE FROM students WHERE (student_group = ").append(groupsListFromDb.get(selectedGroupIndex).getId()).append(");");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            information = "Произошла ошибка на этапе удаления студента(ов)";
            return false;
        }
        return true;
    }

    public boolean deleteGroup(){
        StringBuilder query = new StringBuilder();
        PreparedStatement preparedStatement;
        try {
            query.append("DELETE FROM student_group WHERE (id_group = ").append(groupsListFromDb.get(selectedGroupIndex).getId()).append(");");
            preparedStatement = connection.prepareStatement(query.toString());
            preparedStatement.execute();
        } catch (SQLException ex) {
            information = "Произошла ошибка на этапе удаления группы пользователей";
            return false;
        }
        return true;
    }
}