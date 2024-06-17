package presenter;

import model.TestingModel;
import other.ButtonEditor;
import other.ButtonRenderer;
import other.ClientHandler;
import view.TestingView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TestingPres {
    private TestingView view;
    private TestingModel model;
    private JTable studentTable;
    private JTable testTable;
    private boolean preparation = false;
    private boolean startTest = false;
    private boolean cancelTest = false;
    private static final int PORT = 12345;
    private ServerSocket serverSocket;

    public boolean isStartTest() {
        return startTest;
    }

    public boolean isPreparation() {
        return preparation;
    }

    public TestingPres(TestingView view, TestingModel model) {
        this.view = view;
        this.model = model;
        this.studentTable = view.getStudentTable();
        this.testTable = view.getTestTable();

        CardLayout cards = (CardLayout) view.getCardPanel().getLayout();
        cards.show(view.getCardPanel(), "main");

        model.setStudentModel((DefaultTableModel) studentTable.getModel());
        model.addStudentTableColumns();
        studentTable.setModel(model.getStudentModel());

        model.setTestModel((DefaultTableModel) testTable.getModel());
        model.addTestTableColumns();
        testTable.setModel(model.getTestModel());

        fillYearBox();
        fillGroupBox();
        fillTestBox();

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
                if ((view.getGroupBox().getSelectedIndex() != 0) && (view.getGroupBox().getSelectedIndex() != -1))
                    if (!model.fillTable())
                        JOptionPane.showMessageDialog(view, model.getInformation());
            }
        });
        view.getTestBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setSelectedTestIndex(view.getTestBox().getSelectedIndex() - 1);
            }
        });

        view.getConfirmBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (preparation) {
                    preparation = false;
                    view.getConfirmBtn().setText("Подтвердить выбор");
                    model.setColumnModel(studentTable.getColumnModel());
                    model.editColumns(preparation);
                    studentTable.setColumnModel(model.getColumnModel());

                    view.getYearBox().setEnabled(true);
                    view.getGroupBox().setEnabled(true);
                    view.getTestBox().setEnabled(true);
                    view.getStartBtn().setEnabled(false);
                    view.getStatusLabel().setVisible(false);

                    try {
                        serverSocket.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else {
                    if ((view.getGroupBox().getSelectedIndex() != 0) && (view.getTestBox().getSelectedIndex() != 0)) {
                        preparation = true;
                        view.getConfirmBtn().setText("Отменить выбор");
                        model.editColumns(preparation);

                        view.getYearBox().setEnabled(false);
                        view.getGroupBox().setEnabled(false);
                        view.getTestBox().setEnabled(false);
                        view.getStartBtn().setEnabled(true);
                        view.getStatusLabel().setVisible(true);

                        new Thread(() -> listenForClients()).start();
                    }
                }
            }
        });
        view.getStartBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!startTest) {
                    startTest = true;
                    cancelTest = true;
                    view.getStartBtn().setText("Отменить тест");
                    view.getSaveResultsBtn().setVisible(true);
                    model.copyModel();
                    testTable.getColumn("Действие").setCellRenderer(new ButtonRenderer());
                    testTable.getColumn("Действие").setCellEditor(new ButtonEditor(new JCheckBox(), model.getClients(), (DefaultTableModel) testTable.getModel()));

                    TableColumnModel columnModel = testTable.getColumnModel();
                    columnModel.getColumn(0).setPreferredWidth(60);
                    columnModel.getColumn(1).setPreferredWidth(60);
                    columnModel.getColumn(2).setPreferredWidth(200);
                    columnModel.getColumn(3).setPreferredWidth(40);
                    columnModel.getColumn(4).setPreferredWidth(40);
                    columnModel.getColumn(5).setPreferredWidth(40);

                    testTable.setRowHeight(35);
                    CardLayout cards = (CardLayout) view.getCardPanel().getLayout();
                    cards.show(view.getCardPanel(), "test");
                    model.getQuestionsFromDatabase();
                    model.startTest();
                    model.initializeResultList();

                } else {
                    startTest = false;
                    view.getStartBtn().setText("Начать тестирование");
                    if(cancelTest) {
                        model.cancelTest();
                    }
                    else
                        view.getStartBtn().setEnabled(false);
                    CardLayout cards = (CardLayout) view.getCardPanel().getLayout();
                    cards.show(view.getCardPanel(), "main");
                }
            }
        });
        view.getSaveResultsBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.finishTest();
                model.saveResultsOnDB();
                model.closeConnection();
                view.getSaveResultsBtn().setVisible(false);
                view.getStartBtn().setText("Вернуться");

                cancelTest = false;
                preparation = false;
                view.getConfirmBtn().setText("Подтвердить выбор");
                model.setColumnModel(studentTable.getColumnModel());
                model.editColumns(preparation);
                studentTable.setColumnModel(model.getColumnModel());

                view.getYearBox().setEnabled(true);
                view.getGroupBox().setEnabled(true);
                view.getTestBox().setEnabled(true);
                view.getStatusLabel().setVisible(false);

                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
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
        ResultSet res = model.getTestsFromDB();
        try {
            while (res.next()) {
                view.getTestBox().addItem(res.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, model.getInformation());
        }
    }

    public void listenForClients() {
        try{
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket, this, model).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
