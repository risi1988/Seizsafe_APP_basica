package com.example.root.Seizsafe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jonatan.Gonzalez on 16/03/2016.
 */
public class Sensor {
    private int id;
    private String nombre;
    private String tipo;

    public Sensor(int id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }


    public Sensor(JSONObject jsonObject) {
        try {
            id = jsonObject.getInt("id");
            nombre =jsonObject.getString("nombre");
            tipo = jsonObject.getString("tipo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Sensor) {
            Sensor s = (Sensor) obj;
            return s.getId() == this.id;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
