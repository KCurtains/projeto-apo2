package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class dbConnection {
	
    private static final String URL =
            "jdbc:mysql://localhost:3306/estacionamento_db";

        private static final String USER = "root";
        private static final String PASSWORD = "";

        public static Connection getConnection() {

            try {
                return DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
                );

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
}
