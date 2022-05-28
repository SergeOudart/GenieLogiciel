package fr.ul.miage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import io.github.cdimascio.dotenv.Dotenv;

/**
 * Changer les états des bornes (reservé, ...)
 */

public class Borne {
    static Dotenv dotenv = Dotenv.configure().load();
    private static Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));



    public String etatBorne(int idBorne){
        String etatborne = "";

        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
        String queryBorne = "SELECT etat FROM Borne WHERE idBorne = (?)";
        
        try {
            PreparedStatement pstate = co.prepareStatement(queryBorne);
            pstate.setInt(1, idBorne);
            pstate.execute();
        } catch (SQLException e) {
            System.out.println("Impossible de récupérer une borne disponible");
        }




        return etatborne;



    }

    public static List<Integer> bornesDispo(){
        List<Integer> liste = new ArrayList<Integer>();
        String queryBorne = "SELECT idBorne FROM Borne WHERE etat = 'disponible' ";
            try {
                PreparedStatement pstate = co.prepareStatement(queryBorne);
                ResultSet rs = pstate.executeQuery();
                while(rs.next()){
                    liste.add(rs.getInt(1));
                }
            } catch (SQLException e) {
                System.out.println("Impossible de récupérer une borne");
            }
        return liste;

    }

    public static List<Integer> bornesDispoIntervalle(java.sql.Timestamp date_deb){
        List<Integer> liste = new ArrayList<Integer>();

        String queryBorne1 = "SELECT DISTINCT borne.idBorne FROM borne,reservation WHERE borne.etat = 'disponible' AND borne.idBorne NOT IN (SELECT idBorne FROM reservation)";
        try {
            PreparedStatement pstate = co.prepareStatement(queryBorne1);
            ResultSet rs = pstate.executeQuery(); 
            while(rs.next()){
                liste.add(rs.getInt(1));
            }
        } catch (Exception e) {
            System.out.println("Impossible de récupérer l'id de la borne");
        }
        String queryBorneDispo = "SELECT borne.idBorne FROM borne,reservation WHERE etat = 'disponible' AND borne.idBorne = reservation.idBorne AND (?) > date_fin";
        try {
            PreparedStatement pstate = co.prepareStatement(queryBorneDispo);
            pstate.setTimestamp(1, date_deb);
            ResultSet rs = pstate.executeQuery();
            while(rs.next()){
                liste.add(rs.getInt(1));
            }
        } catch (SQLException e) {

            System.out.println("Impossible de récupérer la borne disponible");
        }
        
        if(liste.isEmpty()){
            System.out.println("Il n'y a pas de borne disponible pour ce créneau");
        }
        
        return liste;
    }

    public static List<Integer> bornesDispoDebutFin(java.sql.Timestamp date_deb, int duree) {
        List<Integer> liste = new ArrayList<Integer>();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date_deb.getTime());
        cal.add(Calendar.HOUR, duree);
        String queryBorneDispo = "SELECT borne.idBorne FROM borne,reservation WHERE etat = 'disponible' AND (?) > reservation.date_fin";
        try {
            PreparedStatement pstate = co.prepareStatement(queryBorneDispo);
            pstate.setTimestamp(1, date_deb);
            ResultSet rs = pstate.executeQuery();
            while(rs.next()) {
                liste.add(rs.getInt("idBorne"));
            }
        } catch (SQLException e) {
            System.out.println("Impossible de récupérer les bornes disponibles");
        }
        return liste;
    }

    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return new Date(c.getTimeInMillis());
    }

    public static void borneEnAttente(int idReservation){
        String query = "SELECT date_fin FROM reservation WHERE idReservation = (?)";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                java.sql.Timestamp timestamp = rs.getTimestamp(1);
                int compareDate = timestamp.compareTo(new java.sql.Timestamp(System.currentTimeMillis()));
                if(compareDate < 0){
                    String query2 = "UPDATE borne,reservation SET borne.etat='en attente' WHERE borne.idBorne = reservation.idBorne AND reservation.idReservation=(?)";
                    PreparedStatement pstate2 = co.prepareStatement(query2);
                    pstate2.execute();
                }
            }
        } catch (Exception e) {
            System.out.println("Impossible de mettre à jour la borne");
        }

    }

    public static void setOccupeeParId(int idBorne) {
        String query = "UPDATE borne SET etat = 'occupe' WHERE idBorne = (?)";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setInt(1, idBorne);
            pstate.execute();
        } catch(SQLException e) {
            System.out.println("Impossible de passer la borne en occupée");
        }
    }

    public static void setDispoParId(int idBorne) {
        String query = "UPDATE borne SET etat = 'disponible' WHERE idBorne = (?)";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setInt(1, idBorne);
            pstate.execute();
        } catch(SQLException e) {
            System.out.println("Impossible de passer la borne en disponible");
        }
    }

    

  
    
}
