package com.example.root.Seizsafe;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.root.myapplication.EstadisticaBarras;
import com.example.root.myapplication.LineBarra;
import com.example.root.myapplication.R;

import java.util.List;


public class Menu_Estadisticas extends AppCompatActivity {

    TextView txtBarras;
    Utiles uth = null;
    List<Series> cadena;
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg)
        {
            cadena = (List<Series>) msg.obj;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__estadisticas);
        txtBarras = (TextView) findViewById(R.id.txtBarras);
        uth = new Utiles(handler);

    }

    public void Lineas(View v){
        Intent i = new Intent(this,Estadisticas_prueba.class);
        startActivity(i);
    }

    public void Barras(View v){
        Intent i = new Intent(this,EstadisticaBarras.class);
        startActivity(i);
    }

    public void LineBarra(View v){
        Intent i = new Intent(this,LineBarra.class);
        startActivity(i);
    }

    public void PJson(View v){
        uth.iniciarServicioLeerEstadisticasMovimiento("", "");

    }

    @Override
    protected void onResume() {
        super.onResume();
        for (String key : Menu.hAtaque.keySet()) {
            if (Menu.hAtaque.get(key)){
                Intent i = new Intent(Menu_Estadisticas.this, Reproductor_Stream.class);
                i.putExtra("ip",key);
                startActivityForResult(i,1);
            }
        }
    }
}
