package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class StartPageModel {
    private PrintWriter out;

    private Socket socket;

    public StartPageModel(){
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendAnswer(String mess){
        out.println(mess);
    }

    public String getFullName(String surname, String name, String middleName){
        return surname + " " + name + " " + middleName;
    }
}
