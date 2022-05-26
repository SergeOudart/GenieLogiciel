package fr.ul.miage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;

import org.junit.Test;

public class AppTest 
{
    @Test
    public void testEntryNewUser() 
    {
        Client client = new Client();
        assertTrue(client.verifEntry("pseudo", "mdp", "nom", "prenom", "0777", "0123", "mail@mail.fr", "0123", "user"));
    }

    @Test
    public void testPaiementPasDepassement()
    {
        PresentationBorne pres = new PresentationBorne(1);
        assertTrue(pres.paiement(2, 0, false) == 20);
    }

    @Test
    public void testPaiementDepassement() {
        PresentationBorne pres = new PresentationBorne(1);
        assertTrue(pres.paiement(2, 30, false) == 50);
    }

    @Test
    public void testPaiementNonPres() {
        PresentationBorne pres = new PresentationBorne(1);
        assertTrue(pres.paiement(2, 0, true) == 40);
    }

    @Test
    public void pasDansPeriodeAttente() {
        PresentationBorne pres = new PresentationBorne(1);
        assertFalse(pres.dansPeriodeAttente(new Timestamp(System.currentTimeMillis() - 910000)));
    }

    @Test
    public void entrePeriodeAttente() {
        PresentationBorne pres = new PresentationBorne(1);
        assertTrue(pres.dansPeriodeAttente(new Timestamp(System.currentTimeMillis() - 30000)));
    }

    @Test
    public void dansPeriodeAttente()
    {
        PresentationBorne pres = new PresentationBorne(1);
        assertTrue(pres.dansPeriodeAttente(new Timestamp(System.currentTimeMillis())));
        assertTrue(pres.dansPeriodeAttente(new Timestamp(System.currentTimeMillis() + 30000)));
    }
    
}
