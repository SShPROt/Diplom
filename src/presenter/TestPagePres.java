package presenter;

import model.TestPageModel;
import view.TestPageView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TestPagePres {
    private TestPageView view;
    private TestPageModel model;
    private JEditorPane editorPane;
    private Timer timer;
    private JLabel timerLabel;
    private static List<JPanel> answerPanels = new ArrayList<>();
    private ButtonGroup buttonGroup;
    private int questionIndex = 0;
    private long startTime;
    private long countdownTime;
    private JPanel panelContainer;
    private ArrayList <String> answers = new ArrayList<>();
    private int currentScore = 0;
    private int score = 0;
    private long elapsed;
    private int grade;
    private String formattedTime;

    public String getFormattedTime() {
        return formattedTime;
    }

    public int getGrade() {
        return grade;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getScore() {
        return score;
    }

    public TestPagePres(TestPageView view, TestPageModel model){
        this.view = view;
        this.model = model;
        this.editorPane = view.getEditorPane();
        this.timerLabel = view.getTimer();
        panelContainer = view.getAnswerPanel();
        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));
        fillFields(0);
        view.getPrevBtn().setEnabled(false);
        view.getQuestionNum().setText(1 + "/" + model.getCountQuestion());

        for (int i = 0; i < model.getCountQuestion(); i++) {
            answers.add(questionIndex, "");
        }

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimer();
            }
        });
        startTimer();

        view.getPrevBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMyAnswer();
                view.getNextBtn().setText("Следующий");
                while(!answerPanels.isEmpty()){
                    panelContainer.remove(answerPanels.get(0));
                    answerPanels.remove(0);
                }
                questionIndex--;
                view.getQuestionNum().setText(questionIndex+1 + "/" + model.getCountQuestion());
                fillFields(questionIndex);
                if(questionIndex == 0)
                    view.getPrevBtn().setEnabled(false);
            }
        });
        view.getNextBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMyAnswer();
                if(questionIndex == model.getCountQuestion()-1){
                    for (String answer : answers)
                        if (answer.isEmpty()) {
                            JOptionPane.showMessageDialog(view, "Не на все вопросы получен ответ.");
                            return;
                        }
                    calculateScore();
                    sendResult();
                }
                else {
                    while (!answerPanels.isEmpty()) {
                        panelContainer.remove(answerPanels.get(0));
                        answerPanels.remove(0);
                    }
                    questionIndex++;
                    view.getQuestionNum().setText(questionIndex+1 + "/" + model.getCountQuestion());
                    fillFields(questionIndex);
                    if (questionIndex == model.getCountQuestion() - 1)
                        view.getNextBtn().setText("Закончить");
                    else
                        view.getNextBtn().setText("Следующий");
                }
            }
        });
    }

    public void addMyAnswer(){
        String selectedRadioIndex = "";
        view.getPrevBtn().setEnabled(true);

        String type = model.getQuestionType(questionIndex);
        switch (type){
            case "single":
                for (int i = 0; i < answerPanels.size(); i++) {
                    JPanel panel = answerPanels.get(i);
                    JRadioButton selectButton = (JRadioButton) panel.getClientProperty("selectButton");
                    if (selectButton.isSelected()) {
                        selectedRadioIndex += ++i;
                        i--;
                    }
                }
                break;
            case "multiple":
                for (int i = 0; i < answerPanels.size(); i++) {
                    JPanel panel = answerPanels.get(i);
                    JCheckBox selectButton = (JCheckBox) panel.getClientProperty("selectButton");
                    if (selectButton.isSelected()) {
                        selectedRadioIndex += ++i + " ";
                        i--;
                    }
                }
                break;
            case "written":
                JPanel panel = answerPanels.get(0);
                JTextField answerField = (JTextField) panel.getClientProperty("answerField");
                selectedRadioIndex = answerField.getText();
        }
        answers.set(questionIndex, selectedRadioIndex);
    }

    private void fillFields(int index){
        String[] content = model.getQuestion(index);
        try {
            String mainEditor = model.convertBase64ToImages(content[0]);
            editorPane.setText(mainEditor);
            String type = content[3];
            switch (type){
                case "single":
                    view.getTaskLabel().setText("Выберите один из ответов");
                case "multiple": {
                    if (type.equals("multiple"))
                        view.getTaskLabel().setText("Выберите один или несколько правильных ответов");
                    String[] answerContents = content[1].split("<html>");
                    String[] correctAnswer;
                    if(answers.size() == questionIndex)
                        correctAnswer = new String[]{"0"};
                    else if(answers.get(questionIndex).isEmpty()) {
                        correctAnswer = new String[]{"0"};
                    }
                    else
                        correctAnswer = answers.get(questionIndex).split(" ");
                    int [] correctAnswerIndex = new int[correctAnswer.length];
                    for(int i = 0; i < correctAnswer.length; i++){
                        correctAnswerIndex[i] = Integer.parseInt(correctAnswer[i]);
                    }
                    buttonGroup = new ButtonGroup();
                    int j = 0;
                    for (int i = 1; i < answerContents.length; i++) {
                        boolean isSelected = false;
                        String answerHtmlContent = answerContents[i];
                        if (!answerHtmlContent.trim().isEmpty()) {
                            if(i == correctAnswerIndex[j]){
                                isSelected = true;
                                if(!(j + 1 == correctAnswerIndex.length))
                                    j++;
                            }
                            String updatedHtmlContent = model.convertBase64ToImages(answerHtmlContent);
                            addAnswerPanel(updatedHtmlContent, isSelected, type);
                        }
                    }
                    break;
                }
                case "written": {
                    view.getTaskLabel().setText("Впишите свой ответ в поле ниже");
                    if(answers.size() == questionIndex)
                        addAnswerPanel("");
                    else if(answers.get(questionIndex).isEmpty()) {
                        addAnswerPanel("");
                    }
                    else
                        addAnswerPanel(answers.get(questionIndex));
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addAnswerPanel(String initialContent, boolean isSelected, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        AbstractButton selectButton;
        if(type.equals("single")) {
            selectButton = new JRadioButton();
            buttonGroup.add(selectButton);
        }else {
            selectButton = new JCheckBox();
        }
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText(initialContent);
        editorPane.setFocusable(false);

        panel.putClientProperty("editorPane", editorPane);
        panel.putClientProperty("selectButton", selectButton);
        panel.setPreferredSize(new Dimension(740, 100));
        panel.setMinimumSize(new Dimension(740, 100));
        panel.setMaximumSize(new Dimension(740, 100));

        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(new JScrollPane(editorPane), BorderLayout.CENTER);

        panel.add(selectButton, BorderLayout.WEST);
        panel.add(editorPanel, BorderLayout.CENTER);

        if (isSelected) {
            selectButton.setSelected(true);
        }

        answerPanels.add(panel);
        panelContainer.add(panel);
        panelContainer.revalidate();
        panelContainer.repaint();
    }

    private void addAnswerPanel(String initialContent){
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(660, 100));
        panel.setMinimumSize(new Dimension(660, 100));
        panel.setMaximumSize(new Dimension(660, 100));
        JTextField answerField = new JTextField(50);
        answerField.setText(initialContent);
        panel.putClientProperty("answerField", answerField);
        panel.add(answerField, BorderLayout.CENTER);
        answerPanels.add(panel);
        panelContainer.add(panel);
        panelContainer.revalidate();
        panelContainer.repaint();
    }

    private void startTimer() {
        int time = model.getTime();
        if (time == 0) {
            countdownTime = -1; // Indicate stopwatch mode
        } else {
            countdownTime = time;
        }
        startTime = System.currentTimeMillis();
        timer.start();
    }

    private void updateTimer() {
        elapsed = (System.currentTimeMillis() - startTime) / 1000;
        if (countdownTime > 0) {
            long remaining = countdownTime - elapsed;
            if (remaining <= 0) {
                timer.stop();
                timerLabel.setText("00:00:00");
                calculateScore();
                sendResult();
                JOptionPane.showMessageDialog(view, "Время вышло!");
            } else {
                timerLabel.setText(formatTime(remaining));
            }
        } else {
            timerLabel.setText(formatTime(elapsed));
        }
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    public void calculateScore(){
        timer.stop();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).equals(model.getAnswer(i)))
                currentScore += model.getScore(i);
            score += model.getScore(i);
        }
    }

    public void sendResult(){
        grade = model.getGrade(currentScore, score);
        model.sendMessage("" + grade);
        model.sendMessage(currentScore + "/" + score);
        long time = elapsed;
        formattedTime = formatTime(time);
        model.sendMessage(formattedTime);
    }
}
