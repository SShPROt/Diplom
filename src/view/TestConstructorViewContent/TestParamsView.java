package view.TestConstructorViewContent;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

public class TestParamsView extends JPanel{
    private JCheckBox timeCheckBox;
    private JFormattedTextField timeField;
    private JPanel mainPanel;
    private JFormattedTextField firstGradeField;
    private JButton saveBtn;
    private JFormattedTextField secondGradeField;
    private JFormattedTextField thirdGradeField;
    private JFormattedTextField fourthGradeField;

    public JCheckBox getTimeCheckBox() {
        return timeCheckBox;
    }

    public JFormattedTextField getTimeField() {
        return timeField;
    }

    public JFormattedTextField getFirstGradeField() {
        return firstGradeField;
    }

    public JFormattedTextField getSecondGradeField() {
        return secondGradeField;
    }

    public JFormattedTextField getThirdGradeField() {
        return thirdGradeField;
    }

    public JFormattedTextField getFourthGradeField() {
        return fourthGradeField;
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }

    public TestParamsView(){
        add(mainPanel);
        setVisible(true);
    }
}
