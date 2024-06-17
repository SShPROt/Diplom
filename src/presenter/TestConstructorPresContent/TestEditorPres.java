package presenter.TestConstructorPresContent;

import model.TestConstructorModelContent.TestEditorModel;
import model.TestConstructorModelContent.TestParamsModel;
import view.TestConstructorViewContent.TestEditorView;
import view.TestConstructorViewContent.TestParamsView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestEditorPres {

    private TestEditorModel model;
    private TestEditorView view;
    private JEditorPane mainEditorPane;
    private JList questionList;
    private JPanel panelContainer;
    private ButtonGroup buttonGroup = new ButtonGroup();

    private static List<JPanel> answerPanels = new ArrayList<>();
    public TestEditorPres(TestEditorView view, TestEditorModel model){
        this.view = view;
        this.model = model;
        this.mainEditorPane = view.getEditorPane();
        questionList = view.getQuestionList();
        view.getEditorPanel().setVisible(false);
        view.setName(model.getSelectedTestName());
        panelContainer = view.getMyPanel();

        TestParamsView testParamsView = new TestParamsView();
        TestParamsModel testParamsModel = new TestParamsModel(model.getConnection(), model.getTestsList(), model.getSelectedTestIndex());
        TestParamsPres testParamsPres = new TestParamsPres(testParamsView, testParamsModel);
        view.getContentPanel().add(testParamsView);

        panelContainer.setLayout(new BoxLayout(panelContainer, BoxLayout.Y_AXIS));

        questionList.setModel(model.getMyModel());
        if(!model.fillList()){
            JOptionPane.showMessageDialog(view, model.getInformation());
        }
        if(questionList.getModel().getSize() != 0){
            view.getEditorPanel().setVisible(true);
            loadQuestionData();
            questionList.setSelectedIndex(0);
            view.getQuestionNameField().setText(questionList.getSelectedValue().toString());
        }

        view.getAddPicBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage(mainEditorPane);
            }
        });

        view.getSaveBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String htmlContent = mainEditorPane.getText();
                String mainHtmlContent;
                StringBuilder answerHtmlContents;
                String selectedRadioIndex = "";
                int score = Integer.parseInt(view.getScoreField().getText());
                try {
                    mainHtmlContent = model.convertImagesToBase64(htmlContent);
                    answerHtmlContents = new StringBuilder();
                    switch(model.getQuestionType()) {
                        case "single": {
                            for (int i = 0; i < answerPanels.size(); i++) {
                                JPanel panel = answerPanels.get(i);
                                JEditorPane editorPane = (JEditorPane) panel.getClientProperty("editorPane");
                                htmlContent = model.convertImagesToBase64(editorPane.getText());
                                answerHtmlContents.append(htmlContent).append("\n");
                                JRadioButton selectButton = (JRadioButton) panel.getClientProperty("selectButton");
                                if (selectButton.isSelected()) {
                                    selectedRadioIndex += ++i;
                                    i--;
                                }
                            }
                            break;
                        }
                        case "multiple":{
                            for (int i = 0; i < answerPanels.size(); i++) {
                                JPanel panel = answerPanels.get(i);
                                JEditorPane editorPane = (JEditorPane) panel.getClientProperty("editorPane");
                                htmlContent = model.convertImagesToBase64(editorPane.getText());
                                answerHtmlContents.append(htmlContent).append("\n");
                                JCheckBox selectButton = (JCheckBox) panel.getClientProperty("selectButton");
                                if (selectButton.isSelected()) {
                                    selectedRadioIndex += ++i + " ";
                                    i--;
                                }
                            }
                            break;
                        }
                        case "written":{
                            JPanel panel = answerPanels.get(0);
                            JTextField answerField = (JTextField) panel.getClientProperty("answerField");
                            selectedRadioIndex = answerField.getText();
                            answerHtmlContents.append("NULL");
                        }
                    }
                    if(selectedRadioIndex.isEmpty())
                        selectedRadioIndex = null;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, model.getInformation());
                    return;
                }
                if(!model.saveToDatabase(mainHtmlContent, answerHtmlContents.toString(),selectedRadioIndex, view.getQuestionNameField().getText(), score)) {
                    JOptionPane.showMessageDialog(view, model.getInformation());
                    return;
                }
                model.fillList();
            }
        });

        questionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                model.setSelectedQuestionIndex(questionList.getSelectedIndex());
                if(!questionList.getSelectedValue().toString().equals("Новый вопрос")) {
                    view.getEditorPanel().setVisible(true);
                    if(model.getQuestionType().equals("written")){
                        view.getAddAnswerBtn().setVisible(false);
                    }
                    else{
                        view.getAddAnswerBtn().setVisible(true);
                    }
                    mainEditorPane.setText("");
                    while(!answerPanels.isEmpty()){
                        panelContainer.remove(answerPanels.get(0));
                        answerPanels.remove(0);
                    }
                    loadQuestionData();
                    view.getQuestionNameField().setText(questionList.getSelectedValue().toString());
                }
                else {
                    view.getEditorPanel().setVisible(true);
                    mainEditorPane.setText("");
                    view.getQuestionNameField().setText("Новый вопрос");
                    view.getScoreField().setText("1");
                    while(!answerPanels.isEmpty()){
                        panelContainer.remove(answerPanels.get(0));
                        answerPanels.remove(0);
                    }
                    if(model.getQuestionType().equals("written")){
                        view.getAddAnswerBtn().setVisible(false);
                        addAnswerPanel("");
                    }
                    else {
                        view.getAddAnswerBtn().setVisible(true);
                    }
                    panelContainer.revalidate();
                    panelContainer.repaint();
                }

            }
        });

        view.getAddQuestionBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel chooseType = new JPanel(new GridLayout(3,1));
                JRadioButton single = new JRadioButton("Одиночный выбор");
                JRadioButton multiple = new JRadioButton("Множественный выбор");
                JRadioButton written = new JRadioButton("Письменный ответ");
                ButtonGroup typeButtons = new ButtonGroup();
                typeButtons.add(single);
                typeButtons.add(multiple);
                typeButtons.add(written);
                chooseType.add(single);
                chooseType.add(multiple);
                chooseType.add(written);

                int result = JOptionPane.showConfirmDialog(view, chooseType, "Укажите тип вопроса", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                if(result == JOptionPane.OK_OPTION){
                    if(single.isSelected())
                       if (!model.addNewQuestion("single"))
                            JOptionPane.showMessageDialog(view, model.getInformation());
                    if(multiple.isSelected())
                        if(!model.addNewQuestion("multiple"))
                            JOptionPane.showMessageDialog(view, model.getInformation());
                    if(written.isSelected())
                        if(!model.addNewQuestion("written"))
                            JOptionPane.showMessageDialog(view, model.getInformation());
                }
            }
        });

        view.getDeleteQuestionBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(questionList.getSelectedIndex() != -1) {
                    if (!model.deleteQuestion()) {
                        JOptionPane.showMessageDialog(view, model.getInformation());
                        return;
                    }
                    view.getEditorPanel().setVisible(false);
                }
                else {
                    JOptionPane.showMessageDialog(view, "Выберите вопрос для удаления");
                }
            }
        });

        view.getAddAnswerBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAnswerPanel("", false, model.getQuestionType());
            }
        });
    }

    private void openImage(JEditorPane editorPane) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filterJpg = new FileNameExtensionFilter("JPG", "jpg", "jpeg");
        FileNameExtensionFilter filterPng = new FileNameExtensionFilter("PNG", "png");
        FileNameExtensionFilter filterGif = new FileNameExtensionFilter("GIF", "gif");
        fileChooser.setFileFilter(filterJpg);
        fileChooser.setFileFilter(filterPng);
        fileChooser.setFileFilter(filterGif);
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            JPanel sizes = new JPanel(new GridLayout(2,2));
            JTextField widthField = new JTextField(10);
            JTextField heightField = new JTextField(10);
            JLabel widthText = new JLabel("Ширина");
            JLabel heightText = new JLabel("Высота");

            sizes.add(widthText);
            sizes.add(widthField);
            sizes.add(heightText);
            sizes.add(heightField);

            try {
                int result = JOptionPane.showConfirmDialog(view, sizes, "Укажите размеры", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                switch (result) {
                    case JOptionPane.OK_OPTION: {
                        int width = Integer.parseInt(widthField.getText());
                        int height = Integer.parseInt(heightField.getText());
                        String imagePath = selectedFile.getAbsolutePath();
                        HTMLEditorKit editorKit = (HTMLEditorKit) editorPane.getEditorKit();
                        editorKit.insertHTML((HTMLDocument) editorPane.getDocument(), editorPane.getDocument().getLength(), "<br>\n<img src='file:" + imagePath + "' width=\"" + width + "\" height=\"" + height + "\"><br>\n", 0, 0, null);
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadQuestionData(){
        try {
            String[] contents = model.loadFromDatabase();
            String mainHtmlContent = model.convertBase64ToImages(contents[0]);
            String type = contents[3];
            view.getScoreField().setText(contents[4]);
            mainEditorPane.setText(mainHtmlContent);
            if(type.equals("written")) {
                String answerContents = contents[2];
                addAnswerPanel(answerContents);
            }
            else {
                String[] answerContents = contents[1].split("<html>");
                String[] correctAnswer = contents[2].split(" ");
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
        editorPane.setContentType("text/html");
        editorPane.setText(initialContent);
        JButton insertButton = new JButton();
        JButton deleteButton = new JButton();
        insertButton.setIcon(new ImageIcon("src/+.png"));
        deleteButton.setIcon(new ImageIcon("src/-.png"));
        insertButton.setBackground(new Color(255,255,255));
        deleteButton.setBackground(new Color(255,255,255));

        panel.putClientProperty("editorPane", editorPane);
        panel.putClientProperty("selectButton", selectButton);
        panel.setPreferredSize(new Dimension(660, 100));
        panel.setMinimumSize(new Dimension(660, 100));
        panel.setMaximumSize(new Dimension(660, 100));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.add(insertButton);
        buttonPanel.add(deleteButton);

        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(new JScrollPane(editorPane), BorderLayout.CENTER);
        editorPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(selectButton, BorderLayout.WEST);
        panel.add(editorPanel, BorderLayout.CENTER);

        if (isSelected) {
            selectButton.setSelected(true);
        }

        answerPanels.add(panel);
        panelContainer.add(panel);
        panelContainer.revalidate();
        panelContainer.repaint();


        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage(editorPane);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panelContainer.remove(panel);
                answerPanels.remove(panel);
                if(buttonGroup.getButtonCount() != 0)
                    buttonGroup.remove(selectButton);
                panelContainer.revalidate();
                panelContainer.repaint();
            }
        });
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
}
