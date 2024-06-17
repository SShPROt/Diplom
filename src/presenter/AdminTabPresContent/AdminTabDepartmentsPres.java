package presenter.AdminTabPresContent;

import model.AdminTabModelContent.AdminTabDepartmentsModel;
import view.AdminTabViewContent.AdminTabDepartmentsView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminTabDepartmentsPres {
    private AdminTabDepartmentsView view;
    private AdminTabDepartmentsModel model;
    private JTable departmentTable;
    public AdminTabDepartmentsPres(AdminTabDepartmentsView view, AdminTabDepartmentsModel model){
        this.view = view;
        this.model = model;
        departmentTable = view.getDepartmentTable();

        departmentTable.setDefaultEditor(Object.class, null);
        model.setMyModel((DefaultTableModel) departmentTable.getModel());
        model.addColumns();
        departmentTable.setModel(model.getMyModel());

        if(!model.fillTable()){
            JOptionPane.showMessageDialog(view, model.getInformation());
        }

        view.getAddDepartmentBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String department = view.getDepartmentField().getText();
                if(!model.addDepartment(department))
                    JOptionPane.showMessageDialog(view, model.getInformation());
                fillField("");
            }
        });

        view.getEditDepartmentBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(departmentTable.getSelectedRowCount() == 1) {
                    String department = view.getDepartmentField().getText();
                    int selectedRowIndex = departmentTable.getSelectedRow();
                    if(!model.editDepartment(department, selectedRowIndex))
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    fillField("");
                }
                else
                    JOptionPane.showMessageDialog(view, "Выберите лишь одну кафедру");
            }
        });

        view.getDeleteDepartmentBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(departmentTable.getSelectedRowCount() > 0) {
                    int[] rows = departmentTable.getSelectedRows();
                    if(!model.removeRows(rows))
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    fillField("");
                }
            }
        });

        departmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                fillField(departmentTable.getValueAt(departmentTable.getSelectedRow(), 0).toString());
            }
        });
    }
    private void fillField(String department){
        view.getDepartmentField().setText(department);
    }
}
