package com.example.root.Seizsafe;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by jonatan.Gonzalez on 07/01/2016.
 */
public class Dispositivo {
    private int id;
    private String codigo;
    private String autorizacion;
    private String nombre;

    public Dispositivo(String autorizacion, String codigo, int id, String nombre) {
        this.autorizacion = autorizacion;
        this.codigo = codigo;
        this.id = id;
        this.nombre = nombre;
    }


    public Dispositivo(JSONObject jsonObject) throws JSONException, ParseException {
        this.autorizacion = jsonObject.getString("autorizacion");
        this.codigo = jsonObject.getString("codigo");
        this.id = jsonObject.getInt("id");
        this.nombre = jsonObject.getString("nombre");
    }


    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    @Override
    public String toString() {
        return "Dispositivo{" +
                "autorizacion='" + autorizacion + '\'' +
                ", id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
