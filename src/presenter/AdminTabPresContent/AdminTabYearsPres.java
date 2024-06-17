package presenter.AdminTabPresContent;

import model.AdminTabModelContent.AdminTabYearsModel;
import view.AdminTabViewContent.AdminTabYearsView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminTabYearsPres {
    private AdminTabYearsModel model;
    private AdminTabYearsView view;
    private JTable yearsTable;
    public AdminTabYearsPres(AdminTabYearsView view, AdminTabYearsModel model){
        this.view = view;
        this.model = model;
        yearsTable = view.getYearsTable();

        yearsTable.setDefaultEditor(Object.class, null);
        model.setMyModel((DefaultTableModel) yearsTable.getModel());
        model.addColumns();
        yearsTable.setModel(model.getMyModel());

        if(!model.fillTable()){
            JOptionPane.showMessageDialog(view, model.getInformation());
        }

        view.getEditYearBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(yearsTable.getSelectedRowCount() == 1) {
                    int year = Integer.parseInt(view.getYearField().getText());
                    int selectedRowIndex = yearsTable.getSelectedRow();
                    if(!model.editYear(year, selectedRowIndex))
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    fillField("");
                }
                else
                    JOptionPane.showMessageDialog(view, "Выберите лишь одну кафедру");
            }
        });
        view.getAddYearBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int year = Integer.parseInt(view.getYearField().getText());
                if(!model.addYear(year))
                    JOptionPane.showMessageDialog(view, model.getInformation());
                fillField("");
            }
        });
        view.getDeleteYearBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(yearsTable.getSelectedRowCount() > 0) {
                    int[] rows = yearsTable.getSelectedRows();
                    if(!model.removeRows(rows))
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    fillField("");
                }
            }
        });
        view.getYearsTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                fillField(yearsTable.getValueAt(yearsTable.getSelectedRow(), 0).toString());
            }
        });
    }
    private void fillField(String year){
        view.getYearField().setText(year);
    }
}
