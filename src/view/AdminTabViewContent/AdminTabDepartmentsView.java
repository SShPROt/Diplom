package view.AdminTabViewContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminTabDepartmentsView extends JPanel{
    private JTable departmentTable;
    private JButton addDepartmentBtn;
    private JButton editDepartmentBtn;
    private JButton deleteDepartmentBtn;
    private JTextField departmentField;
    private JPanel mainPanel;

    public JTable getDepartmentTable() {
        return departmentTable;
    }

    public JButton getAddDepartmentBtn() {
        return addDepartmentBtn;
    }

    public JButton getEditDepartmentBtn() {
        return editDepartmentBtn;
    }

    public JButton getDeleteDepartmentBtn() {
        return deleteDepartmentBtn;
    }

    public JTextField getDepartmentField() {
        return departmentField;
    }

    public AdminTabDepartmentsView() {
        add(mainPanel);
        setVisible(true);
        mainPanel.setPreferredSize(new Dimension(780, 720));
    }
}
