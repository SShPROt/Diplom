package view;

import javax.swing.*;

public class TestPageView extends JPanel {
    private JPanel testPanel;
    private JPanel questionPanel;
    private JPanel answerPanel;
    private JEditorPane editorPane;
    private JLabel timerType;
    private JLabel timer;
    private JButton prevBtn;
    private JButton nextBtn;
    private JLabel questionNum;
    private JLabel taskLabel;

    public JLabel getQuestionNum() {
        return questionNum;
    }

    public JLabel getTaskLabel() {
        return taskLabel;
    }

    public JButton getPrevBtn() {
        return prevBtn;
    }

    public JButton getNextBtn() {
        return nextBtn;
    }

    public JPanel getAnswerPanel() {
        return answerPanel;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public JLabel getTimerType() {
        return timerType;
    }

    public JLabel getTimer() {
        return timer;
    }

    public TestPageView(){
        add(testPanel);
        setVisible(true);

    }
}
