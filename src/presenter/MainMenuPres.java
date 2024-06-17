package presenter;

import model.*;
import other.Department;
import other.Year;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainMenuPres {
    AuthorizationModel authorizationModel;
    private static Map<String, Component> cardMap = new HashMap<>();
    private CardLayout cards;

    public MainMenuPres(MainMenuView view, MainMenuModel model){

        view.getStudentListBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Connection connection = model.checkConnection(authorizationModel);
                if(connection != null){
                    if((model.checkAccess(authorizationModel))) {
                        cards = (CardLayout) view.getScreenPanel().getLayout();
                        if (cardContainsPanel("students")) {
                            cards.show(view.getScreenPanel(),"students");
                        }
                        else {
                            int selectedDepartmentIndex = model.getDepartmentIndex(authorizationModel);
                            ArrayList<Department> departmentListFromDb = model.getDepartmentList(authorizationModel);
                            StudentsView studentsView = new StudentsView();
                            StudentsModel studentsModel = new StudentsModel(connection, selectedDepartmentIndex, departmentListFromDb);
                            StudentsPres studentsPres = new StudentsPres(studentsModel, studentsView);
                            addPanelToCardLayout(view.getScreenPanel(), studentsView, "students");
                            cards.show(view.getScreenPanel(),"students");
                        }
                    }else
                        JOptionPane.showMessageDialog(view, "Вы не авторизовались");
                }
                else
                    JOptionPane.showMessageDialog(view, "Для работы в системе необходимо подключиться к БД и авторизоваться");
            }
        });
        view.getAuthorizationBtn1().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorizationView authorizationView = new AuthorizationView();
                authorizationModel = new AuthorizationModel();
                AuthorizationPres authorizationPres = new AuthorizationPres(authorizationModel, authorizationView, view);
            }
        });

        view.getAuthorizationBtn2().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorizationView authorizationView = new AuthorizationView();
                authorizationModel = new AuthorizationModel();
                AuthorizationPres authorizationPres = new AuthorizationPres(authorizationModel, authorizationView, view);
            }
        });

        view.getTestsBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = model.checkConnection(authorizationModel);
                if(connection != null){
                    if((model.checkAccess(authorizationModel))) {
                        cards = (CardLayout) view.getScreenPanel().getLayout();
                        if (cardContainsPanel("constructor")) {
                            cards.show(view.getScreenPanel(),"constructor");
                        }
                        else {
                            int selectedDepartmentIndex = model.getDepartmentIndex(authorizationModel);
                            int selectedTeacherId = model.getTeacherId(authorizationModel);
                            ArrayList<Department> departmentListFromDb = model.getDepartmentList(authorizationModel);
                            TestConstructorModel testConstructorModel = new TestConstructorModel(connection, departmentListFromDb.get(selectedDepartmentIndex).getId(), selectedTeacherId);
                            TestConstructorView testConstructorView = new TestConstructorView();
                            TestConstructorPres testConstructorPres = new TestConstructorPres(testConstructorView, testConstructorModel);
                            addPanelToCardLayout(view.getScreenPanel(), testConstructorView, "constructor");
                            cards.show(view.getScreenPanel(),"constructor");
                        }
                    }else
                        JOptionPane.showMessageDialog(view, "Вы не авторизовались");
                }
                else
                    JOptionPane.showMessageDialog(view, "Для работы в системе необходимо подключиться к БД и авторизоваться");
            }
        });

        view.getCheckResultsBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = model.checkConnection(authorizationModel);
                if(connection != null){
                    if((model.checkAccess(authorizationModel))) {
                        cards = (CardLayout) view.getScreenPanel().getLayout();
                        if (cardContainsPanel("results")) {
                            cards.show(view.getScreenPanel(),"results");
                        }
                        else {
                            int selectedDepartmentIndex = model.getDepartmentIndex(authorizationModel);
                            ArrayList<Department> departmentListFromDb = model.getDepartmentList(authorizationModel);
                            ResultsView resultsView = new ResultsView();
                            ResultsModel resultsModel = new ResultsModel(connection, departmentListFromDb.get(selectedDepartmentIndex).getId());
                            ResultsPres resultsPres = new ResultsPres(resultsView, resultsModel);
                            addPanelToCardLayout(view.getScreenPanel(), resultsView, "results");
                            cards.show(view.getScreenPanel(),"results");
                        }
                    }else
                        JOptionPane.showMessageDialog(view, "Вы не авторизовались");
                }
                else
                    JOptionPane.showMessageDialog(view, "Для работы в системе необходимо подключиться к БД и авторизоваться");
            }
        });

        view.getStartTestBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = model.checkConnection(authorizationModel);
                if(connection != null){
                    if((model.checkAccess(authorizationModel))) {
                        cards = (CardLayout) view.getScreenPanel().getLayout();
                        if (cardContainsPanel("testing")) {
                            cards.show(view.getScreenPanel(),"testing");
                        }
                        else {
                            int selectedDepartmentIndex = model.getDepartmentIndex(authorizationModel);
                            ArrayList<Department> departmentListFromDb = model.getDepartmentList(authorizationModel);
                            TestingView testingView = new TestingView();
                            TestingModel testingModel = new TestingModel(connection, departmentListFromDb.get(selectedDepartmentIndex).getId());
                            TestingPres testingPres = new TestingPres(testingView, testingModel);
                            addPanelToCardLayout(view.getScreenPanel(), testingView, "testing");
                            cards.show(view.getScreenPanel(),"testing");
                        }
                    }else
                        JOptionPane.showMessageDialog(view, "Вы не авторизовались");
                }
                else
                    JOptionPane.showMessageDialog(view, "Для работы в системе необходимо подключиться к БД и авторизоваться");
            }
        });

        view.getAdminOpportunitiesBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection connection = model.checkConnection(authorizationModel);
                cards = (CardLayout) view.getScreenPanel().getLayout();
                if (cardContainsPanel("admin")) {
                    cards.show(view.getScreenPanel(),"admin");
                }
                else {
                    AdminTabView adminTabView = new AdminTabView();
                    AdminTabModel adminTabModel = new AdminTabModel(connection);
                    AdminTabPres adminTabPres = new AdminTabPres(adminTabView, adminTabModel);
                    addPanelToCardLayout(view.getScreenPanel(), adminTabView, "admin");
                    cards.show(view.getScreenPanel(),"admin");
                }
            }
        });
    }
    private static void addPanelToCardLayout(JPanel cardPanel, JPanel panel, String name) {
        cardPanel.add(panel, name);
        cardMap.put(name, panel);
    }

    private static boolean cardContainsPanel(String name) {
        return cardMap.containsKey(name);
    }
}
