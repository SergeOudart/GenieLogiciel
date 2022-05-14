package fr.ul.miage;

import java.sql.*;

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;

public class DatabaseConnection {
    

    public static Connection dbco(String uri, String user, String password) {
        Connection con = null;

        try {
            con = DriverManager.getConnection(uri, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;

    }



   

}
