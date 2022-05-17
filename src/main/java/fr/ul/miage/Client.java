package fr.ul.miage;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
            System.out.println("Menu client : Se déconnecter (3) Modifier des infos client (4) Supprimer un client (5) Réserver une borne (6) Verifier reservation (7) Bornes dispo (8) Quitter (8)");
            int choice = sc.nextInt();

            switch (choice) {            
                case 2:
                    System.out.println("Numéro de plaque / numéro de réservation : \n");
                    String identifiant = sc.next();

                    // database.fetchPassword(identifiant);
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
                        // TODO Auto-generated catch block
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
                
                    //System.out.println("Le client a été supprimé");
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
        // int idClient = Menu.getClientIdByPlaque(plaque);
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
    


}