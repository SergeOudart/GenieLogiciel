package fr.ul.miage;
import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class Client extends DatabaseConnection{


    
   

    

    public Client(){
        super();
    }

    public void ajoutClient(String pseudo,String mdp, String mail, String plaque) throws ClassNotFoundException{
        try{
            Dotenv dotenv = null;
            dotenv = Dotenv.configure().load();
            Connection co = dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
            String queryClient = "INSERT INTO Client (pseudo,mdp,mail,plaque) VALUES (?,?,?,?)";
            PreparedStatement pstate = co.prepareStatement(queryClient);
            pstate.setString(1, pseudo);
            pstate.setString(2, mdp);
            pstate.setString(3, mail);
            pstate.setString(4, plaque);
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


	


    public void close(PreparedStatement requete, Connection co) throws SQLException {
		if (requete != null) {
			requete.close();
		}
		if (co != null) {
			co.close();
		}
	}


}