package com.example.root.Seizsafe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by jonatan.Gonzalez on 17/12/2015.
 */


public class Estado {
    private boolean estado;
    private boolean automatico;
    private Programacion programacion;

    public Estado(boolean automatico, boolean estado, Programacion programacion) {
        this.automatico = automatico;
        this.estado = estado;
        this.programacion = programacion;
    }

    public Estado(JSONObject jsonObject) throws JSONException, ParseException {
        this.estado =  jsonObject.getBoolean("estado");
        this.automatico = jsonObject.getBoolean("automatico");
        if (automatico==true) this.programacion = new Programacion(jsonObject.getJSONArray("programacion"));
        else this.programacion= null;


    }

    public boolean isAutomatico() {
        return automatico;
    }

    public void setAutomatico(boolean automatico) {
        this.automatico = automatico;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public Programacion getProgramacion() {
        return programacion;
    }

    public void setProgramacion(Programacion programacion) {
        this.programacion = programacion;
    }

    @Override
    public String toString() {
        return "Estado{" +
                "automatico=" + automatico +
                ", estado=" + estado +
                ", programacion=" + programacion +
                '}';
    }
}

class Programacion{
    private String inicio;
    private String fin;

    public Programacion(String inicio, String fin) {
        this.fin = fin;
        this.inicio = inicio;
    }


    public Programacion(JSONArray jsonArray) throws JSONException, ParseException {
        this.inicio = jsonArray.get(0).toString();
        this.fin=jsonArray.get(1).toString();
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    @Override
    public String toString() {
        return "Programacion{" +
                "fin='" + fin + '\'' +
                ", inicio='" + inicio + '\'' +
                '}';
    }
}

