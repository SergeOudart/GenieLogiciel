package fr.ul.miage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class Reservation extends DatabaseConnection{

    private int idReservation;
    private int idClient;
    private int idBorne;
    private LocalDate date_deb;
    private LocalDate date_fin;

    public Reservation(int idReservation, int idClient, int idBorne, LocalDate date_deb, LocalDate date_fin, int duree) {
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

    public LocalDate getDate_deb() {
        return this.date_deb;
    }

    public void setDate_deb(LocalDate date_deb) {
        this.date_deb = date_deb;
    }

    public LocalDate getDate_fin() {
        return this.date_fin;
    }

    public void setDate_fin(LocalDate date_fin) {
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
   



    public static Reservation verifReservation(int idClient){
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        Connection co = dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
        String queryReservation = "SELECT * FROM reservation WHERE idClient = (?)";
        int idBorne;
        Date dt_deb;
        Date dt_fin;
        int duree;
        List<Reservation> lr = new ArrayList<Reservation>();
        Reservation r = null;
        
        try {
            PreparedStatement pstate = co.prepareStatement(queryReservation);
            pstate.setInt(1, idClient);
            ResultSet rs =  pstate.executeQuery();
            System.out.println("1111");

           /* while(rs.next()){
                 r = new Reservation(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getDate(4), rs.getDate(5),rs.getInt(6));
                System.out.println(r.toString());
                lr.add(r);

            }*/
            if(rs.next()){
                System.out.println("ya des données");
            }else{
                System.out.println("pas de données");
            }

            /*if(rs.next()){
                idBorne = rs.getInt(1);
            

            } */
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("e");
        }

        return r;


    }


    public static Reservation affecterReservation(int idClient,LocalDate date_deb, int duree){
        Reservation r = null;
        List<Integer> bornes_dispos = new ArrayList<Integer>();
        bornes_dispos.addAll(Borne.bornesDispo());
        System.out.println(bornes_dispos.toString());
        int id_borne_dispo = 0;
        int idReservation = 0;

        if(!bornes_dispos.isEmpty()){
            id_borne_dispo = bornes_dispos.get(0);
        }
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        Connection co = Client.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
        String queryReservation = "INSERT INTO Reservation (idClient,idBorne,date_deb,date_fin,duree) VALUES (?,?,?,?,?) ";

        try {
            PreparedStatement pstate = co.prepareStatement(queryReservation);
            pstate.setInt(1, idClient);
            pstate.setInt(2,id_borne_dispo);
            Date convert_datedeb = Date.valueOf(date_deb);
            pstate.setDate(3, convert_datedeb);
            LocalDate date_fin = date_deb.plusDays(duree);
            Date convert_datefin = Date.valueOf(date_fin);
            pstate.setDate(4, convert_datefin);
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

    @Override
    public String toString() {
        return "Reservation [date_deb=" + date_deb + ", date_fin=" + date_fin + ", duree=" + duree + ", idBorne="
                + idBorne + ", idClient=" + idClient + ", idReservation=" + idReservation + "]";
    }
   
    
}
