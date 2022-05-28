package fr.ul.miage;

import java.sql.*;

public class DatabaseConnection {
    

    public static Connection dbco(String uri, String user, String password) {
        Connection con = null;

        try {
            con = DriverManager.getConnection(uri, user, password);
        } catch (SQLException e) {
            System.out.println("Impossible d'établir la connexion");
        }
        return con;

    }



   

}
