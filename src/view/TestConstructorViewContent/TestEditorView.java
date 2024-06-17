package view.TestConstructorViewContent;

import javax.swing.*;

public class TestEditorView extends JPanel{
    private JTabbedPane ContentPanel;
    private JPanel questionPanel;
    private JPanel singleSelPanel;
    private JButton deleteAnswerBtn;
    private JButton addAnswerBtn;
    private JEditorPane editorPane;
    private JButton saveBtn;
    private JButton addPicBtn;
    private JTextField questionNameField;
    private JList questionList;
    private JPanel mainPanel;
    private JButton addQuestionBtn;
    private JPanel editorPanel;
    private JPanel answerListPanel;
    private JPanel answersPanel;
    private JPanel answerPanel;
    private JButton deleteQuestionBtn;
    private JScrollPane answerScrollPane;
    private JPanel myPanel;
    private JTextField scoreField;

    public JTextField getScoreField() {
        return scoreField;
    }

    public JTabbedPane getContentPanel() {
        return ContentPanel;
    }

    public JPanel getMyPanel() {
        return myPanel;
    }

    public JScrollPane getAnswerScrollPane() {
        return answerScrollPane;
    }

    public void setAnswerScrollPane(JScrollPane answerScrollPane) {
        this.answerScrollPane = answerScrollPane;
    }

    public JButton getDeleteQuestionBtn() {
        return deleteQuestionBtn;
    }

    public JPanel getSingleSelPanel() {
        return singleSelPanel;
    }

    public JTextField getQuestionNameField() {
        return questionNameField;
    }

    public JButton getAddQuestionBtn() {
        return addQuestionBtn;
    }

    public JPanel getEditorPanel() {
        return editorPanel;
    }

    public JPanel getAnswerPanel() {
        return answerPanel;
    }

    public JButton getDeleteAnswerBtn() {
        return deleteAnswerBtn;
    }

    public JButton getAddAnswerBtn() {
        return addAnswerBtn;
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    public JButton getSaveBtn() {
        return saveBtn;
    }

    public JButton getAddPicBtn() {
        return addPicBtn;
    }

    public JList getQuestionList() {
        return questionList;
    }

    public TestEditorView(){
        add(mainPanel);
        setVisible(true);
    }


}
