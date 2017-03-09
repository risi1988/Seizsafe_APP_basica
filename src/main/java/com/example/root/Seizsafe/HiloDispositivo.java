package com.example.root.Seizsafe;

import android.os.Handler;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jonatan.Gonzalez on 29/01/2016.
 */
public class HiloDispositivo extends Thread {
    private WebSocketClient mWebSocketClient;
    private URI uri;
    public Handler handler;
    public String ip;
    public int idAlerta =-1;
    public Utiles ut;


    public HiloDispositivo(String ip, Handler handler){
        this.ip=ip;
        this.handler=handler;
        ut = new Utiles();
    }


    public void run(){
        conexion();
    }

    private void conexion() {
        //URL a la cual se tiene que conectar para recibir las alertas oportunas
        try {
            uri = new URI("ws://"+ip+":8888/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        connectWebSocket();
    }

    private void connectWebSocket() {
        //Pre unir el handler con el Menu.java
        //Pos enviamos 0 o 4 en caso de que no hayamos conectado y 2 en caso de perder la conexion o la raspberry nos haya cerrado la conexion
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                try {
                    handler.obtainMessage(1,getIp()+":Open").sendToTarget();
                    Thread.sleep(2000);//Tiempo necesario para que cambie el estado de la variable.
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                Log.i("Websocket", "Opened");


            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                Log.i("Websocket", "Mensaje: " + s);
                Object o = new Object();
                try {
                    o = ut.toMap(new JSONObject(s));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (o instanceof Evento){
                    Evento e = (Evento) o;
                    e.setIp(getIp());
                    //Comprobacion si es id es igual al anterior, si es asi no tiene que saltar
                    if (idAlerta!=e.getId()) {
                        if (e.isEsCrisis()) {
                            idAlerta=e.getId();
                            handler.obtainMessage(1, e).sendToTarget();
                        }
                    }
                }else if (o instanceof Acelerometro) {
                    Acelerometro a = (Acelerometro) o;
                    a.setIp(getIp());
                    handler.obtainMessage(1,a).sendToTarget();
                }else if (o instanceof Boolean){
                boolean b = (boolean) o;
                if (b==false){
                    handler.obtainMessage(1,getIp()+":Sesion:False").sendToTarget();
                }
            }


            }

            @Override
            public void onClose(int i, String s, boolean b) {
                try {
                    Thread.sleep(1000); //Esperamos 10 segundos hantes de mandar el mensaja
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("Websocket", "Cierro");
                handler.obtainMessage(1,getIp()+":Error").sendToTarget();
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error");
            }

        };
        mWebSocketClient.connect();
    }


    public WebSocketClient getmWebSocketClient() {
        return mWebSocketClient;
    }

    public void setmWebSocketClient(WebSocketClient mWebSocketClient) {
        this.mWebSocketClient = mWebSocketClient;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void pararServicio(){
        if (mWebSocketClient!=null) {
            if (mWebSocketClient.getConnection().isOpen()) {
                mWebSocketClient.getConnection().close();
            }
        }
    }

}
