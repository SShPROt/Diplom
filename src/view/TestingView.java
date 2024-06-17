package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestingView extends JPanel{
    private JTable studentTable;
    private JComboBox yearBox;
    private JComboBox groupBox;
    private JButton startBtn;
    private JComboBox testBox;
    private JButton confirmBtn;
    private JPanel mainPanel;
    private JPanel cardPanel;
    private JPanel startPanel;
    private JLabel statusLabel;
    private JPanel testPanel;
    private JTable testTable;
    private JButton saveResultsBtn;

    public JButton getSaveResultsBtn() {
        return saveResultsBtn;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public JTable getTestTable() {
        return testTable;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public JTable getStudentTable() {
        return studentTable;
    }

    public JComboBox getYearBox() {
        return yearBox;
    }

    public JComboBox getGroupBox() {
        return groupBox;
    }

    public JButton getStartBtn() {
        return startBtn;
    }

    public JComboBox getTestBox() {
        return testBox;
    }

    public JButton getConfirmBtn() {
        return confirmBtn;
    }

    public TestingView(){
        setVisible(true);
        add(mainPanel);
    }
}
