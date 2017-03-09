package com.example.root.Seizsafe;

import java.util.List;

/**
 * Created by jonatan.Gonzalez on 03/03/2016.
 */
public class ListSeizsafeMenu {
    private String Dispositivo;
    private String codigo;
    private boolean enlazado;
    private String Ip;
    private boolean Estado;
    private List<Sensor> lSensor;

    public  ListSeizsafeMenu(){

    }

    public  ListSeizsafeMenu(boolean enlazado){
        this.enlazado=enlazado;

    }

    public ListSeizsafeMenu(String dispositivo, boolean estado, String ip, List<Sensor> lSensor) {
        Dispositivo = dispositivo;
        Estado = estado;
        Ip = ip;
        this.lSensor=lSensor;
    }

    public ListSeizsafeMenu(String dispositivo,String codigo, boolean enlazado, boolean estado, String ip, List<Sensor> lSensor) {
        Dispositivo = dispositivo;
        this.codigo=codigo;
        this.enlazado=enlazado;
        Estado = estado;
        Ip = ip;
        this.lSensor=lSensor;
    }

    public ListSeizsafeMenu(String dispositivo,String codigo, boolean enlazado, boolean estado, String ip) {
        Dispositivo = dispositivo;
        Estado = estado;
        this.codigo=codigo;
        this.enlazado=enlazado;
        Ip = ip;
        this.lSensor=null;
    }

    public ListSeizsafeMenu(String dispositivo, String codigo ,boolean estado, String ip) {
        Dispositivo = dispositivo;
        this.codigo=codigo;
        this.enlazado=true;
        Estado = estado;
        Ip = ip;
        this.lSensor=null;
    }

    public String getDispositivo() {
        return Dispositivo;
    }

    public boolean isEstado() {
        return Estado;
    }

    public String getIp() {
        return Ip;
    }

    public void setDispositivo(String dispositivo) {
        Dispositivo = dispositivo;
    }

    public void setEstado(boolean estado) {
        Estado = estado;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public List<Sensor> getlSensor() {
        return lSensor;
    }

    public boolean isEnlazado() {
        return enlazado;
    }

    public void setEnlazado(boolean enlazado) {
        this.enlazado = enlazado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setlSensor(List<Sensor> lSensor) {
        this.lSensor = lSensor;
    }

    @Override
    public String toString() {
        return "ListSeizsafeMenu{" +
                "Dispositivo='" + Dispositivo + '\'' +
                ", Ip='" + Ip + '\'' +
                ", Estado='" + Estado + '\'' +
                ", lAcelerometro=" + lSensor +
                '}';
    }
}
