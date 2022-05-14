package fr.ul.miage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class Borne {



    public String etatBorne(int idBorne){
        String etatborne = "";

        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        Connection co = Client.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
        String queryBorne = "SELECT etat FROM Borne WHERE idBorne = (?)";
        
        try {
            PreparedStatement pstate = co.prepareStatement(queryBorne);
            pstate.setInt(1, idBorne);
            pstate.execute();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




        return etatborne;



    }

    public static List<Integer> bornesDispo(){
        List<Integer> liste = new ArrayList<Integer>();
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        Connection co = Client.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
        String queryBorne = "SELECT idBorne FROM Borne WHERE etat = 'disponible' ";

        try {
            PreparedStatement pstate = co.prepareStatement(queryBorne);
            ResultSet rs = pstate.executeQuery();
            while(rs.next()){
                liste.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        return liste;


    }

  
    
}
