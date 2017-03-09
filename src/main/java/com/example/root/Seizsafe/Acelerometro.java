package com.example.root.Seizsafe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonatan.Gonzalez on 16/03/2016.
 */
public class Acelerometro {
    private List<Integer> nuevo;
    private List<Integer> eliminar;
    private String tipo;
    private String ip;
    private List<Sensor> lSensor;

    public Acelerometro(){
        nuevo=null;
        eliminar=null;
        tipo="";
        ip="";
        lSensor=null;
    }

    public Acelerometro(List<Sensor> lSensor, String tipo) {
        this.lSensor = lSensor;
        this.tipo = tipo;
        this.nuevo= nuevo;
        this.eliminar= eliminar;
    }

    public Acelerometro(JSONObject object) {
        try {
            this.tipo = object.getString("tipo");
            this.lSensor = construirSensores(object.getJSONArray("lista"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Sensor> construirSensores(JSONArray lista) {
        List<Sensor> ls = new ArrayList<>();
        try {
            for (int i =0; i<lista.length();i++){
                ls.add(new Sensor(lista.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ls;
    }

    public List<Sensor> getlSensor() {
        return lSensor;
    }

    public void setlSensor(List<Sensor> lSensor) {
        this.lSensor = lSensor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Integer> getEliminar() {
        return eliminar;
    }

    public void setEliminar(List<Integer> eliminar) {
        this.eliminar = eliminar;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<Integer> getNuevo() {
        return nuevo;
    }

    public void setNuevo(List<Integer> nuevo) {
        this.nuevo = nuevo;
    }

    @Override
    public boolean equals(Object obj) {

            Acelerometro a = (Acelerometro) obj;
            if (a.getTipo().equals(this.getTipo())){
                return true;
            }else{
                return false;
            }

    }

    @Override
    public String toString() {
        return "Acelerometro{" +
                "lSensor=" + lSensor +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
