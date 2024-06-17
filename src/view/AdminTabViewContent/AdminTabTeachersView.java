package view.AdminTabViewContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminTabTeachersView extends JPanel{
    private JPanel mainPanel;
    private JTable teachersTable;
    private JComboBox departmentBox;
    private JTextField surnameField;
    private JButton addTeacherBtn;
    private JButton editTeacherBtn;
    private JButton deleteTeacherBtn;
    private JTextField nameField;
    private JTextField middleNameField;
    private JTextField pswField;

    public JTextField getPswField() {
        return pswField;
    }

    public JTable getTeachersTable() {
        return teachersTable;
    }

    public JComboBox getDepartmentBox() {
        return departmentBox;
    }

    public JTextField getSurnameField() {
        return surnameField;
    }

    public JButton getAddTeacherBtn() {
        return addTeacherBtn;
    }

    public JButton getEditTeacherBtn() {
        return editTeacherBtn;
    }

    public JButton getDeleteTeacherBtn() {
        return deleteTeacherBtn;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getMiddleNameField() {
        return middleNameField;
    }

    public AdminTabTeachersView() {
        add(mainPanel);
        setVisible(true);
        mainPanel.setPreferredSize(new Dimension(780, 720));
    }
}
