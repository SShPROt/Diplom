package view;

import javax.swing.*;

public class StartPageView extends JFrame {
    private JPanel cardPanel;
    private JPanel authorizationPanel;
    private JTextField surnameField;
    private JTextField nameField;
    private JTextField middleNameField;
    private JButton connectBtn;
    private JPanel waitingPanel;
    private JPanel resultPanel;
    private JButton exitBtn;
    private JLabel resultLabel;
    private JLabel scoreLabel;
    private JLabel gradeLabel;
    private JLabel timeLabel;
    private JTextField ipField;

    public JTextField getIpField() {
        return ipField;
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public JButton getExitBtn() {
        return exitBtn;
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }

    public JLabel getScoreLabel() {
        return scoreLabel;
    }

    public JLabel getGradeLabel() {
        return gradeLabel;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public JTextField getSurnameField() {
        return surnameField;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getMiddleNameField() {
        return middleNameField;
    }

    public JButton getConnectBtn() {
        return connectBtn;
    }

    public StartPageView() {
        setContentPane(cardPanel);
        setVisible(true);
        setSize(940, 680);
        setTitle("Клиент");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
