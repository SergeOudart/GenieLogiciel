package fr.ul.miage;

import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class Main 
{
    public static void main( String[] args ) throws ClassNotFoundException
    {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();

        DatabaseConnection database = new DatabaseConnection(dotenv.get("MYSQL_STRING"),dotenv.get("USER"), dotenv.get("PASSWORD"));

        Scanner sc = new Scanner(System.in);

        System.out.println("Bienvenue, choisissez une des options suivantes : Inscription (1) Connexion (2)");

        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                
                break;
            
            case 2:
                System.out.println("Numéro de plaque / numéro de réservation : \n");
                String identifiant = sc.next();

                database.fetchPassword(identifiant);
                break;

            default:
                break;
        }

        sc.close();

    }
}
