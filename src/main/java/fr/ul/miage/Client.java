package fr.ul.miage;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import io.github.cdimascio.dotenv.Dotenv;

public class Client { 
    static Dotenv dotenv = Dotenv.configure().load();
    private static Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
    
    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }




    private int idClient;

    public Client(){
        super();
    }

   
    public Client(int idClient) {
        this.idClient = idClient;
    }

    public void menu_client()
    {
        Scanner sc = new Scanner(System.in);
        Reservation r = new Reservation();
        List<Reservation> lr = new ArrayList<Reservation>();
        boolean quitter = false;

        while(!quitter) {
            System.out.println("Menu client : Se présenter à une borne (2) Se déconnecter (3) Modifier des infos client (4) Supprimer un client (5) Réserver une borne (6) Verifier reservation (7) Bornes dispo (8) Quitter (8)");
            int choice = sc.nextInt();

            switch (choice) {            
                case 2:
                    presenterBorne();
                    
                    break;

                default:
                    break;
                case 3:
                    System.out.println("Vous avez été déconnecté de l'application");
                    break;
                case 4:
                    System.out.println("Id du client à modifier");
                    int id_cl_modif = sc.nextInt();
                    System.out.println("Saisir un nouveau pseudo");
                    String nv_pseudo = sc.next();
                    System.out.println("Saisir un nouveau mdp");
                    String nv_mdp = sc.next();
                    System.out.println("Saisir un nouveau mail");
                    String nv_mail = sc.next();
                    System.out.println("Saisir une nouvelle plaque");
                    String nv_plaque = sc.next();
                    modifierClient(id_cl_modif, nv_pseudo, nv_mdp, nv_mail, nv_plaque);
                    break;

                case 5:
                    System.out.println("Saisir l'id du client à supprimer");
                    int id_client = sc.nextInt();
                    try{
                        supprimerClient(id_client);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 6:
                System.out.println("Saisir un numéro d'immatriculation");
                String num_immatriculation = sc.next();
                System.out.println(idClient);
                verifPlaque(num_immatriculation, idClient);
                System.out.println("Saisir une date de début concernant la réservation");
                String date = sc.next();
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // java.util.Date parsedDate = dateFormat.parse(date);
                    // Timestamp date_deb = new java.sql.Timestamp(parsedDate.getTime());
                    Timestamp date_deb = new Timestamp((dateFormat.parse(date)).getTime());
                    System.out.println("Saisir une heure de début");
                    int heure_debut = sc.nextInt();
                    System.out.println("Saisir une durée pour la réservation");
                    int duree_reservation = sc.nextInt();
                    r = Reservation.affecterReservation(idClient, date_deb,heure_debut, duree_reservation);
                    System.out.println(r.toString());

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;

                case 7:
                    System.out.println("Saisir l'id d'un client pour vérifier ses réservations");
                    int id_cl_reservation = sc.nextInt();
                    List<Reservation> liste_reservations = new ArrayList<>();
                    try {
                        liste_reservations.addAll(Reservation.verifReservation(id_cl_reservation));
                        System.out.println(liste_reservations.toString());
                    
                    //List<Reservation> checkReservation = new ArrayList<Reservation>();
                    //checkReservation.add((Reservation) Reservation.verifReservation(id_cl_reservation));
                 
                    // System.out.println(checkReservation.toString());
                        
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                    

                    case 8:
                        quitter = true;
                    break;
                    case 9:
                    System.out.println("Saisir un numéro de réservation");
                    int num = sc.nextInt();
                    System.out.println("Saisir la durée de prolongation");
                    int duree_pro = sc.nextInt();
                    Reservation r2 = Reservation.prolongerReservation(num,duree_pro);


                    break;
                
                }
            }
        sc.close();
    }

    
    public void supprimerClient(int id) throws ClassNotFoundException {
		try {
            Dotenv dotenv = null;
            dotenv = Dotenv.configure().load();
            Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
			PreparedStatement requete = co.prepareStatement("DELETE from Client where idClient=(?)");
			requete.setInt(1, id);
            requete.execute();
			// int nb = requete.executeUpdate();
			close(requete, co);
		}
		catch(SQLException sqle) {
            sqle.printStackTrace();
				
		}
	}

    public void modifierClient(int id_client,String pseudo, String mdp, String email, String plaque) {
		
		try {
            Dotenv dotenv = null;
            dotenv = Dotenv.configure().load();
			Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
			PreparedStatement requete = co.prepareStatement("UPDATE Client SET pseudo=(?),mdp = (?),email =(?),plaque=(?),WHERE id_client =(?)");
			requete.setString(1, pseudo);
			requete.setString(2, mdp);
			requete.setString(3, email);
			requete.setString(4, plaque);
			requete.setInt(5, id_client);
			// int nblignes = requete.executeUpdate();	
            requete.execute();

			close(requete, co);
			
		}
		catch(SQLException sqle) {
			System.out.println("Erreur" + sqle);
	
	
		}
		
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
            int num_res = sc.nextInt();
            try {
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
            
                        while(rs.next()) {
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
                        System.out.println("Vous avez dépassé le délais d'attente, vérification des bornes disponibles...");
                        //verifBornesDIspo
                    }
            
                }

            } catch (SQLException e) 


            {
            }
                break;
        
            default:
                break;
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

        System.out.println("Votre facture : \n"
        + "idClient : " + idClient + "\n"
        + "Numéro de reservation : " + idReservation + "\n"
        + "Prix : " + prix + " euros \n"
        + "Date : " + date);
    }

    public boolean dansPeriodeAttente(Timestamp date_deb) {
       
        Timestamp heure_actu = new Timestamp(System.currentTimeMillis());
        boolean result = false;

        long time = heure_actu.getTime() - date_deb.getTime() + 900000;

        if (TimeUnit.MILLISECONDS.toMinutes(time) > 0) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }


    public boolean verifEntry(String pseudo, String mdp, String nom, String prenom, String num_tel, String num_carte, String mail, String plaque, String role) 
    {
        boolean verif = true;

        verif = nom.matches("[A-Za-z]+") 
        && prenom.matches("[A-Za-z]+") 
        && num_tel.matches("[0-9]+") 
        && num_carte.matches("[0-9]+")
        && mail.matches("^(.+)@(.+)$")
        && plaque.matches("[0-9]*")
        && (role.equals("user") || role.equals("admin") || role.equals("exploitant"));

        return verif;
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

    public void close(PreparedStatement requete, Connection co) throws SQLException {
		if (requete != null) {
			requete.close();
		}
		if (co != null) {
			co.close();
		}
	}

   

    public void verifPlaque(String plaque,int idClient){
        String resRequete="";
        int idVehicule = 0;
        boolean verifAssociation = false;
        boolean estLoue = false;
        try {
            String queryPlaqueExiste = "SELECT plaque,vehicule.idVehicule,estLoue FROM vehicule,client WHERE plaque = (?) AND vehicule.idVehicule = client.idVehicule";
            PreparedStatement ps = co.prepareStatement(queryPlaqueExiste);
            ps.setString(1, plaque);
            ResultSet rs1 = ps.executeQuery();
            if(rs1.next()){
                System.out.println("La plaque renseignée est la même que dans le profil client");
            }else{
                String queryClient = "SELECT plaque,vehicule.idVehicule,estLoue FROM vehicule,client WHERE plaque = (?)";
                PreparedStatement pstate = co.prepareStatement(queryClient);
                pstate.setString(1, plaque);
                ResultSet rs = pstate.executeQuery();
                if(rs.next()){
                    resRequete = rs.getString(1);
                    idVehicule = rs.getInt(2);
                    estLoue = rs.getBoolean(3); 
                }
                if(estLoue == false){
                    String queryTemporaire = "INSERT INTO clientvehicule (idClient,idVehicule) VALUES (?,?)";
                    PreparedStatement pstate2 = co.prepareStatement(queryTemporaire);
                    pstate2.setInt(1,idClient);
                    pstate2.setInt(2,idVehicule);
                    pstate2.execute();
                } 

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

        public static long compareTwoTimeStamps(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime)
    {
        long milliseconds1 = oldTime.getTime();
        long milliseconds2 = currentTime.getTime();

        long diff = milliseconds2 - milliseconds1;
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return diffMinutes;
    }

}