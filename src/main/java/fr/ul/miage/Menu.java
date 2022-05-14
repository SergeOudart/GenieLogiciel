package fr.ul.miage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class Menu {

    Dotenv dotenv = Dotenv.configure().load();
    private Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));

    public void Demarrer(){

        Scanner sc = new Scanner(System.in);
		System.out.println("Choisissez une option : Inscription (1) Connexion (2)");
        int nb1 = sc.nextInt();

        /**
         * Essayer de boucler sur inscription
         */
        switch(nb1) {
            case 1:
                inscription(sc);
			break;
            case 2:
                if(connexion(sc)) {
                    boolean menu = false;
                    Client client = new Client();
                    client.menu_client();
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
            System.out.println("Saisir un numéro de plaque (non obligatoire)");
            String plaque = sc.next();
            System.out.println("Choisissez votre role (administrateur, user, exploitant)");
            String role = sc.next();
            if(plaque.isEmpty()){
                plaque = "";
            }
            //database.addNewClient(pseudo,mdp,mail,plaque);
           try{
            if (verifEntry(pseudo, mdp, nom, prenom, num_tel, num_carte, mail, plaque)) {
                ajoutClient(pseudo, protect_mdp,nom,prenom,num_tel,num_carte, mail, plaque, role);
                System.out.println("Votre compte a bien été créé");
            } else 
            {
                System.out.println("Les identifiants passés ne respectent pas le bon format");
            }
           }catch(ClassNotFoundException e){
               e.printStackTrace();
           }
    }

    public boolean connexion(Scanner sc) {
        String password = "";
        boolean result = false;
        while(!result) {
            System.out.println("Voulez vous vous connecter avec votre numéro de réservation (1) ou votre plaque d'imatriculation (2)");
            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("Veuillez entrer votre numéro de réservation :");
                    String num_res = sc.nextLine();
                    System.out.println("Veuillez entrer votre mot de passe : ");
                    password = sc.nextLine();
                    result = connexionFromReservation(num_res, password);
                    break;
            
                case "2":
                    System.out.println("Veuillez entrer votre numéro de plaque :");
                    String plaque = sc.nextLine();
                    System.out.println("Veuillez entrer votre mot de passe : ");
                    password = sc.nextLine();
                    result = connexionFromPlaque(plaque,password);
                    break;
                default:
                    break;
            }
        }
        return result;
    
    }

    public boolean connexionFromPlaque(String plaque, String password) {
        String query = "SELECT mdp FROM client where plaque=(?)";
        String passwordCompare = "";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setString(1, plaque);
            ResultSet rs = pstate.executeQuery();

            while(rs.next()) {
                passwordCompare = rs.getString("mdp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (passwordCompare.equals(password)) {
            return true;
        }
        else {
            System.out.println("Erreur dans les identifiants !");
            return false;
        }
    }

    public boolean connexionFromReservation(String reservation, String password) {
        String query = "SELECT idClient FROM reservation where idReservation=(?)";
        String passwordCompare = "";
        String idClient = "";
        try {
            PreparedStatement pstate = co.prepareStatement(query);
            pstate.setString(1, reservation);
            ResultSet rs = pstate.executeQuery();

            while(rs.next()) {
                idClient = rs.getString("idClient");
            }

            query = "SELECT mdp FROM client where idCLient=(?)";
            pstate.setString(1, idClient);
            rs = pstate.executeQuery();

            while(rs.next()) {
                password = rs.getString("mdp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (passwordCompare.equals(password)) {
            return true;
        }
        else {
            System.out.println("Erreur dans les identifiants !");
            return false;
        }
    }



    public void ajoutClient(String pseudo,String mdp,String nom, String prenom, String num_tel, String num_carte, String mail, String plaque, String role) throws ClassNotFoundException{
        try{
            String queryClient = "INSERT INTO Client (pseudo,mdp,nom,prenom,num_tel,num_carte,mail,plaque,role) VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstate = co.prepareStatement(queryClient);
            pstate.setString(1, pseudo);
            pstate.setString(2, mdp);
            pstate.setString(3, nom);
            pstate.setString(4, prenom);
            pstate.setString(5, num_tel);
            pstate.setString(6, num_carte);
            pstate.setString(7, mail);
            pstate.setString(8, plaque);
            pstate.setString(9, role);
            pstate.execute();
            close(pstate,co);

        }catch(SQLException e){
            e.printStackTrace();
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

}
