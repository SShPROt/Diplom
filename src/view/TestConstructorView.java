package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TestConstructorView extends JPanel{
    private JPanel mainPanel;
    private JTabbedPane questionsPanel;
    private JList testList;
    private JButton deleteBtn;
    private JButton editBtn;
    private JButton createBtn;
    private JTextField nameField;
    private JButton openBtn;
    private JCheckBox showAllCheckBox;

    public JCheckBox getShowAllCheckBox() {
        return showAllCheckBox;
    }


    public JButton getDeleteBtn() {
        return deleteBtn;
    }

    public JButton getEditBtn() {
        return editBtn;
    }

    public JButton getCreateBtn() {
        return createBtn;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JButton getOpenBtn() {
        return openBtn;
    }

    public JList getTestList() {
        return testList;
    }

    public JTabbedPane getQuestionsPanel() {
        return questionsPanel;
    }

    public TestConstructorView(){
        add(mainPanel);
        mainPanel.setPreferredSize(new Dimension(940, 680));
        setVisible(true);
    }
}
