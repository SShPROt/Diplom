package presenter;

import model.AdminTabModel;
import view.AdminTabView;
import model.AdminTabModelContent.AdminTabYearsModel;
import presenter.AdminTabPresContent.AdminTabYearsPres;
import view.AdminTabViewContent.AdminTabYearsView;
import model.AdminTabModelContent.AdminTabDepartmentsModel;
import presenter.AdminTabPresContent.AdminTabDepartmentsPres;
import view.AdminTabViewContent.AdminTabDepartmentsView;
import model.AdminTabModelContent.AdminTabTeachersModel;
import presenter.AdminTabPresContent.AdminTabTeachersPres;
import view.AdminTabViewContent.AdminTabTeachersView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class AdminTabPres {
    private AdminTabModel model;
    private AdminTabView view;
    private Connection connection;
    public AdminTabPres(AdminTabView view, AdminTabModel model){
        this.view = view;
        this.model = model;

        view.getYearsBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connection = model.getConnection();
                AdminTabYearsView adminTabYearsView = new AdminTabYearsView();
                AdminTabYearsModel adminTabYearsModel = new AdminTabYearsModel(connection);
                AdminTabYearsPres adminTabYearsPres = new AdminTabYearsPres(adminTabYearsView, adminTabYearsModel);
                view.getScreenPanel().removeAll();
                view.getScreenPanel().add(adminTabYearsView);
                view.getScreenPanel().revalidate();
                view.getScreenPanel().repaint();
            }
        });
        view.getDepartmentsBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connection = model.getConnection();
                AdminTabDepartmentsView adminTabDepartmentView = new AdminTabDepartmentsView();
                AdminTabDepartmentsModel adminTabDepartmentModel = new AdminTabDepartmentsModel(connection);
                AdminTabDepartmentsPres adminTabDepartmentPres = new AdminTabDepartmentsPres(adminTabDepartmentView, adminTabDepartmentModel);
                view.getScreenPanel().removeAll();
                view.getScreenPanel().add(adminTabDepartmentView);
                view.getScreenPanel().revalidate();
                view.getScreenPanel().repaint();
            }
        });
        view.getTeachersBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connection = model.getConnection();
                AdminTabTeachersView adminTabTeacherView = new AdminTabTeachersView();
                AdminTabTeachersModel adminTabTeacherModel = new AdminTabTeachersModel(connection);
                AdminTabTeachersPres adminTabTeacherPres = new AdminTabTeachersPres(adminTabTeacherView, adminTabTeacherModel);
                view.getScreenPanel().removeAll();
                view.getScreenPanel().add(adminTabTeacherView);
                view.getScreenPanel().revalidate();
                view.getScreenPanel().repaint();
            }
        });
    }
}
