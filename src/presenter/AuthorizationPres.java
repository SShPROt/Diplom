package presenter;

import model.AuthorizationModel;
import other.Crypto;
import view.AuthorizationView;
import view.MainMenuView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorizationPres {
    private AuthorizationModel model;
    private AuthorizationView view;
    private MainMenuView mainMenuView;
    JTextField ipField;
    JTextField portField;
    JTextField loginField;
    JPasswordField passwordDbField;
    JPasswordField passwordAccField;
    JComboBox departmentComboBox;
    JTextField surnameField;
    JTextField nameField;
    JTextField middleField;
    JCheckBox adminCheckBox;
    JCheckBox showPswAccCheckBox;
    private boolean admin = false;

    private void autoFillFields(){
        String ip = model.getDefaultData().getIp();
        String port = model.getDefaultData().getPort();
        ipField.setText(ip);
        portField.setText(port);

    }
    private void fillComboBox(){
        ResultSet rs = model.getDepartmentsFromDb();
        try {
            if(rs.isBeforeFirst())
                while (rs.next()) {
                    departmentComboBox.addItem(rs.getString(2));
                }
            else
                JOptionPane.showMessageDialog(view, "Не найдено ни одной кафедры в системе. Сначала необходимо создать хотя бы одну.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Найдено 0 таблиц");
        }
    }
    public AuthorizationPres(AuthorizationModel model, AuthorizationView view, MainMenuView mainMenuView){
        this.model = model;
        this.view = view;
        this.mainMenuView = mainMenuView;
        ipField = view.getIpField();
        portField = view.getPortField();
        loginField = view.getLoginField();
        passwordDbField = view.getPasswordDbField();
        departmentComboBox = view.getDepartmentComboBox();
        surnameField = view.getSurnameField();
        nameField = view.getNameField();
        middleField = view.getMiddleField();
        passwordAccField = view.getPasswordAccField();
        adminCheckBox = view.getAdminCheckBox();
        showPswAccCheckBox = view.getShowPswAccCheckBox();

        view.getDefaultValueBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autoFillFields();
            }
        });

        view.getShowPswDbCheckBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(view.getShowPswDbCheckBox().isSelected())
                    passwordDbField.setEchoChar((char)0);
                else passwordDbField.setEchoChar('•');
            }
        });

        view.getConnectBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip, port, login, password;
                if(admin){
                    ip = ipField.getText();
                    port = portField.getText();
                    login = loginField.getText();
                    password = passwordDbField.getText();
                    if(login.equals(model.getDefaultData().getLoginAdmin())) {
                        if (model.attemptConnect(ip, port, login, password)) {
                            JOptionPane.showMessageDialog(view, "Права админа получены, но доступ к большинству разделов всё ещё не получен. Для получения прав авторизуйтесь, как преподаватель.");
                            mainMenuView.getAdminOpportunitiesBtn().setVisible(true);
                            view.setContentPane(view.getAutorizationPanel());
//                            fillComboBox();
//                            view.revalidate();
//                            view.repaint();
                            view.dispose();
                        } else
                            JOptionPane.showMessageDialog(view, "Не удалось подключиться. Сервер недоступен либо указаны неверные данные");
                    }
                    else
                        JOptionPane.showMessageDialog(view, "Неверный логин админа");
                }
                else {
                    ip = ipField.getText();
                    port = portField.getText();
                    login = model.getDefaultData().getLoginTeacher();
                    password = model.getDefaultData().getPasswordTeacher();
                    if (model.attemptConnect(ip, port, login, password)) {
                        view.setContentPane(view.getAutorizationPanel());
                        mainMenuView.getAdminOpportunitiesBtn().setVisible(false);
                        fillComboBox();
                        view.revalidate();
                        view.repaint();
                    } else
                        JOptionPane.showMessageDialog(view, "Не удалось подключиться. Сервер недоступен либо указаны неверные данные");
                }

            }
        });

        view.getEnterButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText(),
                        surname = surnameField.getText(),
                        middleName = middleField.getText(),
                        password = passwordAccField.getText();
                int departmentIndex = departmentComboBox.getSelectedIndex();
                if(departmentIndex == - 1){
                    JOptionPane.showMessageDialog(view, "Список кафедр пуст. Необходимо создать новую.");
                    return;
                }
                model.setDepartment(departmentIndex);
                if(model.compareUser(surname, name, middleName, password)){
                    JOptionPane.showMessageDialog(view, model.getInformation());

                    mainMenuView.getScreenPanel().removeAll();
                    mainMenuView.getScreenPanel().add(mainMenuView.getWelcomePanel());
                    mainMenuView.getScreenPanel().revalidate();
                    mainMenuView.getScreenPanel().repaint();

                    mainMenuView.getAuthorizationBtn1().setText("Сменить пользователя");
                    mainMenuView.getAuthorizationBtn2().setVisible(false);
                    mainMenuView.getText1().setVisible(false);
                    mainMenuView.getText2().setText("Вы успешно авторизовались как: " + surname + " " + name + " " + middleName);
                    mainMenuView.getText3().setText(surname + " " + name + " " + middleName + " " + departmentComboBox.getSelectedItem().toString());

                    view.dispose();
                }
                else
                    JOptionPane.showMessageDialog(view, model.getInformation());
            }
        });

        adminCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(adminCheckBox.isSelected()){
                    loginField.setEnabled(true);
                    passwordDbField.setEnabled(true);
                    admin = true;
                }
                else{
                    loginField.setEnabled(false);
                    passwordDbField.setEnabled(false);
                    admin = false;
                }
            }
        });
        showPswAccCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(view.getShowPswAccCheckBox().isSelected())
                    passwordAccField.setEchoChar((char)0);
                else passwordAccField.setEchoChar('•');
            }
        });
    }


}
