package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/tramplin";
    private static final String USER = "root";
    private static final String PASSWORD = "qwerty123";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to database successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found in classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Connection failed. URL: " + URL + ", User: " + USER);
            e.printStackTrace();
        }
        return conn;
    }
}