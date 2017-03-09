package com.example.root.Seizsafe;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonatan.Gonzalez on 27/01/2016.
 */
public class BuscarPing extends Thread{
    private Utiles uth = new Utiles();
    private String mascara, ip;
    private String[] ipArray;
    private String salida = "buscar";
    private String cadena="";
    private InetAddress in = null;
    private int posicion=0;

    private final Handler handler2 = new Handler() {

        @Override
        public void handleMessage(Message msg)
        {
            cadena = (String) msg.obj;
            String [] dispositivos = cadena.split(";");
            if(cadena.startsWith("Dispositivo")){
                try {
                    salida=salida+";"+uth.crearSeizsafe(dispositivos[1]).getNombre()+" "+ dispositivos[2];
                    posicion++;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                enviar_lista(posicion);
            }else if (cadena.startsWith("Error")){
                ldispositivos.remove(dispositivos[1]);
            }
        }
    };

    private Handler handler = null;
    private List<String> ldispositivos = new ArrayList<String>();

    public BuscarPing(String mascara, String ip, Handler handler){
        this.posicion=0;
        this.mascara=mascara;
        this.ip=ip;
        this.handler=handler;
        uth = new Utiles(handler2);
        ipArray = ip.split("\\.");
    }
    public void run(){
        pingRed();
    }
    public void pingRed() {
        int i =0;
        recorrerClaseC(ipArray[0],ipArray[1],ipArray[2]);
        leerArp();
        while (i<ldispositivos.size()){
            uth.iniciarServicioDispositivo(ldispositivos.get(i));
            i++;
        }
    }


    private void recorrerClaseC(String s, String s1, String s2) {
        for (int i =0 ; i<255; i++){
            // Definimos la ip a la cual haremos el ping
            try {
                in = InetAddress.getByName(s+"."+s1+"."+s2+"."+i);
                Log.i("BuscarPing", "Clase de red " +s+"."+s1+"."+s2+"."+i);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Definimos un tiempo en el cual ha de responder
            try {
                if (in.isReachable(5)) {// tiempo en mili segundos
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void leerArp(){
        BufferedReader br = null;
        Log.i("BuscarPing", "Leer fichero Arp");
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" +");
                // Basic sanity check
                String mac = splitted[3];
                if (mac.startsWith("b8:27:eb")){
                    ldispositivos.add(splitted[0]);
                }else if (mac.startsWith("60:")){//Esto es muy por pinzas ya que es solo la 94
                    ldispositivos.add(splitted[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void enviar_lista(int count) {
        if (count==ldispositivos.size()){
            handler.obtainMessage(1,salida).sendToTarget();
        }
    }


}
