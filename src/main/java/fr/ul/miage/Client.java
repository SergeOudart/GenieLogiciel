package fr.ul.miage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class Client extends DatabaseConnection{    

    public Client(){
        super();
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


	


    public void close(PreparedStatement requete, Connection co) throws SQLException {
		if (requete != null) {
			requete.close();
		}
		if (co != null) {
			co.close();
		}
	}


}