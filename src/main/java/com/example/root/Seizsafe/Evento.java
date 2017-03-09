package com.example.root.Seizsafe;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jonatan.Gonzalez on 16/03/2016.
 */
public class Evento {
    private int id;
    private boolean esCrisis;
    private String ip;
    private final String tipo="Evento";
    private boolean revisado;
    private String fechaInicio;

    public Evento(){}
    public Evento(boolean esCrisis, int id, boolean revisado) {
        this.esCrisis = esCrisis;
        this.id = id;
        this.revisado = revisado;
    }

    public Evento(JSONObject object) {
        try {
                this.id = object.getInt("id");
                this.esCrisis = object.getBoolean("esCrisis");
                this.revisado = object.getBoolean("revisado");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isEsCrisis() {
        return esCrisis;
    }

    public void setEsCrisis(boolean esCrisis) {
        this.esCrisis = esCrisis;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isRevisado() {
        return revisado;
    }

    public void setRevisado(boolean revisado) {
        this.revisado = revisado;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public boolean equals(Object obj) {

            Evento e = (Evento) obj;
            if (e.getId()==this.id) return true;
            else return false;

    }

    @Override
    public String toString() {
        return "Evento{" +
                "esCrisis=" + esCrisis +
                ", id=" + id +
                ", ip='" + ip + '\'' +
                ", tipo='" + tipo + '\'' +
                ", revisado=" + revisado +
                ", fechaInicio='" + fechaInicio + '\'' +
                '}';
    }


}


