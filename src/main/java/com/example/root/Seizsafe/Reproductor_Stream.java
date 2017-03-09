package com.example.root.Seizsafe;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.ParseException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.myapplication.R;

import static com.example.root.myapplication.R.*;
import static com.example.root.myapplication.R.color.*;


public class Reproductor_Stream extends Activity {

    private static final String TAG = "MjpegActivity";
    private Button btn1;
    private Menu menu;
    private MjpegView mv;
    private EnviarMensaje en;
    private LinearLayout ll1;
    private RelativeLayout lp;
    private String ip="";
    private String URL;
    private Utiles uth=null;
    private Estado estado;
    private String cadena="";

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            cadena = (String) msg.obj;
            String[] estadoCamara = cadena.split(";");
            if (cadena.startsWith("Camara")) {
                try {
                    estado = uth.crearEstado(estadoCamara[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (estado.isEstado()) {
                    continuar();
                } else {
                    finish();
                    Toast toast1 = Toast.makeText(getApplicationContext(), getString(string.Camara_Off), Toast.LENGTH_LONG);
                    toast1.show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        Menu.hAtaque.put(ip, false);
        setContentView(layout.content_reproductor);
        ip = getIntent().getExtras().getString("ip");
            en = new EnviarMensaje(ip);
            btn1 = (Button) findViewById(id.btnnoCrisis);
            if (Menu.hAtaque.get(ip)){
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("Envio", getIntent().getExtras().getString("notificar"));
                        en.sendMessage(getIntent().getExtras().getString("notificar"));
                        btn1.setEnabled(false);
                    }
                });
            } else {
                btn1.setVisibility(View.INVISIBLE);
            }
            uth = new Utiles(handler);
            uth.iniciarServicioCamara();

    }

    public void continuar(){
        mv = new MjpegView(Reproductor_Stream.this);
        LinearLayout llpadre = (LinearLayout) findViewById(id.lineRepro);
        ll1 = (LinearLayout) findViewById(id.linearRepro1);
        mv.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));

        ll1.addView(mv);
        lp = (RelativeLayout) findViewById(id.linearRepro2);
        ll1.setBackgroundColor(Color.RED);
        llpadre.removeView(ll1);
        llpadre.addView(ll1);
        llpadre.removeView(lp);
        llpadre.addView(lp);
        URL = "http://" + ip + ":8080/?action=stream";
        new DoRead().execute(URL);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (en!=null) en.cerrarConexion();
        Menu.hAtaque.put(ip, false);
        if (estado!=null) {
            if (estado.isEstado()) {
                mv.stopPlayback();
            }
        }
        finish();
    }


    @Override
    protected void onPause() {
        //onDestroy();
        super.onPause();
        Menu.hAtaque.put(ip, false);
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode()==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-IOException", e);
                //Error connecting to camera
            }

            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(true);
            Menu.hAtaque.put(ip, false);
        }
    }

}
