package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthorizationView extends JDialog {
    private JPanel mainPanel;
    private JTextField ipField;
    private JPasswordField passwordDbField;
    private JTextField portField;
    private JTextField loginField;
    private JButton connectBtn;
    private JButton defaultValueBtn;
    private JCheckBox showPswDbCheckBox;
    private JComboBox departmentComboBox;
    private JTextField surnameField;
    private JTextField nameField;
    private JTextField middleField;
    private JButton enterButton;
    private JPanel autorizationPanel;
    private JPanel connectDbPanel;
    private JPasswordField passwordAccField;
    private JCheckBox showPswAccCheckBox;
    private JCheckBox adminCheckBox;

    public JPanel getAutorizationPanel() {
        return autorizationPanel;
    }

    public AuthorizationView(){
        setSize(400, 300);
        setTitle("Авторизация");
        setResizable(false);
        setContentPane(connectDbPanel);
        setVisible(true);

        surnameField.setText("Карамышева");
        nameField.setText("Надежда");
        middleField.setText("Сергеевна");
    }

    public JPasswordField getPasswordAccField() {
        return passwordAccField;
    }

    public JCheckBox getShowPswAccCheckBox() {
        return showPswAccCheckBox;
    }

    public JCheckBox getAdminCheckBox() {
        return adminCheckBox;
    }

    public JTextField getIpField() {
        return ipField;
    }

    public JPasswordField getPasswordDbField() {
        return passwordDbField;
    }

    public JTextField getPortField() {
        return portField;
    }

    public JTextField getLoginField() {
        return loginField;
    }

    public JButton getConnectBtn() {
        return connectBtn;
    }

    public JButton getDefaultValueBtn() {
        return defaultValueBtn;
    }

    public JCheckBox getShowPswDbCheckBox() {
        return showPswDbCheckBox;
    }

    public JComboBox getDepartmentComboBox() {
        return departmentComboBox;
    }

    public JTextField getSurnameField() {
        return surnameField;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JTextField getMiddleField() {
        return middleField;
    }

    public JButton getEnterButton() {
        return enterButton;
    }
}
