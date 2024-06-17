package other;

public class DefaultDataForAuthorization {

    private final String ip = "127.0.0.1";
    private final String port = "3306";
    private final String loginTeacher = "teacher";
    private final String passwordTeacher = "12345";
    private final String loginAdmin = "admin";

    public DefaultDataForAuthorization() {
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getLoginTeacher() {
        return loginTeacher;
    }

    public String getPasswordTeacher() {
        return passwordTeacher;
    }

    public String getLoginAdmin() {
        return loginAdmin;
    }
}
