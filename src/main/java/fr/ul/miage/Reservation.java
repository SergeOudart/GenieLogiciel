package fr.ul.miage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.cdimascio.dotenv.Dotenv;

public class Reservation extends DatabaseConnection{

    private int idReservation;
    private int idClient;
    private int idBorne;
    private Date date_deb;
    private Date date_fin;

    public Reservation(int idReservation, int idClient, int idBorne, Date date_deb, Date date_fin, int duree) {
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

    public Date getDate_deb() {
        return this.date_deb;
    }

    public void setDate_deb(Date date_deb) {
        this.date_deb = date_deb;
    }

    public Date getDate_fin() {
        return this.date_fin;
    }

    public void setDate_fin(Date date_fin) {
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


    public boolean affecterReservation(int idClient){

        boolean checkReservation = false;
        List<Reservation> lr = null;
        lr.add(verifReservation(idClient));

        if(lr.isEmpty()){
            
        }




        return checkReservation;

    }
   
    
}
