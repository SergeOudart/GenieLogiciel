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


    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNumTel() {
        return numTel;
    }

    public void setNumTel(int numTel) {
        this.numTel = numTel;
    }

    public int getNumCarte() {
        return numCarte;
    }

    public void setNumCarte(int numCarte) {
        this.numCarte = numCarte;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getIdVehicule() {
        return idVehicule;
    }

    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }

    public int getNbReservationsPerma() {
        return nbReservationsPerma;
    }

    public void setNbReservationsPerma(int nbReservationsPerma) {
        this.nbReservationsPerma = nbReservationsPerma;
    }

    private String pseudo;
    private String mdp;
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    private String nom;
    private String prenom;
    private int numTel;
    private int numCarte;
    private String mail;
    private String role;
    @Override
    public String toString() {
        return "Client [idClient=" + idClient + ", idVehicule=" + idVehicule + ", mail=" + mail + ", mdp=" + mdp
                + ", nbReservationsPerma=" + nbReservationsPerma + ", nom=" + nom + ", numCarte=" + numCarte
                + ", numTel=" + numTel + ", prenom=" + prenom + ", pseudo=" + pseudo + ", role=" + role + "]";
    }

    private int idVehicule;
    private int nbReservationsPerma;


    public Client(int idClient, String pseudo, String mdp, String nom, String prenom, int numTel, int numCarte, String mail, String role,
        int idVehicule, int nbReservationsPerma) {
        this.pseudo = pseudo;
        this.mdp = mdp;
        this.nom = nom;
        this.numTel = numTel;
        this.numCarte = numCarte;
        this.mail = mail;
        this.role = role;
        this.idVehicule = idVehicule;
        this.nbReservationsPerma = nbReservationsPerma;
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
        Contrat contrat = new Contrat();
        List<Reservation> lr = new ArrayList<Reservation>();
        boolean quitter = false;

        while(!quitter) {
            System.out.println("Menu client : Se pr??senter ?? une borne (2) Se d??connecter (3) Modifier des infos client (4) Supprimer un client (5) R??server une borne (6) Verifier reservation (7) Quitter (8) Prolonger r??servation (9) Effectuer une r??servation permanente (10) Ajouter plaque d'immatriculation (11)");
            int choice = sc.nextInt();

            switch (choice) {            
                case 2:
                    PresentationBorne presentation = new PresentationBorne(getIdClient());
                    presentation.presenterBorne();
                    
                    break;

                default:
                    break;
                case 3:
                    System.out.println("Vous avez ??t?? d??connect?? de l'application");
                    break;
                case 4:
                    System.out.println("Id du client ?? modifier");
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
                    System.out.println("Saisir l'id du client ?? supprimer");
                    int id_client = sc.nextInt();
                    try{
                        supprimerClient(id_client);

                    }catch (Exception e){
                        System.out.println("Impossible de supprimer le client");
                    }
                    break;
                case 6:


                System.out.println("Saisir un num??ro d'immatriculation");
                String num_immatriculation = sc.next();
                System.out.println(idClient);
                verifPlaque(num_immatriculation, idClient);
                System.out.println("Saisir une date de d??but concernant la r??servation");
                String date = sc.next();
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // java.util.Date parsedDate = dateFormat.parse(date);
                    // Timestamp date_deb = new java.sql.Timestamp(parsedDate.getTime());
                    Timestamp date_deb = new Timestamp((dateFormat.parse(date)).getTime());
                    System.out.println("Saisir une heure de d??but");
                    int heure_debut = sc.nextInt();
                    System.out.println("Saisir une dur??e pour la r??servation");
                    int duree_reservation = sc.nextInt();
                    r = Reservation.affecterReservation(idClient, date_deb,heure_debut, duree_reservation);
                    System.out.println(r.toString());

                } catch (Exception e1) {
                    System.out.println("Impossible d'affecter la r??servation, veuillez reessayer plus tard");
                }
                break;


                case 7:
                    System.out.println("Saisir l'id d'un client pour v??rifier ses r??servations");
                    int id_cl_reservation = sc.nextInt();
                    List<Reservation> liste_reservations = new ArrayList<>();
                    try {
                        liste_reservations.addAll(Reservation.verifReservation(id_cl_reservation));
                        System.out.println(liste_reservations.toString());
                    
                    //List<Reservation> checkReservation = new ArrayList<Reservation>();
                    //checkReservation.add((Reservation) Reservation.verifReservation(id_cl_reservation));
                 
                    // System.out.println(checkReservation.toString());
                        
                        
                    } catch (Exception e) {
                        System.out.println("Impossible de r??cup??rer les r??servations");
                    }

                    break;
                    

                    case 8:
                        quitter = true;
                    break;
                    case 9:
                    System.out.println("Saisir un num??ro de r??servation");
                    int num = sc.nextInt();
                    System.out.println("Saisir la dur??e de prolongation");
                    int duree_pro = sc.nextInt();
                    Reservation r2 = Reservation.prolongerReservation(num,duree_pro);

                    break;

                    case 10:
                    System.out.println("Saisir une date de d??but pour votre r??servation");
                    String dateDeb = sc.next();
                    System.out.println("Saisir la dur??e pendant laquelle vous souhaitez rester chaque jour");
                    int duree = sc.nextInt();
            
                    try {
                        Timestamp dateConvert = formatterDate(dateDeb);
                        contrat = Reservation.reservationPermanente(idClient, dateConvert, duree);
                        if(contrat != null){
                            System.out.println(contrat.toString());

                        }else{
                            break;
                        }
                        
                    } catch (ParseException e) {
                        System.out.println("Impossible d'ajouter la r??servation permanente");
                    }
                    
                    break;

                    case 11:
                        System.out.println("Entrez votre plaque d'immatriculation : ");
                        String plaque = sc.next();
                        System.out.println("Votre v??hicule est il lou?? ? (0 = non/1 = oui)");
                        int estLoue = sc.nextInt();
                        System.out.println("Quelle est la marque de votre v??hicule ? ");
                        String marque = sc.next();
                        insertVehicule(plaque,estLoue,marque);
                        break;
                
                }
            }
        sc.close();
    }

    public void insertVehicule(String plaque, int estLoue, String marque) {
        String query = "INSERT INTO vehicule(marque,estLoue,plaque) values ((?),(?),(?))";
        System.out.println(this.idClient);
        int idVehicule = 0;
        try {
            PreparedStatement requete = co.prepareStatement(query);
            requete.setString(1, marque);
            requete.setInt(2, estLoue);
            requete.setString(3, plaque);
            requete.executeUpdate();
            System.out.println("Changement effectu?? !");
        } catch (SQLException e) {
            System.out.println("Impossible d'ins??rer le nouveau v??hicule");
        }
        try {
            PreparedStatement foreignKeyChecks = co.prepareStatement("SET foreign_key_checks = 0");
            foreignKeyChecks.execute();
        } catch (SQLException e) {
            System.out.println("Erreur dans le lien entre le v??hicule et le client");
        }

        String getId = "SELECT idVehicule FROM vehicule WHERE plaque=(?)";

        try {
            PreparedStatement requete = co.prepareStatement(getId);
            requete.setString(1, plaque);
            ResultSet rs = requete.executeQuery();

            while(rs.next()) {
                idVehicule = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Impossible de trouver le v??hicule");
        }

        String queryUpdate = "UPDATE client set idVehicule = (?) WHERE idClient=(?)";

        try {
            PreparedStatement requete = co.prepareStatement(queryUpdate);
            requete.setInt(1, idVehicule);
            requete.setInt(2, this.idClient);
            requete.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Impossible de mettre ?? jour votre v??hicule");
        }
    }
  
    public Timestamp formatterDate(String date) throws ParseException{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp dateFormatee = new Timestamp((dateFormat.parse(date)).getTime());
        return dateFormatee;

    }

    
    public void supprimerClient(int id) throws ClassNotFoundException {
		try {
            Dotenv dotenv = null;
            dotenv = Dotenv.configure().load();
            Connection co = DatabaseConnection.dbco(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));
			PreparedStatement requete = co.prepareStatement("DELETE from Client where idClient=(?)");
			requete.setInt(1, id);
            requete.execute();
			close(requete, co);
		}
		catch(SQLException sqle) {
            System.out.println("Impossible de supprimer le client");				
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

        try {
            String queryPlaqueExiste = "SELECT plaque,vehicule.idVehicule,estLoue FROM vehicule,client WHERE plaque = (?) AND vehicule.idVehicule = client.idVehicule";
            PreparedStatement ps = co.prepareStatement(queryPlaqueExiste);
            ps.setString(1, plaque);
            ResultSet rs1 = ps.executeQuery();
            if(rs1.next()){
                System.out.println("La plaque renseign??e est la m??me que dans le profil client");
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
            System.out.println("Impossible de r??cup??rer les informations");
        }
     

    }

        public static long compareTwoTimeStamps(java.sql.Timestamp currentTime, java.sql.Timestamp oldTime)
        {
            long milliseconds1 = oldTime.getTime();
            long milliseconds2 = currentTime.getTime();

            long diff = milliseconds2 - milliseconds1;
            long diffMinutes = diff / (60 * 1000);

            return diffMinutes;
        }

    public boolean verifContratClient(int idClient)
    {
        boolean contratExiste = false;
        String queryClient = "SELECT 1 FROM contrat WHERE idClient = (?)";
        try {
            PreparedStatement pstate = co.prepareStatement(queryClient);
            ResultSet rs = pstate.executeQuery();
            if(rs.next()){
                contratExiste = true;
            }
        } catch (SQLException e) {
            System.out.println("Impossible de trouver un contrat");
        }
        return contratExiste;
    }


    

    

}