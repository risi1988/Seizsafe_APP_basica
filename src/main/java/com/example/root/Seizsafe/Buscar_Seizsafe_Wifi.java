package com.example.root.Seizsafe;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.myapplication.R;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Buscar_Seizsafe_Wifi extends AppCompatActivity {
    private List<String> lIp;
    private ListView lv;
    private List <String> ls = new ArrayList();
    private TextView txtBuscando;
    private TextView txtEncontrado;
    private TextView txtInfo;
    private ProgressBar processBar;

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String cadena = msg.toString();
            String[] numerosComoArray = cadena.split(";");
            for(int i = 1; i < numerosComoArray.length-1; i++) {
                ls.add(numerosComoArray[i]);
            }
            rellenarListWifi(ls);
        }

    };

    private void rellenarListWifi(List<String> ls) {
        //Pre recivimos una lista con las Ips de las raspberrys que hay en nuestra red.
        //Post mosttamos en un listView las Ips que nos han mandado
        txtInfo.setVisibility(View.GONE);
        txtBuscando.setVisibility(View.GONE);
        processBar.setVisibility(View.GONE);
        txtEncontrado.setVisibility(View.VISIBLE);
        lv.setVisibility(View.VISIBLE);
        String wifis[] = new String[ls.size()];
        for (int i =0; i<ls.size();i++){
            wifis[i] = ls.get(i).toString();
        }
        lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_negro, wifis));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar__seizsafe__wifi);
        lv = (ListView) findViewById(R.id.listWifi);
        txtBuscando = (TextView) findViewById(R.id.txtBuscando);
        txtEncontrado = (TextView) findViewById(R.id.txtEncontrado);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        processBar = (ProgressBar) findViewById(R.id.progressBar);
        //rastrearWifi();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nombreWifi;
                String[] raspberry = ls.get(position).toString().split(":");
                nombreWifi = raspberry[1];
                if (!Menu.lip.contains(nombreWifi)) Menu.lip.add(nombreWifi);
                guardarIP(nombreWifi);
                String cadena = getString(R.string.wifiConectado) + " " + raspberry[0];
                Toast toast1 = Toast.makeText(getApplicationContext(), cadena, Toast.LENGTH_LONG);
                toast1.show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }


    private void guardarIP(String ip){//Esta funcion esta tambien en el fichero de Configurar_Conexion y en Buscar_SeizSafe
        //Pre nos mandan una String con la IP que queremos que se guarde en el fichero
        //Post guardamos la IP en el fichero acordado
        try
        {
            OutputStreamWriter fout=new OutputStreamWriter(openFileOutput(Menu.nomFichero, Context.MODE_PRIVATE));
            fout.write(ip);
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (String key : Menu.hAtaque.keySet()) {
            if (Menu.hAtaque.get(key)){
                Intent i = new Intent(Buscar_Seizsafe_Wifi.this, Reproductor_Stream.class);
                i.putExtra("ip",key);
                startActivityForResult(i,1);
            }
        }
    }
}
