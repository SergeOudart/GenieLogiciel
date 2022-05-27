package fr.ul.miage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;

import org.junit.Before;


import org.junit.Test;
import org.mockito.*;

import junit.framework.Assert;

public class AppTest 

{
/*
{
    @InjectMocks private DatabaseConnection connection;
    @Mock private Connection mockConnection;
    @Mock private Statement mockStatement;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMockDatabaseConnection() throws Exception{
        Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
        Mockito.when(mockConnection.createStatement().executeUpdate((String) Mockito.any())).thenReturn(1);
        int val = connection.executeQuery("");
        Assert.assertEquals(value,1);
        

    }

*/

    @Test
    public void testEntryNewUser() 
    {
        Client client = new Client();
        assertTrue(client.verifEntry("pseudo", "mdp", "nom", "prenom", "0777", "0123", "mail@mail.fr", "0123", "user"));
    }
    
    @Test
    public void testDateExpiration(){
        assertTrue(Reservation.verifDateExpiration(Timestamp.valueOf("2022-05-19 20:15:00")));
    }
    

    @Test
    public void testPaiement()
    {
        PresentationBorne pres = new PresentationBorne(1);
        assertTrue(pres.paiement(2, 0, false) == 20);
        assertTrue(pres.paiement(2, 30, false) == 50);
        assertTrue(pres.paiement(2, 0, true) == 40);
    }

    @Test
    public void dansPeriodeAttente()
    {
        PresentationBorne pres = new PresentationBorne(1);
        assertTrue(pres.dansPeriodeAttente(new Timestamp(System.currentTimeMillis())));
        assertTrue(pres.dansPeriodeAttente(new Timestamp(System.currentTimeMillis() - 30000)));
        assertTrue(pres.dansPeriodeAttente(new Timestamp(System.currentTimeMillis() + 30000)));
        assertFalse(pres.dansPeriodeAttente(new Timestamp(System.currentTimeMillis() + 910000)));
    }
    
}

