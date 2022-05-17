package fr.ul.miage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.cdimascio.dotenv.Dotenv;

public class Reservation{
    public Timestamp getDate_depart() {
        return date_depart;
    }

    public void setDate_depart(Timestamp date_depart) {
        this.date_depart = date_depart;
    }
    static Dotenv dotenv = Dotenv.configure().load();
    private static Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));

    private int idReservation;
    private int idClient;
    private int idBorne;
    private Timestamp date_deb;
    private Timestamp date_fin;
    private Timestamp date_depart;

    public Reservation(int idReservation, int idClient, int idBorne, Timestamp date_deb, Timestamp date_fin, Timestamp date_depart, int duree) {
        this.idReservation = idReservation;
        this.idClient = idClient;
        this.idBorne = idBorne;
        this.date_deb = date_deb;
        this.date_fin = date_fin;
        this.date_depart = date_depart;
        this.duree = duree;
    }
    public Reservation(int idReservation, int idClient, int idBorne, Timestamp date_deb, Timestamp date_fin, int duree) {
        this.idReservation = idReservation;
        this.idClient = idClient;
        this.idBorne = idBorne;
        this.date_deb = date_deb;
        this.date_fin = date_fin;
        this.duree = duree;
    }
    private int duree;

    public int getIdReservation() {
        return this.idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdClient() {
        return this.idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdBorne() {
        return this.idBorne;
    }

    public void setIdBorne(int idBorne) {
        this.idBorne = idBorne;
    }

    public Timestamp getDate_deb() {
        return this.date_deb;
    }

    public void setDate_deb(Timestamp date_deb) {
        this.date_deb = date_deb;
    }

    public Timestamp getDate_fin() {
        return this.date_fin;
    }

    public void setDate_fin(Timestamp date_fin) {
        this.date_fin = date_fin;
    }

    public int getDuree() {
        return this.duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public Reservation() {
    }
   
    /**
     * Changer idReservation enlever Auto increment et en générer aléatoirement 
     * @param idClient
     * @return
     */



    public static List<Reservation> verifReservation(int idClient){
        String queryReservation = "SELECT * FROM reservation WHERE idClient = (?)";
        int idBorne;
        String pseudo;
        String mdp;
        String nom;
        String num_tel;
        Timestamp dt_deb;
        Timestamp dt_fin;
        int duree;
        List<Reservation> lr = new ArrayList<Reservation>();
        
        
        try {
            PreparedStatement pstate = co.prepareStatement(queryReservation);
            pstate.setInt(1, idClient);
            ResultSet rs =  pstate.executeQuery();    
            while(rs.next()){
                //idBorne = rs.getInt(1);
                lr.add(new Reservation(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getTimestamp(4),rs.getTimestamp(5),rs.getInt(6)));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("e");
        }

        return lr;


    }


    public static Reservation affecterReservation(int idClient,Timestamp date_deb, int duree){
        Reservation r = null;
        List<Integer> bornes_dispos = new ArrayList<Integer>();
        bornes_dispos.addAll(Borne.bornesDispo());
        int id_borne_dispo = 0;
        int idReservation = 0;
        Long duration = (long) ((duree*60)*1000);
        Timestamp date_fin = new Timestamp(date_deb.getTime()+duration);

        if(!bornes_dispos.isEmpty()){
            id_borne_dispo = bornes_dispos.get(0);
        }
    

        String queryReservation = "INSERT INTO Reservation (idClient,idBorne,date_deb,date_fin,duree) VALUES (?,?,?,?,?) ";

        try {
            PreparedStatement pstate = co.prepareStatement(queryReservation);
            pstate.setInt(1, idClient);
            pstate.setInt(2,id_borne_dispo);
            pstate.setTimestamp(3, date_deb);
           
            pstate.setTimestamp(4, date_fin);
            pstate.setInt(5, duree);
            pstate.execute();
            int countLines = pstate.executeUpdate();
            if(countLines > 0){
                System.out.println("a");
                String queryId = "SELECT idReservation from reservation WHERE idClient = (?) AND idBorne = (?)";
                PreparedStatement pstate2 = co.prepareStatement(queryId);
                pstate2.setInt(1, idClient);
                pstate2.setInt(2,id_borne_dispo);
                ResultSet rs = pstate2.executeQuery();
                if(rs.next()){
                    idReservation = rs.getInt(1);
                }
                System.out.println(idReservation);
                r = new Reservation(idReservation,idClient,id_borne_dispo,date_deb,date_fin,duree);
                System.out.println(r.toString());
            }

            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return r;
    }

    public void changerBorneEtatReserveeParIdReservation(int idReservation) {
        String query = "UPDATE borne,reservation SET borne.etat='reservee' WHERE borne.idBorne = reservation.idBorne AND reservation.idReservation=(?)";
        try {
            PreparedStatement requete = co.prepareStatement(query);
            requete.setInt(1, idReservation);
            requete.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changerBorneEtatDispoParIdReservation(int idReservation) {
        String query = "UPDATE borne,reservation SET borne.etat='disponible' WHERE borne.idBorne = reservation.idBorne AND reservation.idReservation=(?)";
        try {
            PreparedStatement requete = co.prepareStatement(query);
            requete.setInt(1, idReservation);
            requete.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkProlongerReservation(int idReservation){ //Vérifier dans combien de minutes est l'expiration de réservation
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        long deb;
        long fin = 0; 
        long diff_timestamp = 0;
        String query = "SELECT date_fin from reservation WHERE idReservation = (?)";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setInt(1, idReservation);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                timestamp2 = rs.getTimestamp(1);     
            }
            deb = timestamp.getTime();
            long dateFin = timestamp2.getTime();
            long mtn = timestamp.getTime(); 
            long diff = mtn - dateFin;
            long diffMinutes = diff / (60*1000);
            // diff_timestamp = TimeUnit.MILLISECONDS.toMinutes(deb - fin);
            System.out.println(diffMinutes);
            if(diffMinutes < 30){
                return true;
            }else{
                System.out.println("Impossible de prolonger la réservation");
                return false;
            }    
         } catch (Exception e) {
             
         }
        return false;
    }

    public static Reservation prolongerReservation(int idReservation, int duree_prolongation){ //Prolonger une réservation
        Reservation r = new Reservation();
        boolean checkReservation = checkProlongerReservation(idReservation);
        String query_datefin = "SELECT date_fin FROM reservation WHERE idReservation = (?)";
        Timestamp date_fin = new Timestamp(System.currentTimeMillis());
        try {
            PreparedStatement pstate = co.prepareStatement(query_datefin);
            pstate.setInt(1, idReservation);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                date_fin = rs.getTimestamp(1);     
            }
            
        } catch (Exception e) {
            //TODO: handle exception
        }
        if(checkReservation){ // Si la durée d'expiration est dans moins de 30 minutes
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date_fin.getTime());
            cal.add(Calendar.HOUR,duree_prolongation);
            Timestamp date_fin_convertie = new Timestamp(cal.getTime().getTime());
            List<Integer> bornesDispos = Borne.bornesDispoIntervalle(date_fin_convertie);
            if(!bornesDispos.isEmpty()){
                int nouvelleBorne = bornesDispos.get(0);
                String updateReservation = "UPDATE reservation SET date_fin = (?) WHERE idReservation = (?)";
                try {
                    PreparedStatement pstate2 = co.prepareStatement(updateReservation);
                    pstate2.setTimestamp(1, date_fin_convertie);
                    pstate2.setInt(2, idReservation);
                    pstate2.execute();
                } catch (Exception e) {
                    //TODO: handle exception
                }
                

                
            }      

        }
        //On récupère la réservation mise à jour
        r = recupeReservation(idReservation);
        System.out.println();

       
        return r;

    }

    public static Reservation recupeReservation(int idReservation){
        Reservation r = null;
        String query = "SELECT * from reservation WHERE idReservation = (?)";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setInt(1, idReservation);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                r = new Reservation(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getTimestamp(4),rs.getTimestamp(5),rs.getTimestamp(6),rs.getInt(7));
            }
            
        } catch (Exception e) {
            //TODO: handle exception
        }
        return r;

    }

    @Override
    public String toString() {
        return "Reservation [date_deb=" + date_deb + ", date_fin=" + date_fin + ", duree=" + duree + ", idBorne="
                + idBorne + ", idClient=" + idClient + ", idReservation=" + idReservation + "]";
    }

 /*   public Timestamp convertToTimestampViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
    }*/

    
}
