package fr.ul.miage;

import java.sql.*;

public class DatabaseConnection {
    
    Connection con;

    DatabaseConnection(String uri, String user, String password) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");

        try {
            this.con = DriverManager.getConnection(uri, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String fetchPassword(String username) {
        try {
            PreparedStatement stmt = this.con.prepareStatement("SELECT * FROM user WHERE plaque=?");
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();

            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

}
