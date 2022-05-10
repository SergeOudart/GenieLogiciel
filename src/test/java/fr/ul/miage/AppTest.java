package fr.ul.miage;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void testEntryNewUser() 
    {
        Menu_client menu = new Menu_client();
        assertTrue(menu.verifEntry("pseudo", "mdp", "nom", "prenom", "0777", "0123", "mail@mail.fr", "0123"));
    }

    
}
