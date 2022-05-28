package fr.ul.miage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


import fr.Exploitant;
import io.github.cdimascio.dotenv.Dotenv;

public class Menu {

    Dotenv dotenv = Dotenv.configure().load();
    private Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));

    public void demarrer(){

        Scanner sc = new Scanner(System.in);
		System.out.println("Choisissez une option : Inscription (1) Connexion (2) Connexion exploitant (3)");
        int nb1 = sc.nextInt();

        switch(nb1) {
            case 1:
                inscription(sc);
			break;
            case 2:
            String password = "";
            boolean result = false;
            String plaqueClient = "";
            int idReservation = 0;
            int idClient = 0;
            while(!result) {
                System.out.println("Voulez vous vous connecter avec votre numéro de réservation (1) ou votre plaque d'imatriculation (2) ou votre pseudo (3)");
                String choice = sc.nextLine();
                switch (choice) {
                    case "1":
                        System.out.println("Veuillez entrer votre numéro de réservation :");
                        int num_res = sc.nextInt();
                        System.out.println("Veuillez entrer votre mot de passe : ");
                        
                        password = sc.next();
                        result = connexionFromReservation(num_res, password);
                        if(result){
                            idClient = getClientIdByReservation(num_res);
                        }
                      
                        break;
                
                    case "2":
                        System.out.println("Veuillez entrer votre numéro de plaque :");
                        plaqueClient = sc.nextLine();
                        System.out.println("Veuillez entrer votre mot de passe : ");
                        password = sc.next();
                        result = connexionFromPlaque(plaqueClient,password);
                        if(result){
                            idClient = getClientIdByPlaque(plaqueClient);
                        }
                        
                        break;

                    case "3":
                        System.out.println("Veuillez entrer votre pseudo :");
                        String pseudo = sc.next();
                        System.out.println("Veuillez entrer votre mot de passe :");
                        password = sc.next();
                        result = connexionFromPseudo(pseudo, password);
                        if(result){
                            idClient = getClientIdByPseudo(pseudo);
                        }
                        break;
                    default:
                        break;
                }
            }
                if(result) {
                    Client client = new Client(idClient); 
                    client.menu_client();
                }
            break;
                case 3:
                    System.out.println("Veuillez saisir votre pseudo");
                    String pseudo = sc.next();
                    System.out.println("Veuillez saisir votre mot de passe");
                    String mdp = sc.next();
                    boolean connectExploitant = connexionExploitant(pseudo, mdp);
                    
                    if(connectExploitant){
                        int idExploitant = Exploitant.getExploitantIdByPseudo(pseudo);
                        Exploitant ex = new Exploitant(idExploitant);
                        ex.menu_exploitant();
                        //client.menu_client();
                    }
            
                break;

        } 
    }

    public void inscription(Scanner sc) {
        System.out.println("Saisir un pseudo");
            String pseudo = sc.next();
            System.out.println("Saisir un mot de passe");
            String mdp = sc.next();
            String protect_mdp = mdp;
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
            System.out.println("Choisissez votre role (administrateur, user, exploitant)");
            String role = sc.next();

           try{
            if (verifEntry(pseudo, mdp, nom, prenom, num_tel, num_carte, mail)) {
                ajoutClient(pseudo, protect_mdp,nom,prenom,num_tel,num_carte, mail, role);
                System.out.println("Votre compte a bien été créé");
            } else 
            {
                System.out.println("Les identifiants passés ne respectent pas le bon format");
            }
           }catch(ClassNotFoundException e){
               System.out.println("Impossible d'ajouter le client");
           }
    }

    public boolean connexionFromPlaque(String plaque, String password) {
        String query = "SELECT mdp FROM client,vehicule where client.idVehicule = vehicule.idVehicule AND vehicule.plaque=(?)";
        String passwordCompare = "";
        int idClient = 0;
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setString(1, plaque);
            ResultSet rs = pstate.executeQuery();

            while(rs.next()) {
                passwordCompare = rs.getString("mdp");
                
            }

        } catch (SQLException e) {
            System.out.println("Impossible de trouver cette plaque");
        }

        if (passwordCompare.equals(password)) {
            return true;
        }
        else {
            System.out.println("Erreur dans les identifiants !");
            return false;
        }
        
    }

    public boolean connexionFromReservation(int reservation, String password) {
        String query = "SELECT idClient FROM reservation where idReservation=(?)";
        String query2 = "SELECT mdp FROM client where idCLient=(?)";
        String passwordCompare = "";
        int idClient = 0;
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setInt(1, reservation);
            ResultSet rs = pstate.executeQuery();

            while(rs.next()) {
                idClient = rs.getInt("idClient");
            }
            System.out.println(idClient);
        } catch (SQLException e) {
            System.out.println("Impossible de trouver un client pour cette réservation");
        }
            
        try {
            PreparedStatement pstate2 = co.prepareStatement(query2);
            pstate2.setInt(1, idClient);
            ResultSet rs2;
            rs2 = pstate2.executeQuery();

            while(rs2.next()) {
                passwordCompare = rs2.getString(1);
            }
            
        } catch (Exception e) {
            System.out.println("Impossible de récupérer le mot de passe ");
        }
                

      

        if (passwordCompare.equals(password)) {
            return true;
        }
        else {
            System.out.println("Erreur dans les identifiants !");
            return false;
        }
    }

    public boolean connexionExploitant(String pseudo, String mdp){
        String query = "SELECT idClient,mdp,role FROM client WHERE pseudo =(?)";
        int idExploitant = 0;
        String checkMdp = "";
        String role= "";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setString(1,pseudo);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                idExploitant = rs.getInt(1);
                checkMdp = rs.getString(2);
                role = rs.getString(3);
            }
        } catch (Exception e) {
            System.out.println("Impossible de récupérer l'xploitant");
        }
        if(checkMdp.equals(mdp) && role.equals("exploitant")){
            return true;
        }else{
            System.out.println("Veuillez saisir des identifiants valides");
            return false;
        }
    }

    public boolean connexionFromPseudo(String pseudo, String password) {
        String query = "SELECT mdp,role FROM client WHERE pseudo=(?)";
        String mdp = "";
        String role = "";

        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setString(1, pseudo);
            ResultSet rs = pstate.executeQuery();
            if (rs.next()) {
                mdp = rs.getString(1);
                role = rs.getString(2);
            }
        } catch (SQLException e) {
            System.out.println("Impossible de trouver un client pour ces identifiants");
        }
        if(mdp.equals(password) && role.equals("user")){
            return true;
        } else {
            System.out.println("Veuillez entrer des indentifiants valides");
            return false;
        }
    }

    public boolean checkPassword(String str1, String str2){
        if(str1.equals(str2)){
            return true;
        }else{
            return false;
        }

    }



    public void ajoutClient(String pseudo,String mdp,String nom, String prenom, String num_tel, String num_carte, String mail, String role) throws ClassNotFoundException{
        try{
            String queryClient = "INSERT INTO Client (pseudo,mdp,nom,prenom,num_tel,num_carte,mail,role) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement pstate = co.prepareStatement(queryClient);
            pstate.setString(1, pseudo);
            pstate.setString(2, mdp);
            pstate.setString(3, nom);
            pstate.setString(4, prenom);
            pstate.setString(5, num_tel);
            pstate.setString(6, num_carte);
            pstate.setString(7, mail);
            pstate.setString(8, role);
            pstate.execute();
            close(pstate,co);

        }catch(SQLException e){
            System.out.println("Impossible d'ajouter le client");
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

    public boolean verifEntry(String pseudo, String mdp, String nom, String prenom, String num_tel, String num_carte, String mail) 
    {
        boolean verif = true;

        verif = nom.matches("[A-Za-z]+") 
        && prenom.matches("[A-Za-z]+") 
        && num_tel.matches("[0-9]+") 
        && num_carte.matches("[0-9]+")
        && mail.matches("^(.+)@(.+)$");
        return verif;
    }

    public int getClientIdByPlaque(String plaque){
        int id = 0;
        try {
            String queryClient = "SELECT idClient FROM client,vehicule where client.idVehicule = vehicule.idVehicule AND vehicule.plaque=(?)";
            PreparedStatement pstate = co.prepareStatement(queryClient);
            pstate.setString(1, plaque);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                id = rs.getInt(1);
            }
            
        } catch (Exception e) {
            System.out.println("Impossible de récupérer un client avec cette plaque");
        }
        return id;
    }

    public int getClientIdByPseudo(String pseudo){
        int id = 0;
        try {
            String queryClient = "SELECT idClient FROM client where pseudo=(?)";
            PreparedStatement pstate = co.prepareStatement(queryClient);
            pstate.setString(1, pseudo);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                id = rs.getInt(1);
            }
            
        } catch (Exception e) {
            System.out.println("Impossible de récupérer un client avec ce pseudo");
        }
        return id;
    }

    public int getClientIdByReservation(int idReservation){
        int id = 0;
        try {
            String queryClient = "SELECT idClient FROM reservation where idReservation=(?)";
            PreparedStatement pstate = co.prepareStatement(queryClient);
            pstate.setInt(1, idReservation);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                id = rs.getInt(1);
            }
            
        } catch (Exception e) {
            System.out.println("Impossible de récupérer un client avec cette réservation");
        }
        return id;
    }

}
