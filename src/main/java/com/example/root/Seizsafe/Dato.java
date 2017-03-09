package com.example.root.Seizsafe;

/**
 * Created by jonatan.Gonzalez on 21/01/2016.
 */
public class Dato {
    private String fecha;
    private String valor;

    public Dato(String fecha, String valor) {
        this.fecha = fecha;
        this.valor = valor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Dato{" +
                "fecha='" + fecha + '\'' +
                ", valor=" + valor +
                '}';
    }
}
