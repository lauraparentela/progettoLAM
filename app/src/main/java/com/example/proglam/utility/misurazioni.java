package com.example.proglam.database.utility;

import android.database.Cursor;

import java.sql.Timestamp;

public class misurazioni implements Comparable<misurazioni> {

    private long Codice;
    private String Categoria;
    private int Rilevazione;
    private String Coordinate;
    private Timestamp Data;

    public misurazioni() {
    }

    public misurazioni(String Categoria, int Rilevazione, String Coordinate, Timestamp Data) {
        this.Categoria = Categoria;
        this.Rilevazione = Rilevazione;
        this.Coordinate = Coordinate;
        this.Data = Data;
    }

    public long getCodice() {
        return Codice;
    }

    public void setCodice(long Codice) {
        this.Codice = Codice;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String Categoria) {
        this.Categoria = Categoria;
    }

    public int getRilevazione() {
        return Rilevazione;
    }

    public void setRilevazione(int Rilevazione) {
        this.Rilevazione = Rilevazione;
    }

    public String getCoordinate() {
        return Coordinate;
    }

    public void setCoordinate(String Coordinate) {
        this.Coordinate = Coordinate;
    }

    public Timestamp getData() {
        return Data;
    }

    public void setData(Timestamp Data) {
        this.Data = Data;
    }

    @Override
    public int compareTo(misurazioni misurazioni) {
        return this.getData().compareTo(misurazioni.getData());
    }

    // Metodo per ottenere i valori della riga corrente dal cursore
    public static misurazioni fromCursor(Cursor cursor) {
        misurazioni misurazioni = new misurazioni();
        misurazioni.setCodice(cursor.getLong(cursor.getColumnIndexOrThrow("Codice")));
        misurazioni.setCategoria(cursor.getString(cursor.getColumnIndexOrThrow("Categoria")));
        misurazioni.setRilevazione(cursor.getInt(cursor.getColumnIndexOrThrow("Rilevazione")));
        misurazioni.setCoordinate(cursor.getString(cursor.getColumnIndexOrThrow("Coordinate")));
        misurazioni.setData(new Timestamp(cursor.getLong(cursor.getColumnIndexOrThrow("Data"))));
        return misurazioni;
    }

}
