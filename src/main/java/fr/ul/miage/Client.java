package fr.ul.miage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class Client extends DatabaseConnection{    

    public Client(){
        super();
    }

    public void menu_client()
    {
        System.out.println("Menu client : Inscription (1) Connexion (2) Se déconnecter (3) Modifier des infos client (4) Supprimer un client (5)  Verifier reservation (6) Bornes dispo (7) Quitter (8)");
        Scanner sc = new Scanner(System.in);
		
		Client cl = new Client();

        Reservation r = new Reservation();
        List<Reservation> lr = new ArrayList<Reservation>();

        int choice = sc.nextInt();

        switch (choice) {
            case 1:
            System.out.println("Saisir un pseudo");
            String pseudo = sc.next();
            System.out.println("Saisir un mot de passe");
            String mdp = sc.next();
            String protect_mdp = cl.encryptPassword(mdp);
            System.out.println("Saisir votre nom");
            String nom = sc.next();
            System.out.println("Saisir votre prenom");
            String prenom = sc.next();
            System.out.println("Saisir votre numéro de téléphone");
            String num_tel = sc.next();
            System.out.println("Saisir votre numéro de carte bancaire");
            String num_carte = sc.next();
            System.out.println("Saisir un mail");
            String mail = sc.next();
            System.out.println("Saisir un numéro de plaque (non obligatoire)");
            String plaque = sc.next();
            if(plaque.isEmpty()){
                plaque = "";
            }
            //database.addNewClient(pseudo,mdp,mail,plaque);
           try{
            if (verifEntry(pseudo, mdp, nom, prenom, num_tel, num_carte, mail, plaque)) {
                cl.ajoutClient(pseudo, protect_mdp,nom,prenom,num_tel,num_carte, mail, plaque);
                System.out.println("Votre compte a bien été créé");
            } else 
            {
                System.out.println("Les identifiants passés ne respectent pas le bon format");
            }
           }catch(ClassNotFoundException e){
               e.printStackTrace();
           }
                
                break;
            
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
                cl.modifierClient(id_cl_modif, nv_pseudo, nv_mdp, nv_mail, nv_plaque);
                break;

            case 5:
                System.out.println("Saisir l'id du client à supprimer");
                int id_client = sc.nextInt();
                try{
                    cl.supprimerClient(id_client);

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case 6:
                System.out.println("Saisir l'id d'un client pour vérifier ses réservations");
                int id_cl_reservation = sc.nextInt();
                try {
                    lr.add(Reservation.verifReservation(id_cl_reservation));
                
                //List<Reservation> checkReservation = new ArrayList<Reservation>();
                //checkReservation.add((Reservation) Reservation.verifReservation(id_cl_reservation));
                System.out.println(Reservation.verifReservation(id_cl_reservation));
                // System.out.println(checkReservation.toString());
                System.out.println("op");

                
                    
                    
                } catch (Exception e) {
                    //TODO: handle exception
                    System.out.println("marche pas");
                }

                break;
                case 7:
                    System.out.println(Borne.bornesDispo());
                break;
            
                //System.out.println("Le client a été supprimé");

        }

        sc.close();

    }

    public void ajoutClient(String pseudo,String mdp,String nom, String prenom, String num_tel, String num_carte, String mail, String plaque) throws ClassNotFoundException{
        try{
            Dotenv dotenv = null;
            dotenv = Dotenv.configure().load();
            Connection co = dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
            String queryClient = "INSERT INTO Client (pseudo,mdp,nom,prenom,num_tel,num_carte,mail,plaque) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement pstate = co.prepareStatement(queryClient);
            pstate.setString(1, pseudo);
            pstate.setString(2, mdp);
            pstate.setString(3, nom);
            pstate.setString(4, prenom);
            pstate.setString(5, num_tel);
            pstate.setString(6, num_carte);
            pstate.setString(7, mail);
            pstate.setString(8, plaque);
            pstate.execute();
            close(pstate,co);

        }catch(SQLException e){
            e.printStackTrace();
        }
        
    }
    public void supprimerClient(int id) throws ClassNotFoundException {
		try {
            Dotenv dotenv = null;
            dotenv = Dotenv.configure().load();
            Connection co = dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
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
			Connection co = dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
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

    //Protéger les mdp qui sont mis en base de données avec un hachage
    public String encryptPassword(String mdp){
        try {
            //Applique l'algorithme de hachage 
            MessageDigest digestor = MessageDigest.getInstance("SHA-256");
            byte[] hash = digestor.digest(mdp.getBytes(StandardCharsets.UTF_8));
            StringBuilder valCryptee = new StringBuilder(2 * hash.length);

            for (int i = 0; i < hash.length; i++) {
                String hexVal = Integer.toHexString(0xff & hash[i]);
                if (hexVal.length() == 1) {
                    valCryptee.append('0');
                }
                valCryptee.append(hexVal);
                
            }
            return valCryptee.toString();
    
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return e.getMessage();
        }
    }

    public boolean verifEntry(String pseudo, String mdp, String nom, String prenom, String num_tel, String num_carte, String mail, String plaque) 
    {
        boolean verif = true;

        verif = nom.matches("[A-Za-z]+") 
        && prenom.matches("[A-Za-z]+") 
        && num_tel.matches("[0-9]+") 
        && num_carte.matches("[0-9]+")
        && mail.matches("^(.+)@(.+)$")
        && plaque.matches("[0-9]*");

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


}