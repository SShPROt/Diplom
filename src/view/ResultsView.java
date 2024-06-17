package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ResultsView extends JPanel{
    private JPanel mainPanel;
    private JComboBox yearBox;
    private JComboBox groupBox;
    private JComboBox testBox;
    private JTable resultTable;

    public JComboBox getYearBox() {
        return yearBox;
    }

    public JComboBox getGroupBox() {
        return groupBox;
    }

    public JComboBox getTestBox() {
        return testBox;
    }

    public JTable getResultTable() {
        return resultTable;
    }

    public ResultsView() {
        add(mainPanel);
        setVisible(true);
    }
}
