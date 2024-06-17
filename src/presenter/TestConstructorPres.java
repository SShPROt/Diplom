package presenter;

import model.TestConstructorModel;
import model.TestConstructorModelContent.TestEditorModel;
import presenter.TestConstructorPresContent.TestEditorPres;
import view.TestConstructorView;
import view.TestConstructorViewContent.TestEditorView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TestConstructorPres {
    private TestConstructorModel model;
    private TestConstructorView view;
    private JList testList;
    private JTabbedPane questionsPanel;

    public TestConstructorPres(TestConstructorView view, TestConstructorModel model){
        this.model = model;
        this.view = view;
        testList = view.getTestList();
        questionsPanel = view.getQuestionsPanel();

        testList.setModel(model.getMyModel());
        if(!model.fillList(view.getShowAllCheckBox().isSelected())){
            JOptionPane.showMessageDialog(view, model.getInformation());
        }

        testList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                fillField(testList.getSelectedValue().toString());
            }
        });

        view.getCreateBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String testName = view.getNameField().getText();
                if(!model.createTest(testName))
                    JOptionPane.showMessageDialog(view, model.getInformation());
                fillField("");
            }
        });
        view.getEditBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String testName = view.getNameField().getText();
                int selectedRowIndex = testList.getSelectedIndex();
                if(!model.editTest(testName, selectedRowIndex))
                    JOptionPane.showMessageDialog(view, model.getInformation());
                fillField("");
            }
        });
        view.getDeleteBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedTestIndex = testList.getSelectedIndex();
                if(!model.removeTest(selectedTestIndex))
                    JOptionPane.showMessageDialog(view, model.getInformation());
                fillField("");
            }
        });
        view.getOpenBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TestEditorModel testEditorModel = new TestEditorModel(model.getConnection(), model.getTestList(), testList.getSelectedIndex());
                TestEditorView testEditorView = new TestEditorView();
                TestEditorPres testEditorPres = new TestEditorPres(testEditorView, testEditorModel);
                questionsPanel.add(testEditorView);
            }
        });
        view.getShowAllCheckBox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!model.fillList(view.getShowAllCheckBox().isSelected())){
                    JOptionPane.showMessageDialog(view, model.getInformation());
                }
            }
        });
    }
    private void fillField(String testName){
        view.getNameField().setText(testName);
    }

}
