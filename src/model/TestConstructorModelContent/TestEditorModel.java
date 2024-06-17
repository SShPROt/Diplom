package model.TestConstructorModelContent;


import other.Question;
import other.Test;
import presenter.TestConstructorPresContent.TestEditorPres;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

public class TestEditorModel {
    private Connection connection;
    private ArrayList<Question> questionsList = new ArrayList<>();
    private ArrayList<Test> testsList;
    private int selectedTestIndex;
    private int selectedQuestionIndex;
    private DefaultListModel myModel = new DefaultListModel<>();
    private String information;

    public ArrayList<Test> getTestsList() {
        return testsList;
    }

    public int getSelectedTestIndex() {
        return selectedTestIndex;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setSelectedQuestionIndex(int selectedQuestionIndex) {
        this.selectedQuestionIndex = selectedQuestionIndex;
    }

    public String getInformation() {
        return information;
    }

    public DefaultListModel getMyModel() {
        return myModel;
    }

    public TestEditorModel(Connection connection, ArrayList<Test> testsList, int selectedTestIndex){
        this.connection = connection;
        this.selectedTestIndex = selectedTestIndex;
        this.testsList = testsList;
    }

    public String getSelectedTestName(){
        return testsList.get(selectedTestIndex).getName();
    }

    private void clearList(){
        while (myModel.getSize() > 0) {
            myModel.removeElementAt(0);
        }
        clearQuestionArray();
    }

    private void clearQuestionArray(){
        while(!questionsList.isEmpty())
            questionsList.remove(0);
    }
    public boolean fillList(){
        String query;
        PreparedStatement preparedStatement;
        ResultSet res;
        clearList();
        try {
            query = "SELECT * FROM questions where test = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, testsList.get(selectedTestIndex).getId());

            res = preparedStatement.executeQuery();
            while (res.next())
            {
                questionsList.add(new Question(res.getString(2), res.getInt(1), res.getString("type")));
                myModel.addElement(res.getString(2));
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе получения списка вопросов";
            return false;
        }
        return true;
    }
    public boolean saveToDatabase(String questionContent, String answersContent,String selectedRadioIndex, String questionName, int score) {
        if((!questionName.equals("")) && (!questionName.equals("Новый вопрос"))) {

            if(questionsList.get(selectedQuestionIndex).getId() == 0){
                for(int i = 0; i < questionsList.size(); i++){
                    if(questionName.equals(questionsList.get(i).getName())){
                        information = "Вопрос с таким названием уже существует";
                        return false;
                    }
                }
                String query = "INSERT INTO questions (name, question, answers, answer, type, test, score) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, questionName);
                    preparedStatement.setString(2, questionContent);
                    preparedStatement.setString(3, answersContent);
                    preparedStatement.setString(4, selectedRadioIndex);
                    preparedStatement.setString(5, questionsList.get(selectedQuestionIndex).getType());
                    preparedStatement.setInt(6, testsList.get(selectedTestIndex).getId());
                    preparedStatement.setInt(7, score);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    information = "Произошла ошибка на этапе создания вопроса в БД. Проверьте выбрали ли вы верный ответ";
                    return false;
                }
            }
            else {
                String query = "UPDATE questions SET name = ?, question = ?, answers = ?, answer = ?, score = ? WHERE id_question = ?";
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, questionName);
                    preparedStatement.setString(2, questionContent);
                    preparedStatement.setString(3, answersContent);
                    preparedStatement.setString(4, selectedRadioIndex);
                    preparedStatement.setInt(5, score);
                    preparedStatement.setInt(6, questionsList.get(selectedQuestionIndex).getId());
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    information = "Произошла ошибка на этапе обновления вопроса в БД";
                    return false;
                }
            }
        }else {
            information = "Дайте оригинальное название вопросу";
            return false;
        }
        return true;
    }

    public String[] loadFromDatabase() {
        String query = "SELECT question, answers, answer, type, score FROM questions WHERE id_question = ?";
        String[] data = new String[]{"", "", " -1", "", ""};
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, questionsList.get(selectedQuestionIndex).getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                data = new String[]{resultSet.getString("question"), resultSet.getString("answers"), resultSet.getString("answer"), resultSet.getString("type"), resultSet.getString("score")};
            }
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе загрузки вопроса";
        }
        return data;
    }

    public String convertImagesToBase64(String htmlContent) throws IOException {
        String updatedHtmlContent = htmlContent;
        while (updatedHtmlContent.contains("src=\"file:")) {
            int start = updatedHtmlContent.indexOf("src=\"file:") + 10;
            int end = updatedHtmlContent.indexOf("\"", start);
            String imagePath = updatedHtmlContent.substring(start, end);
            File imageFile = new File(imagePath);
            byte[] fileContent = Files.readAllBytes(imageFile.toPath());
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            updatedHtmlContent = updatedHtmlContent.replace("src=\"file:" + imagePath, "src=\"data:image/jpg;base64," + encodedString);
        }
        return updatedHtmlContent;
    }

    public String convertBase64ToImages(String htmlContent) throws IOException {
        String updatedHtmlContent = htmlContent;
        while (updatedHtmlContent.contains("src=\"data:image/jpg;base64,")) {
            int start = updatedHtmlContent.indexOf("src=\"data:image/jpg;base64,") + 27;
            int end = updatedHtmlContent.indexOf("\"", start);
            String base64Image = updatedHtmlContent.substring(start, end);
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            File tempFile = File.createTempFile("image", ".jpg");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(imageBytes);
            }
            updatedHtmlContent = updatedHtmlContent.replace("src=\"data:image/jpg;base64," + base64Image, "src=\"file:" + tempFile.getAbsolutePath());
        }
        return updatedHtmlContent;
    }

    public boolean addNewQuestion(String type){
        if(myModel.getSize() == 0){
            myModel.addElement("Новый вопрос");
            questionsList.add(new Question("Новый вопрос", type));
            return true;
        }
        else
            if(!myModel.getElementAt(myModel.getSize()-1).equals("Новый вопрос")) {
                myModel.addElement("Новый вопрос");
                questionsList.add(new Question("Новый вопрос", type));
                return true;
            }
            else {
                information = "Прежде, чем добавить новый вопрос, закончите работу с прошлым";
                return false;
            }
    }

    public String getQuestionType(){
        return questionsList.get(selectedQuestionIndex).getType();
    }

    public boolean deleteQuestion(){

        String query = "DELETE FROM questions WHERE (id_question = ?)";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, questionsList.get(selectedQuestionIndex).getId());
            preparedStatement.execute();
            myModel.removeElementAt(selectedQuestionIndex);
            questionsList.remove(selectedQuestionIndex);
        } catch (SQLException e) {
            information = "Произошла ошибка на этапе удаления вопроса";
            return false;
        }
        return true;
    }
}
