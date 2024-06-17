package view;

import javax.swing.*;

public class AdminTabView extends JPanel{
    private JPanel mainPanel;
    private JButton yearsBtn;
    private JButton departmentsBtn;
    private JButton teachersBtn;
    private JPanel screenPanel;

    public JPanel getScreenPanel() {
        return screenPanel;
    }

    public JButton getYearsBtn() {
        return yearsBtn;
    }

    public JButton getDepartmentsBtn() {
        return departmentsBtn;
    }

    public JButton getTeachersBtn() {
        return teachersBtn;
    }

    public AdminTabView() {
        add(mainPanel);
        setSize(980, 720);
        setVisible(true);


    }
}
