package fr.ul.miage;

import java.sql.Timestamp;

public class Contrat {

    public int getIdContrat() {
        return idContrat;
    }
    public void setIdContrat(int idContrat) {
        this.idContrat = idContrat;
    }
    public int getIdClient() {
        return idClient;
    }
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }
    public int getIdBorne() {
        return idBorne;
    }
    public Contrat(){
        
    }
    public Contrat(int idContrat, int idClient, int idBorne, Timestamp dateDebut, Timestamp dateFin, int duree) {
        this.idContrat = idContrat;
        this.idClient = idClient;
        this.idBorne = idBorne;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.duree = duree;
    }
    public void setIdBorne(int idBorne) {
        this.idBorne = idBorne;
    }
    public Timestamp getDateDebut() {
        return dateDebut;
    }
    public void setDateDebut(Timestamp dateDebut) {
        this.dateDebut = dateDebut;
    }
    public Timestamp getDateFin() {
        return dateFin;
    }
    public void setDateFin(Timestamp dateFin) {
        this.dateFin = dateFin;
    }
    public int getDuree() {
        return duree;
    }
    public void setDuree(int duree) {
        this.duree = duree;
    }
    int idContrat;
    int idClient;
    int idBorne;
    Timestamp dateDebut;
    Timestamp dateFin;
    int duree;
    @Override
    public String toString() {
        return "Contrat [dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", duree=" + duree + ", idBorne=" + idBorne
                + ", idClient=" + idClient + ", idContrat=" + idContrat + "]";
    }
    
}
