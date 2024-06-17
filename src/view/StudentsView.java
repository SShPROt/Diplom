package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentsView extends JPanel{
    private JTable studentsTable;
    private JTextField nameField;
    private JComboBox groupBox;
    private JTextField groupNameField;
    private JPanel tablePanel;
    private JPanel insertPanel;
    private JButton addStudentBtn;
    private JButton removeStudentBtn;
    private JButton editStudentBtn;
    private JButton createTableBtn;
    private JButton saveTableBtn;
    private JTextField surnameField;
    private JTextField middleNameField;

    public JComboBox getYearBox() {
        return yearBox;
    }

    private JComboBox yearBox;
    private JButton deleteGroupBtn;
    private JPanel mainPanel;

    public StudentsView() {

        setVisible(true);
        add(mainPanel);
        mainPanel.setPreferredSize(new Dimension(940, 680));
    }

    public JButton getDeleteGroupBtn() {
        return deleteGroupBtn;
    }

    public JTable getStudentsTable() {
        return studentsTable;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JComboBox getGroupBox() {
        return groupBox;
    }

    public JTextField getGroupNameField() {
        return groupNameField;
    }

    public JButton getAddStudentBtn() {
        return addStudentBtn;
    }

    public JButton getRemoveStudentBtn() {
        return removeStudentBtn;
    }

    public JButton getEditStudentBtn() {
        return editStudentBtn;
    }

    public JButton getCreateTableBtn() {
        return createTableBtn;
    }

    public JButton getSaveTableBtn() {
        return saveTableBtn;
    }

    public JTextField getSurnameField() {
        return surnameField;
    }

    public JTextField getMiddleNameField() {
        return middleNameField;
    }
}
