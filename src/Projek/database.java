package Projek;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {

    private static final String URL =
        "jdbc:postgresql://localhost:5432/projek";
    private static final String USER = "postgres";
    private static final String PASSWORD = "kibo";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
