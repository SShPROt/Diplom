package presenter;

import model.StartPageModel;
import model.TestPageModel;
import view.StartPageView;
import view.TestPageView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class StartPagePres {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private PrintWriter out;
    private StartPageView view;
    private StartPageModel model;

    private CardLayout cards;
    private TestPagePres testPagePres;
    public StartPagePres(StartPageView view, StartPageModel model){
        this.view = view;
        this.model = model;

        cards = (CardLayout) view.getCardPanel().getLayout();


        view.getConnectBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String surname = view.getSurnameField().getText(),
                        name = view.getNameField().getText(),
                        middleName = view.getMiddleNameField().getText();
                String fullName = model.getFullName(surname, name, middleName);
                view.getConnectBtn().setEnabled(false);
                if (!fullName.isEmpty()) {
                    connectToServer(fullName.toString());
                }
            }
        });

        view.getExitBtn().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
            }
        });
    }
    private void connectToServer(String username) {
        try {
            Socket socket;
            if(!view.getIpField().getText().isEmpty())
                socket = new Socket(view.getIpField().getText(), SERVER_PORT);
            else
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            model.setSocket(socket);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            out.println(username);

            new Thread(() -> {
                String response;
                try {
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                        switch (response) {
                            case "Успех":
                                SwingUtilities.invokeLater(() -> {
                                    cards.show(view.getCardPanel(), "waiting");
                                    view.getConnectBtn().setEnabled(true);
                                });
                                break;
                            case "Отказ":
                                SwingUtilities.invokeLater(() -> {
                                    view.getConnectBtn().setEnabled(true);
                                });
                                JOptionPane.showMessageDialog(view, "Отказано в прохождении теста.", "Отказано", JOptionPane.ERROR_MESSAGE);
                                break;
                            case "Предупреждение":
                                JOptionPane.showMessageDialog(view, "Вы получили предупреждение!", "Предупреждение", JOptionPane.WARNING_MESSAGE);
                                break;
                            case "Выгнать":
                                JOptionPane.showMessageDialog(view, "Вы были замечены в нарушении правил проведения тестирования.", "Отключение", JOptionPane.ERROR_MESSAGE);
                                socket.close();
                                resetClientState();
                                break;
                            case "Имя пользователя не найдено":
                                JOptionPane.showMessageDialog(view, response, "Ошибка", JOptionPane.ERROR_MESSAGE);
                                SwingUtilities.invokeLater(() -> {
                                    view.getConnectBtn().setEnabled(true);
                                });
                                out = null;
                                break;
                            case "Начало": {
                                //Кочергmodel.sendAnswer("");
                                String time = in.readLine();
                                String gradeSys = in.readLine();
                                ArrayList<String[]> data = (ArrayList<String[]>) inputStream.readObject();
                                TestPageView testPageView = new TestPageView();
                                TestPageModel testPageModel = new TestPageModel(data, time, socket, gradeSys);
                                testPagePres = new TestPagePres(testPageView, testPageModel);
                                view.getCardPanel().add(testPageView, "test");
                                SwingUtilities.invokeLater(() -> {
                                    cards.show(view.getCardPanel(), "test");
                                    view.getConnectBtn().setEnabled(true);
                                });

                                break;
                            }
                            case "Тест отменён":
                                JOptionPane.showMessageDialog(view, "Тест был отменён.", "Тест отменён", JOptionPane.INFORMATION_MESSAGE);
                                resetClientState();
                                SwingUtilities.invokeLater(() -> {
                                    view.getConnectBtn().setEnabled(true);
                                });
                                break;
                            case "Экстренное завершение":
                                testPagePres.addMyAnswer();
                                testPagePres.calculateScore();
                                testPagePres.sendResult();
                                JOptionPane.showMessageDialog(view, "Тест был завершён преподавателем", "Тест завершён", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            case "Завершение": {

                                int score = testPagePres.getScore();
                                int currentScore = testPagePres.getCurrentScore();
                                String time = testPagePres.getFormattedTime();
                                int grade = testPagePres.getGrade();
                                SwingUtilities.invokeLater(() -> {
                                    cards.show(view.getCardPanel(), "result");
                                    if(grade == 2)
                                        view.getResultLabel().setText("Вы не прошли тест");
                                    else
                                        view.getResultLabel().setText("Вы успешно прошли тест");
                                    view.getGradeLabel().setText("Ваша оценка: " + grade);
                                    view.getScoreLabel().setText("Получено " + currentScore + "/" + score + " баллов");
                                    view.getTimeLabel().setText("Тест пройден за " + time);
                                });
                                break;
                            }
                            default:
                                System.out.println(response);
                                break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    try {
                        String ti = in.readLine();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(view, "Сервер недоступен или неправильный ip адрес", "Ошибка", JOptionPane.ERROR_MESSAGE);
            view.getConnectBtn().setEnabled(true);
        }
    }

    private void resetClientState() {
        SwingUtilities.invokeLater(() -> {
            cards.show(view.getCardPanel(), "authorization");
            out = null;
        });
    }
}
