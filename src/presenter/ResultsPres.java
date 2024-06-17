package presenter;

import model.ResultsModel;
import view.ResultsView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultsPres {
    private ResultsView view;
    private ResultsModel model;
    private JTable resultTable;
    public ResultsPres(ResultsView view, ResultsModel model){
        this.view = view;
        this.model = model;
        this.resultTable = view.getResultTable();

        model.setStudentModel((DefaultTableModel) resultTable.getModel());
        model.addStudentTableColumns();
        resultTable.setModel(model.getStudentModel());

        TableColumnModel columnModel = resultTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(60);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(40);
        columnModel.getColumn(4).setPreferredWidth(40);
        columnModel.getColumn(5).setPreferredWidth(40);

        fillYearBox();
        fillGroupBox();

        view.getYearBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setSelectedYearIndex(view.getYearBox().getSelectedIndex() - 1);
                fillGroupBox();
            }
        });
        view.getGroupBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setSelectedGroupIndex(view.getGroupBox().getSelectedIndex() - 1);
                if ((view.getGroupBox().getSelectedIndex() != 0) && (view.getGroupBox().getSelectedIndex() != -1)) {
                    fillTestBox();
                    model.setSelectedTestIndex(view.getTestBox().getSelectedIndex() - 1);
                    if (!model.fillTable()) {
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    }
                }
            }
        });
        view.getTestBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setSelectedTestIndex(view.getTestBox().getSelectedIndex() - 1);
                if ((view.getTestBox().getSelectedIndex() != -1) && (view.getGroupBox().getSelectedIndex() != 0)) {
                    if (!model.fillTable()) {
                        JOptionPane.showMessageDialog(view, model.getInformation());
                    }
                }
            }
        });
    }

    private void fillYearBox() {
        ResultSet res = model.getYearsFromDB();
        try {
            while (res.next()) {
                view.getYearBox().addItem(res.getInt("year"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, model.getInformation());
        }
    }

    private void fillGroupBox() {
        view.getGroupBox().removeAllItems();
        view.getGroupBox().addItem("...");
        clearTestBox();
        ResultSet res = model.getGroupsFromDB();
        try {
            while (res.next()) {
                view.getGroupBox().addItem(res.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, model.getInformation());
        }
    }

    private void fillTestBox() {
        clearTestBox();
        ResultSet res = model.getTestsFromDB();
        try {
            while (res.next()) {
                view.getTestBox().addItem(res.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, model.getInformation());
        }
    }

    private void clearTestBox(){
        view.getTestBox().removeAllItems();
        view.getTestBox().addItem("...");
    }
}
