package model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class TestPageModel {
    private ArrayList<String[]> data;
    private String time;
    private Socket socket;
    private PrintWriter out;
    private String gradeSys;
    public TestPageModel(ArrayList<String[]> data, String time, Socket socket, String gradeSys){
        this.data = data;
        this.time = time;
        this.socket = socket;
        this.gradeSys = gradeSys;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getGradeSys() {
        return gradeSys;
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
    public int getCountQuestion(){
        return data.size();
    }

    public String[] getQuestion(int index){
        return data.get(index);
    }

    public String getQuestionType(int index) {
        return data.get(index)[3];
    }

    public String getAnswer(int index){
        return data.get(index)[2];
    }

    public int getScore(int index){
        return Integer.parseInt(data.get(index)[4]);
    }

    public int getTime() {
        String[] splitTime = time.split(":");
        int hours = Integer.parseInt(splitTime[0]);
        int minutes = Integer.parseInt(splitTime[1]);
        int seconds = Integer.parseInt(splitTime[2]);
        seconds += (minutes * 60) + (hours * 3600);
        return seconds;
    }

    public void sendMessage(String mess){
        out.println(mess);
    }

    public int getGrade(int currentScore, int score){
        double percentD = ((double) currentScore / score) * 100;
        int percentI = (int) Math.round(percentD);
        String[] gradePercentsS = gradeSys.split(",");
        int[] gradesPercentsI = new int[gradePercentsS.length];
        for(int i = 0; i < gradesPercentsI.length; i++){
            gradesPercentsI[i] = Integer.parseInt(gradePercentsS[i]);
        }
        if(percentI >= gradesPercentsI[1]){
            if(percentI >= gradesPercentsI[0])
                return 5;
            else
                return 4;
        }
        else if(percentI >= gradesPercentsI[2])
            return 3;
        else
            return 2;

    }
}
