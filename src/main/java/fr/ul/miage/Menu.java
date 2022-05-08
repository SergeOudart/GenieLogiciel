package fr.ul.miage;

import java.util.Scanner;

public class Menu {

    public void Demarrer(){

        Scanner sc = new Scanner(System.in);
		System.out.println("Bienvenue, selection de la requête par chiffre : 1 - Client |");
        int nb1 = sc.nextInt();

        switch(nb1) {
            case 1:
            Menu_client mc = new Menu_client();
			
			mc.launch();
			break;

    }
    
}
}
