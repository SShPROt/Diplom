package other;

import model.TestingModel;
import presenter.TestingPres;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ObjectOutputStream outObj;
    private String username;
    private TestingPres pres;
    private TestingModel model;
    private volatile boolean received = false;

    public ClientHandler(Socket clientSocket, TestingPres pres, TestingModel model) {
        this.clientSocket = clientSocket;
        this.pres = pres;
        this.model = model;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outObj = new ObjectOutputStream(clientSocket.getOutputStream());
            username = in.readLine();

            if (!isValidUser(username)) {
                out.println("Имя пользователя не найдено");
                clientSocket.close();
                return;
            }

            if (pres.isStartTest()) {
                SwingUtilities.invokeLater(() -> {model.showConnectionRequest(username, this);});
            } else {
                model.setUserStatus(username, "Подключён", false);
                model.getClients().put(username, this);
                out.println("Успех");
            }

            String grade, score, time;
            grade = in.readLine();
            score = in.readLine();
            time = in.readLine();
            out.println("Завершение");
            model.setUserStatus(username, "Завершил", true);
            model.setResult(username, grade, score, time);
            for (int i = 0; i < model.getStudents().size(); i++) {
                if(model.getStudents().get(i).getFullName().equals(username)) {
                    model.getResultsFromTable().get(i).setScore(score);
                    model.getResultsFromTable().get(i).setGrade(grade);
                    model.getResultsFromTable().get(i).setTime(time);
                }
            }



        } catch (IOException e) {
            model.setUserStatus(username, "Не подключён", true);
            model.setUserStatus(username, "Не подключён", false);
        } finally {
            if (username != null) {
                model.getClients().remove(username);
                model.setUserStatus(username, "Не подключён", false);
            }
            closeSender();
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValidUser(String username) {
        for (int i = 0; i < model.getStudentModel().getRowCount(); i++) {
            if (username.equals(model.getStudentModel().getValueAt(i, 0))) {
                return true;
            }
        }
        return false;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            out.flush();
        }
    }

    public void sendObject(ArrayList<String[]> data){
        try {
            if (outObj != null) {
                outObj.writeObject(data);
                outObj.flush();
            }
            //in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeSender(){
        out.close();
        try {
            outObj.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ExtracloseConnection() {
        while (!received);
        closeConnection();
    }

    public void closeConnection() {
        closeSender();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}