package presenter.TestConstructorPresContent;

import model.TestConstructorModelContent.TestParamsModel;
import view.TestConstructorViewContent.TestParamsView;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class TestParamsPres {
    private TestParamsView view;
    private TestParamsModel model;
    private JFormattedTextField fiveGradeField;
    private JFormattedTextField fourGradeField;
    private JFormattedTextField threeGradeField;
    private JFormattedTextField twoGradeField;
    private JFormattedTextField timeField;
    public TestParamsPres(TestParamsView view, TestParamsModel model){
        this.view = view;
        this.model = model;
        fiveGradeField = view.getFirstGradeField();
        fourGradeField = view.getSecondGradeField();
        threeGradeField = view.getThirdGradeField();
        twoGradeField = view.getFourthGradeField();
        timeField = view.getTimeField();

        view.setName("Дополнительно");

        timeField.setColumns(5);
        fiveGradeField.setColumns(5);
        fourGradeField.setColumns(5);
        threeGradeField.setColumns(5);
        twoGradeField.setColumns(5);

        fillFields();

        timeField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                validateTime();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateTime();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateTime();
            }
        });



        view.getTimeCheckBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeField.setEnabled(view.getTimeCheckBox().isSelected());
            }
        });
        view.getSaveBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] fiveGradeStr, fourGradeStr, threeGradeStr, twoGradeStr;
                fiveGradeStr = fiveGradeField.getText().split("%");
                fourGradeStr = fourGradeField.getText().split("%");
                threeGradeStr = threeGradeField.getText().split("%");
                twoGradeStr = twoGradeField.getText().split("%");
                int fiveGrade = Integer.parseInt(fiveGradeStr[0]);
                int fourGrade = Integer.parseInt(fourGradeStr[0]);
                int threeGrade = Integer.parseInt(threeGradeStr[0]);
                int twoGrade = Integer.parseInt(twoGradeStr[0]);
                String grades = fiveGradeStr[0] + "," + fourGradeStr[0] + "," + threeGradeStr[0] + "," + twoGradeStr[0];
                String time;
                if(view.getTimeCheckBox().isSelected()) {
                    timeField.setEnabled(true);
                    time = timeField.getText() + ":00";
                }
                else
                    time = "00:00:00";

                if((fiveGrade > fourGrade) && (fourGrade > threeGrade) && (threeGrade > twoGrade)) {
                    if (!model.saveParamsOnDB(grades, time))
                        JOptionPane.showConfirmDialog(view, model.getInformation());
                }
                else
                    JOptionPane.showConfirmDialog(view, "Нарушена логика оценивания. Проверьте правильно ли указаны проценты");
            }
        });
    }
    private void setMaskOnGradeField(JFormattedTextField gradeField, String percent){
        MaskFormatter phoneFormatter = null;
        try {
            phoneFormatter = new MaskFormatter("##%");
            phoneFormatter.setPlaceholderCharacter('0');
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        phoneFormatter.install(gradeField);
        gradeField.setText(percent + "%");
    }
    private void setMaskOnTimeField(String time){
        MaskFormatter timeFormatter = null;
        try {
            timeFormatter = new MaskFormatter("##:##");
            timeFormatter.setPlaceholderCharacter('0');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeFormatter.install(timeField);
        timeField.setText(time);
    }

    private void validateTime() {
        SwingUtilities.invokeLater(() -> {
            String text = timeField.getText();
            String[] parts = text.split(":");
            if (parts.length == 2) {
                try {
                    int minutes = Integer.parseInt(parts[1]);
                    if (minutes >= 60) {
                        timeField.setText(parts[0] + ":59");
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });
    }

    private void fillFields(){
        ResultSet res = model.getDataFromDb();
        try {
            if(res.next()){
                String[] grades = res.getString("gradeSys").split(",");
                String fiveGrade = grades[0],
                        fourGrade = grades[1],
                        threeGrade = grades[2],
                        twoGrade = grades[3];
                String[] time = res.getString("test_time").split(":");;
                if(!res.getString("test_time").equals("00:00:00")) {
                    view.getTimeCheckBox().setSelected(true);
                    view.getTimeField().setEnabled(true);
                }
                else
                    view.getTimeCheckBox().setSelected(false);

                setMaskOnGradeField(fiveGradeField, fiveGrade);
                setMaskOnGradeField(fourGradeField, fourGrade);
                setMaskOnGradeField(threeGradeField,threeGrade);
                setMaskOnGradeField(twoGradeField, twoGrade);
                setMaskOnTimeField(time[0] + ":" + time[1]);
            }
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(view, model.getInformation());
        }

    }
}
