package fr.ul.miage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import io.github.cdimascio.dotenv.Dotenv;

public class PresentationBorne {
    
    static Dotenv dotenv = Dotenv.configure().load();
    private static Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
    private int idClient;

    PresentationBorne(int idClient) {
        this.idClient = idClient;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }



    public void presenterBorne() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Avez vous un numéro de réservation ? (oui/non)");
        String num_res_exist = sc.next();
        int idClient = 0;
        int idBorne = 0;
        Timestamp date_deb = new Timestamp(System.currentTimeMillis());
        Timestamp date_fin = new Timestamp(System.currentTimeMillis());
        int duree = 0;
        switch (num_res_exist) {
            case "oui":
            System.out.println("Entrez votre numéro de réservation :");
            try {
                int num_res = sc.nextInt();
                PreparedStatement requete = co.prepareStatement("SELECT * from reservation where idReservation=(?)");
                requete.setInt(1, num_res);
                ResultSet rs = requete.executeQuery();

                while (rs.next()) {
                    idClient = rs.getInt("idClient");
                    idBorne = rs.getInt("idBorne");
                    date_deb = rs.getTimestamp("date_deb");
                    date_fin = rs.getTimestamp("date_fin");
                    duree = rs.getInt("duree");
                }
                reservationFound(num_res, idClient, idBorne, date_deb, date_fin, duree, sc);

            } catch (SQLException | InputMismatchException e) 
            {
                System.out.println("La réservation rensignée n'éxiste pas, veuillez ressayer");
            }
                break;
        
            case "non":
                System.out.println("Veuillez rentrer votre plaque d'immatriculation : ");
                String plaque = sc.next();

                int idReservation = 0;
                boolean reserExiste = false;
                String query = "SELECT idReservation from reservation,client,vehicule where vehicule.plaque=(?) AND vehicule.idVehicule = client.idVehicule AND reservation.idClient = client.idClient";
                try {
                    PreparedStatement pstate = co.prepareStatement(query);
                    pstate.setString(1, plaque);
                    ResultSet result = pstate.executeQuery();

                    while(result.next()) {
                        idReservation = result.getInt("idReservation");
                        reserExiste = true;
                    }
                } catch (SQLException e) {
                    reserExiste = false;
                }

                if (reserExiste) {
                    reservationFound(idReservation, idClient, idBorne, date_deb, date_fin, duree, sc);
                } else {
                    int dureeNoRes = readInt(sc, "Veuillez préciser la durée de recharge prévue : ", "Veuillez entrer une durée");
                    Timestamp date_arrivee = new Timestamp(System.currentTimeMillis());
                    List<Integer> borneDispo = Borne.bornesDispoDebutFin(date_arrivee, dureeNoRes);
                    if (!borneDispo.isEmpty()) {
                        System.out.println("La borne " + borneDispo.get(0) + " est disponible.");
                        Borne.setOccupeeParId(borneDispo.get(0));
                        assocTemporaire(idClient, plaque);      //Creér l'association temporaire entre un client et un véhicule
                        boolean fini = false;
                        while(!fini) {
                            System.out.println("Voulez vous arrêter le rechargement ? (oui)");
                            String choix = sc.next();
                            if (choix.equals("oui")) {
                                fini = true;
                            }
                        }
                        Timestamp date_depart = new Timestamp(System.currentTimeMillis());
                        Borne.setDispoParId(borneDispo.get(0));
                        long time = date_depart.getTime() - date_deb.getTime();
                        long hours = TimeUnit.MILLISECONDS.toHours(time);
                        int prix = paiement(hours, 0, false);
                        facturationSansReservation(idClient, prix, date_depart);        //TODO facture à 0 euros problème
                        dropAssocTemporaire(idClient);
                    }
                }
                break;

            default:
                System.out.println("Choisissez une des options ci dessus ");
                break;
        }
    }


    public void reservationFound(int num_res, int idClient, int idBorne, Timestamp date_deb, Timestamp date_fin, int duree, Scanner sc) {
        Reservation reservation = new Reservation(num_res,idClient,idBorne,date_deb,date_fin,duree);
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

         /**
         * >= 0 --> A l'heure ou en avance
         * < 0 --> En retard
         */
        if(date_deb.compareTo(Timestamp.valueOf(sdf1.format(timestamp))) >= 0) { 
            System.out.println("Vous êtes à l'heure, rechargement du véhicule ...");
            reservation.changerBorneEtatReserveeParIdReservation(num_res);
            boolean fini = false;
            while(!fini) {
                System.out.println("Voulez vous arrêter le rechargement ? (oui)");
                String choix = sc.next();
                if (choix.equals("oui")) {
                    fini = true;
                }
            }
            arriverATemps(num_res, reservation, date_deb, date_fin, sdf1);

        } else if(date_deb.compareTo(Timestamp.valueOf(sdf1.format(timestamp))) < 0) {
            System.out.println("Vous êtes en retard, ");
            String query = "SELECT date_deb from reservation where idReservation=(?)";

            try {
                PreparedStatement stmt = co.prepareStatement(query);
                stmt.setInt(1, num_res);
                ResultSet rsRetard = stmt.executeQuery();
    
                while(rsRetard.next()) {
                    date_deb = rsRetard.getTimestamp("date_deb");
                }
            } catch (SQLException e) {

            }

            if (dansPeriodeAttente(date_deb)) {
                System.out.println("Vous êtes dans la période d'attente.");
                reservation.changerBorneEtatReserveeParIdReservation(num_res);
                boolean fini = false;
                while(!fini) {
                    System.out.println("Voulez vous arrêter le rechargement ? (oui)");
                    String choix = sc.next();
                    if (choix.equals("oui")) {
                        fini = true;
                    }
                }
                arriverATemps(num_res, reservation, date_deb, date_fin, sdf1);

                
            } else {
                System.out.println("Vous avez dépassé le délais d'attente, vérification des bornes disponibles");

                int dureeRetard = readInt(sc, "Combien de temps voulez vous rester ?", "Veuillez rentrer une durée");

                Timestamp date_arrivee = new Timestamp(System.currentTimeMillis());
                List<Integer> borneDispo = Borne.bornesDispoDebutFin(date_arrivee, dureeRetard); //TODO vérifier cette fonction (ne marche pas)
                if (!borneDispo.isEmpty()) {
                    System.out.println("La borne " + borneDispo.get(0) + " est disponible.");
                    Borne.setOccupeeParId(borneDispo.get(0));
                    boolean fini = false;
                    while(!fini) {
                        System.out.println("Voulez vous arrêter le rechargement ? (oui)");
                        String choix = sc.next();
                        if (choix.equals("oui")) {
                            fini = true;
                        }
                    }
                    Timestamp date_depart = new Timestamp(System.currentTimeMillis());
                    Borne.setDispoParId(borneDispo.get(0));
                    long time = date_depart.getTime() - date_deb.getTime();
                    long hours = TimeUnit.MILLISECONDS.toHours(time);
                    int prix = paiement(hours, 0, false);
                    facturationSansReservation(idClient, prix, date_depart);        //TODO facture à 0 euros problème
                }
            }
        }
    }

    public void arriverATemps(int num_res, Reservation reservation, Timestamp date_deb, Timestamp date_fin, SimpleDateFormat sdf1) {
        Timestamp heure_depart = new Timestamp(System.currentTimeMillis());
        heure_depart = Timestamp.valueOf(sdf1.format(heure_depart));
        //Timestamp heure_depart = getHeureDepart();                    /* TODO Pour tester les dépassement d'heures / pas supprimer*/
        finaliserRecharge(num_res, heure_depart,reservation);
        

        long time = heure_depart.getTime() - date_deb.getTime();
        long hours = TimeUnit.MILLISECONDS.toHours(time);

        long depass = heure_depart.getTime() - date_fin.getTime();
        long minutes_dep = TimeUnit.MILLISECONDS.toMinutes(depass);

        int prix = paiement(hours, minutes_dep, false);

        facturation(idClient, num_res, prix, heure_depart);
    }

    public void finaliserRecharge(int idReservation, Timestamp date_depart, Reservation reservation) {
        System.out.println("Rechargement terminé");
        reservation.changerBorneEtatDispoParIdReservation(idReservation);
        String query = "UPDATE reservation SET date_depart = (?) where idReservation=(?)";
        try {
            PreparedStatement requete = co.prepareStatement(query);
            requete.setTimestamp(1, date_depart);
            requete.setInt(2, idReservation);
            requete.executeUpdate();     //A mute pour tester
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int paiement(long heures, long depassement, boolean non_pres) {
        System.out.println("Paiement en cours ...");
        int prix = 0;
        if (depassement <= 0) {
            int frais = getFrais("frais");
            prix = (int) heures * frais;
        } if (depassement > 0) {
            int frais = getFrais("frais");
            int frais_depassement = getFrais("depassement");
            prix = (int) ((heures * frais) + (depassement * frais_depassement)); 
        }
        
        if (non_pres) {
            int frais_non_pres = getFrais("non_pres");
            prix = (int) heures * frais_non_pres;
        }
        
        return prix;
    }

    public int getFrais(String choix) {
        String query = "SELECT * from frais";
        Statement stmt;
        int frais = 0;
        int frais_depassement = 0;
        int frais_non_pres = 0;
        try {
            stmt = co.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                frais = rs.getInt(1);
                frais_depassement = rs.getInt(2);
                frais_non_pres = rs.getInt(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        if (choix.equals("frais")) {
            return frais;
        } else if(choix.equals("depassement")) {
            return frais_depassement;
        } else if (choix.equals("non_pres")) {
            return frais_non_pres;
        } else {
            return 0;
        }
    }

    public boolean dansPeriodeAttente(Timestamp date_deb) {
       
        Timestamp heure_actu = new Timestamp(System.currentTimeMillis());
        boolean result = false;

        long time = heure_actu.getTime() - date_deb.getTime();

        System.out.println(TimeUnit.MILLISECONDS.toMinutes(time));

        if (TimeUnit.MILLISECONDS.toMinutes(time) < 15) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    public void facturation(int idClient, int idReservation, int prix, Timestamp date) {
        String query = "INSERT INTO facture(idClient, idReservation, prix, date) values (?,?,?,?)";
        try {
            PreparedStatement stmt = co.prepareStatement(query);
            stmt.setInt(1, idClient);
            stmt.setInt(2, idReservation);
            stmt.setInt(3, prix);
            stmt.setTimestamp(4, date);
            stmt.execute();
        } catch(SQLException e) {

        }
    }

    public void facturationSansReservation(int idClient, int prix, Timestamp date) {
        String query = "INSERT INTO facture(idClient, prix, date) values (?,?,?)";
        try {
            PreparedStatement stmt = co.prepareStatement(query);
            stmt.setInt(1, idClient);
            stmt.setInt(2, prix);
            stmt.setTimestamp(3, date);
            stmt.execute();
        } catch(SQLException e) {

        }
    }

    public Timestamp getHeureDepart() {
        String query = "SELECT date_depart from reservation where idReservation=2";
        Timestamp heure = new Timestamp(System.currentTimeMillis());
        try {
            Statement stmt = co.createStatement();
            ResultSet rs= stmt.executeQuery(query);

            while(rs.next()) {
                heure = rs.getTimestamp("date_depart");
            }

        } catch(SQLException e) {

        }
        return heure;
    }

    public void assocTemporaire(int idClient, String plaque) {
        int idVehicule = 0;
        String query = "SELECT idVehicule from vehicule where plaque=(?)";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setString(1, plaque);
            ResultSet result = pstate.executeQuery();

            while(result.next()) {
                idVehicule = result.getInt(idVehicule);
            }
        } catch (SQLException e) {

        }
        String queryInsert = "INSERT INTO clientvehicule(idClient, idVehicule) values ((?),(?))";
        try {
            PreparedStatement pstate2 = co.prepareStatement(queryInsert);
            pstate2.setInt(1, idClient);
            pstate2.setInt(2, idVehicule);
            pstate2.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropAssocTemporaire(int idClient) {
        String query = "DELETE FROM clientvehicule WHERE idClient=(?)";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setInt(1, idClient);
            pstate.execute();
        } catch (SQLException e) {

        }
    }

    public static int readInt(Scanner scanner, String prompt, String promptOnError) {
 
        System.out.println(prompt);
     
        while ( !scanner.hasNextInt() ) {
            scanner.nextLine();
            System.out.println(promptOnError);
        }
     
        final int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }
}
