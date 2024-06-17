package presenter.AdminTabPresContent;

import model.AdminTabModelContent.AdminTabTeachersModel;
import view.AdminTabViewContent.AdminTabTeachersView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminTabTeachersPres {
    private AdminTabTeachersView view;
    private AdminTabTeachersModel model;
    private JTable teachersTable;
    private JComboBox departmentsComboBox;
    public AdminTabTeachersPres(AdminTabTeachersView view, AdminTabTeachersModel model){
        this.view = view;
        this.model = model;
        teachersTable = view.getTeachersTable();
        departmentsComboBox = view.getDepartmentBox();

        teachersTable.setDefaultEditor(Object.class, null);
        model.setMyModel((DefaultTableModel) teachersTable.getModel());
        model.addColumns();
        teachersTable.setModel(model.getMyModel());

        if(!model.fillTable()){
            JOptionPane.showMessageDialog(view, model.getInformation());
        }
        if(!fillComboBox()){
            JOptionPane.showMessageDialog(view, model.getInformation());
        }

        view.getDepartmentBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setSelectedDepartmentIndex(departmentsComboBox.getSelectedIndex() - 1);
                model.fillTable();
            }
        });

        view.getAddTeacherBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!departmentsComboBox.getSelectedItem().toString().equals("...")) {
                    String surname = view.getSurnameField().getText(),
                            name = view.getNameField().getText(),
                            middleName = view.getMiddleNameField().getText(),
                            password = view.getPswField().getText();
                    if(!model.addNewTeacher(surname, name, middleName, password))
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    fillFields("", "", "", "");
                }
                else
                    JOptionPane.showMessageDialog(view, "Выберите кафедру для добавления преподавателя");
            }
        });

        view.getEditTeacherBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(teachersTable.getSelectedRowCount() == 1) {
                    String surname = view.getSurnameField().getText(),
                            name = view.getNameField().getText(),
                            middleName = view.getMiddleNameField().getText(),
                            password = view.getPswField().getText();
                    int selectedRowIndex = teachersTable.getSelectedRow();
                    if(!model.editTeacher(surname, name, middleName, password, selectedRowIndex))
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    fillFields("", "", "", "");
                }
                else
                    JOptionPane.showMessageDialog(view, "Выберите лишь одного преподавателя");
            }
        });

        view.getDeleteTeacherBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(teachersTable.getSelectedRowCount() > 0) {
                    int[] rows = teachersTable.getSelectedRows();
                    if(!model.removeRows(rows))
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    fillFields("", "", "", "");
                }
            }
        });

        teachersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(teachersTable.getSelectedRowCount() == 1) {
                    String fullName = teachersTable.getValueAt(teachersTable.getSelectedRow(), 0).toString(),
                            password = teachersTable.getValueAt(teachersTable.getSelectedRow(),1).toString();
                    String [] splitFullName = model.splitFullName(fullName);
                    fillFields(splitFullName[0], splitFullName[1], splitFullName[2], password);
                }
            }
        });
    }
    private boolean fillComboBox() {
        ResultSet rs = model.getDepartmentsFromDb();
        if(rs != null) {
            try {
                while (rs.next()) {
                    departmentsComboBox.addItem(rs.getString(2));
                }
                return true;
            }
            catch (SQLException e) {
                model.setInformation("Не найдено ни одной кафедры");
                return false;
            }
        }
        else
            return false;
    }

    private void fillFields(String surname, String name, String middleName, String password){
        view.getSurnameField().setText(surname);
        view.getNameField().setText(name);
        view.getMiddleNameField().setText(middleName);
        view.getPswField().setText(password);
    }
}
