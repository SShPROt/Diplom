package view;

import javax.swing.*;

public class MainMenuView extends JFrame{
    private JPanel mainPanel;
    private JButton studentListBtn;
    private JButton testsBtn;
    private JButton startTestBtn;
    private JButton authorizationBtn1;

    private JButton authorizationBtn2;
    private JLabel text1;
    private JLabel text2;
    private JLabel text3;
    private JPanel screenPanel;
    private JButton checkResultsBtn;
    private JButton adminOpportunitiesBtn;
    private JPanel welcomePanel;

    public JPanel getWelcomePanel() {
        return welcomePanel;
    }

    public JButton getCheckResultsBtn() {
        return checkResultsBtn;
    }

    public JButton getAdminOpportunitiesBtn() {
        return adminOpportunitiesBtn;
    }

    public JButton getStudentListBtn() {
        return studentListBtn;
    }

    public JButton getTestsBtn() {
        return testsBtn;
    }

    public JButton getStartTestBtn() {
        return startTestBtn;
    }

    public JButton getAuthorizationBtn1() {
        return authorizationBtn1;
    }

    public JButton getAuthorizationBtn2() {
        return authorizationBtn2;
    }

    public JLabel getText1() {
        return text1;
    }

    public JLabel getText2() {
        return text2;
    }

    public JLabel getText3() {
        return text3;
    }

    public JPanel getScreenPanel() {
        return screenPanel;
    }

    public MainMenuView(){
        setSize(1280, 720);
        setTitle("Сервер");
        setResizable(false);
        setContentPane(mainPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
