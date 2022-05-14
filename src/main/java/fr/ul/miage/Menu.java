package fr.ul.miage;

import java.util.Scanner;

public class Menu {

    public void Demarrer(){

        Scanner sc = new Scanner(System.in);
		System.out.println("Bienvenue, selection de la requête par chiffre : 1 - Client |");
        int nb1 = sc.nextInt();

        switch(nb1) {
            case 1:
            Client client = new Client();
			
			client.menu_client();
			break;

    }
    
}
}
