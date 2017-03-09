package com.example.root.Seizsafe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jonatan.Gonzalez on 26/01/2016.
 */
public class Seizsafe {
    //private int id;
    private String codigo;
    //private URL autorizacion;
    private String nombre;
    //private int datosCompartidos;
    //private boolean autorizacionDatos;
    private String ip;

    public Seizsafe(JSONObject jsonObject) {

        try {
            //this.autorizacionDatos =  ""; //jsonObject.getBoolean("autorizacionDatos");
            //this.id = ""; //jsonObject.getInt("id");
            //this.autorizacion = ""; //new URL(jsonObject.getString("autorizacion"));
            this.codigo = jsonObject.getString("codigo");
            this.nombre = jsonObject.getString("nombre");
            //this.datosCompartidos = "";//jsonObject.getInt("datosCompartidos");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Seizsafe (){

    }

    public Seizsafe(URL autorizacion, boolean autorizacionDatos, String codigo, int datosCompartidos, int id, String nombre) {
        //this.autorizacion = autorizacion;
        //this.autorizacionDatos = autorizacionDatos;
        this.codigo = codigo;
        //this.datosCompartidos = datosCompartidos;
        //this.id = id;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    /*
    public void setAutorizacion(String autorizacion) {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public URL getAutorizacion() {
        return autorizacion;
    }

    public boolean isAutorizacionDatos() {
        return autorizacionDatos;
    }

    public void setAutorizacionDatos(boolean autorizacionDatos) {
        this.autorizacionDatos = autorizacionDatos;
    }

    public int getDatosCompartidos() {
        return datosCompartidos;
    }

    public void setDatosCompartidos(int datosCompartidos) {
        this.datosCompartidos = datosCompartidos;
    }

    */

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /*
    @Override
    public String toString() {
        return "Seizsafe{" +
                "autorizacion='" + autorizacion + '\'' +
                ", id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", datosCompartidos=" + datosCompartidos +
                ", autorizacionDatos=" + autorizacionDatos +
                '}';
    }
    */

    @Override
    public String toString() {
        return "Seizsafe{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
