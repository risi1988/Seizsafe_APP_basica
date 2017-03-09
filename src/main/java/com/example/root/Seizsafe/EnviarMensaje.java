package com.example.root.Seizsafe;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jonatan.Gonzalez on 17/03/2016.
 */
public class EnviarMensaje {
    private WebSocketClient mWebSocketClient;

    public EnviarMensaje(String ip){
        connectWebSocket();
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://"+Menu.ip+":8888/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("WebSocket", "Abro la comunicacion");
            }
            @Override
            public void onMessage(String s) {
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("WebSocket", "Cierro la comunicacion");
            }

            @Override
            public void onError(Exception e) {
                Log.i("WebSocket", "Error en la comunicacion");
            }
        };
        mWebSocketClient.connect();
    }

    public void cerrarConexion(){
        mWebSocketClient.getConnection().close();
    }

    public void sendMessage(String cadena) {
        mWebSocketClient.send(cadena);
    }
}
