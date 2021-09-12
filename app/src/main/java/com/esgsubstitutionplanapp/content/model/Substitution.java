package com.esgsubstitutionplanapp.content.model;

import org.jetbrains.annotations.NotNull;

public class Substitution {

    private String klassen;
    private String datum;
    private String std;
    private String art;
    private String vertretung;
    private String fach;
    private String raum;
    private String fach2;
    private String zuVertreten;
    private String bemerkung;
    private String verlegtVon;

    @NotNull
    @Override
    public String toString() {
        return "Substitution{" +
                "klassen='" + klassen + "\n" +
                ", datum='" + datum + "\n" +
                ", std='" + std + "\n" +
                ", art='" + art + "\n" +
                ", vertretung='" + vertretung + "\n" +
                ", fach='" + fach + "\n" +
                ", raum='" + raum + "\n" +
                ", fach2='" + fach2 + "\n" +
                ", zuVertreten='" + zuVertreten + "\n" +
                ", bemerkung='" + bemerkung + "\n" +
                ", verlegtVon='" + verlegtVon + "\n" +
                '}';
    }

    public String getKlassen() {
        return klassen;
    }

    public void setKlassen(String klassen) {
        this.klassen = klassen;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getStd() {
        return std;
    }

    public void setStd(String std) {
        this.std = std;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getVertretung() {
        return vertretung;
    }

    public void setVertretung(String vertretung) {
        this.vertretung = vertretung;
    }

    public String getFach() {
        return fach;
    }

    public void setFach(String fach) {
        this.fach = fach;
    }

    public String getRaum() {
        return raum;
    }

    public void setRaum(String raum) {
        this.raum = raum;
    }

    public String getFach2() {
        return fach2;
    }

    public void setFach2(String fach2) {
        this.fach2 = fach2;
    }

    public String getZuVertreten() {
        return zuVertreten;
    }

    public void setZuVertreten(String zuVertreten) {
        this.zuVertreten = zuVertreten;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public void setBemerkung(String bemerkung) {
        this.bemerkung = bemerkung;
    }

    public String getVerlegtVon() {
        return verlegtVon;
    }

    public void setVerlegtVon(String verlegtVon) {
        this.verlegtVon = verlegtVon;
    }

}
