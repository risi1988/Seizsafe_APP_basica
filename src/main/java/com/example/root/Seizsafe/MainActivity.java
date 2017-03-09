package com.example.root.Seizsafe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.root.myapplication.R;

public class MainActivity extends AppCompatActivity {

    private String cadena;
    public static Tiempo tiempo;
    private boolean entro =true;

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg)
        {
            cadena = (String) msg.obj;
            if (cadena.startsWith("Continuar")){
                Intent i = new Intent(MainActivity.this, Menu.class);
                startActivityForResult(i,0);
            }

        }
    };

    public static void tiempo(){
        tiempo.detenerHilo(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tiempo = new Tiempo(handler);
        tiempo.start();
        entro = true;

    }

    private class Tiempo extends Thread{
        private Handler handler;
        private boolean estado = true;
        private int i =0;
        public Tiempo (Handler handler){
            this.handler=handler;
        }
        public void run(){
            while (estado && i<8){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
            }
            if (i==8) handler.obtainMessage(1,"Continuar").sendToTarget();
        }
        public void detenerHilo(boolean estado){
            this.estado = estado;
        }
    }

    @Override
    protected void onRestart() {
        Toast toast1 = Toast.makeText(getApplicationContext(), getString(R.string.Mensaje_salir), Toast.LENGTH_SHORT);
        toast1.show();
        if (entro) {
            tiempo = new Tiempo(handler);
            tiempo.start();
            super.onRestart();
        }
    }


    @Override
    protected void onDestroy() {
        tiempo.detenerHilo(false);
        super.onDestroy();
    }
}


