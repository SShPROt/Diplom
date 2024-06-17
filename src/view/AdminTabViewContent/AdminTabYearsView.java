package view.AdminTabViewContent;

import javax.swing.*;
import java.awt.*;

public class AdminTabYearsView extends JPanel{
    private JPanel mainPanel;
    private JTable yearsTable;
    private JTextField yearField;
    private JButton addYearBtn;
    private JButton deleteYearBtn;
    private JButton editYearBtn;

    public JTable getYearsTable() {
        return yearsTable;
    }

    public JTextField getYearField() {
        return yearField;
    }

    public JButton getAddYearBtn() {
        return addYearBtn;
    }

    public JButton getDeleteYearBtn() {
        return deleteYearBtn;
    }

    public JButton getEditYearBtn() {
        return editYearBtn;
    }

    public AdminTabYearsView() {
        add(mainPanel);
        setVisible(true);
        mainPanel.setPreferredSize(new Dimension(780, 720));
    }
}
