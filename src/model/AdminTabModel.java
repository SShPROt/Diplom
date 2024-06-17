package model;

import java.sql.Connection;

public class AdminTabModel {
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public AdminTabModel(Connection connection){
        this.connection = connection;
    }
}
