package fr.ul.miage;

import java.util.Scanner;

public class Menu_client {

    public void launch(){

        System.out.println("Menu client : Inscription (1) Connexion (2) Se déconnecter (3) Modifier des infos client (4) Supprimer un client (5)  Quitter (6)");
        Scanner sc = new Scanner(System.in);
		
		Client cl = new Client();

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
            cl.ajoutClient(pseudo, protect_mdp,nom,prenom,num_tel,num_carte, mail, plaque);

           }catch(ClassNotFoundException e){
               e.printStackTrace();
           }
            

            

            System.out.println("Votre compte a bien été créé");


                
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

            case 5:
                System.out.println("Saisir l'id du client à supprimer");
                int id_client = sc.nextInt();
                try{
                    cl.supprimerClient(id_client);

                }
            catch(ClassNotFoundException e){
               e.printStackTrace();
           }
            
                System.out.println("Le client a été supprimé");

        }

        sc.close();


    }
    
}
