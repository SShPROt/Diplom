package presenter;

import model.StudentsModel;
import view.StudentsView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class StudentsPres {
    private StudentsModel model;
    private StudentsView view;
    private enum Mode{
        newTable,
        oldTable;
    }
    private Mode mode;
    JTable studentsTable;
    JComboBox groupBox;
    JTextField nameField;
    JTextField groupNameField;
    JTextField surnameField;
    JTextField middleNameField;
    JButton addStudentBtn;
    JButton removeStudentBtn;
    JButton editStudentBtn;
    JButton createTableBtn;
    JButton saveTableBtn;
    JComboBox yearBox;
    JButton deleteGroupBtn;

    private void fillYearBox(){
        ResultSet rs = model.getYearsFromDb();
        try {
            while (rs.next()) {
                yearBox.addItem(rs.getString(2));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Найдено 0 таблиц");
        }
    }

    public void clearGroupBox(){
        while(groupBox.getItemCount() > 1){
            groupBox.removeItemAt(1);
        }
    }
    private boolean fillGroupBox(){
        clearGroupBox();
        ResultSet rs = model.getGroupsFromDb();
        try {
            while (rs.next()) {
                groupBox.addItem(rs.getString(2));
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private void fillTable(){
        groupNameField.setText(groupBox.getSelectedItem().toString());
        model.getGroupDataFromDb();
    }

    private void updateGroupBox(){
        fillGroupBox();
        groupBox.setSelectedItem(groupNameField.getText());
    }

    public StudentsPres(StudentsModel model, StudentsView view){
        this.model = model;
        this.view = view;

        studentsTable = view.getStudentsTable();
        groupBox = view.getGroupBox();
        nameField = view.getNameField();
        groupNameField = view.getGroupNameField();
        surnameField = view.getSurnameField();
        middleNameField = view.getMiddleNameField();
        addStudentBtn = view.getAddStudentBtn();
        removeStudentBtn = view.getRemoveStudentBtn();
        editStudentBtn = view.getEditStudentBtn();
        createTableBtn = view.getCreateTableBtn();
        saveTableBtn = view.getSaveTableBtn();
        yearBox = view.getYearBox();
        deleteGroupBtn = view.getDeleteGroupBtn();

        fillYearBox();

        studentsTable.setDefaultEditor(Object.class, null);
        model.setMyModel((DefaultTableModel) studentsTable.getModel());
        model.addColumns();
        studentsTable.setModel(model.getMyModel());

        saveTableBtn.setText("Создать новую таблицу");
        mode = Mode.newTable;

        yearBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!yearBox.getSelectedItem().toString().equals("...")) {
                    if(!(model.compareYear(yearBox.getSelectedIndex() - 1))) {
                        model.setSelectedYearIndex(yearBox.getSelectedIndex() - 1);
                        fillGroupBox();
                        saveTableBtn.setEnabled(true);
                        deleteGroupBtn.setEnabled(false);
                    }
                }
                else {
                    clearGroupBox();
                    model.setSelectedYearIndex(- 1);
                    saveTableBtn.setEnabled(false);
                }
            }
        });

        groupBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!groupBox.getSelectedItem().toString().equals("...")) {
                    model.setSelectedGroupIndex(groupBox.getSelectedIndex() - 1);
                    fillTable();
                    mode = Mode.oldTable;
                    saveTableBtn.setText("Сохранить таблицу");
                    deleteGroupBtn.setEnabled(true);
                }
                else{
                    saveTableBtn.setText("Создать новую таблицу");
                    mode = Mode.newTable;
                    deleteGroupBtn.setEnabled(false);
                }
            }
        });

        addStudentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!model.addRowInTable(surnameField.getText(), nameField.getText(), middleNameField.getText())){
                    JOptionPane.showMessageDialog(view, model.getInformation());
                }
                else {
                    nameField.setText("");
                    surnameField.setText("");
                    middleNameField.setText("");
                }
            }
        });

        editStudentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(studentsTable.getSelectedRowCount() == 1) {
                    model.editRowInTable(surnameField.getText(), nameField.getText(), middleNameField.getText(), studentsTable.getSelectedRow());
                }else
                    JOptionPane.showMessageDialog(editStudentBtn, "Пожалуйста выберите 1 любую строку");
            }
        });

        removeStudentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int [] rows = studentsTable.getSelectedRows();
                model.removeRows(rows);
            }
        });

        saveTableBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = groupNameField.getText();
                switch(mode)
                {
                    case newTable: {
                        if(model.compareGroupName(groupName)){
                            JOptionPane.showMessageDialog(view, model.getInformation());
                        }
                        else {
                            if(!model.addGroup(groupName)){
                                JOptionPane.showMessageDialog(view, model.getInformation());
                                break;
                            }
                            if(!fillGroupBox()){
                                JOptionPane.showMessageDialog(view, model.getInformation());
                                break;
                            }
                            if(!model.fillGroup()) {
                                JOptionPane.showMessageDialog(view, model.getInformation());
                                break;
                            }
                            JOptionPane.showMessageDialog(view, "Группа успешно создана");

                        }
                        break;
                    }
                    case oldTable: {
                        if(model.compareGroupName(groupName)){
                            JOptionPane.showMessageDialog(view, model.getInformation());
                            break;
                        }
                        model.updateTable(groupName);
                        updateGroupBox();
                    }
                }
            }
        });

        deleteGroupBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(view, "Вы уверены, что хотите удалить группу?", "", JOptionPane.YES_NO_OPTION) == 0) {
                    if (!model.deleteStudents()) {
                        JOptionPane.showMessageDialog(view, model.getInformation());
                        return;
                    }
                    model.clearTable();
                    if (!model.deleteGroup()) {
                        JOptionPane.showMessageDialog(view, model.getInformation());
                        return;
                    }
                    groupBox.removeItemAt(groupBox.getSelectedIndex());
                    groupBox.setSelectedIndex(0);
                    model.clearTable();
                    groupNameField.setText("");
                }
            }
        });

        createTableBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.clearTable();
                groupNameField.setText("");
                mode = Mode.newTable;
                saveTableBtn.setText("Создать новую таблицу");
                saveTableBtn.setEnabled(true);
                deleteGroupBtn.setEnabled(false);
            }
        });

        studentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(studentsTable.getSelectedRowCount() == 1) {
                    surnameField.setText(studentsTable.getValueAt(studentsTable.getSelectedRow(), 0).toString());
                    nameField.setText(studentsTable.getValueAt(studentsTable.getSelectedRow(), 1).toString());
                    middleNameField.setText(studentsTable.getValueAt(studentsTable.getSelectedRow(), 2).toString());
                }
            }
        });
    }
}
