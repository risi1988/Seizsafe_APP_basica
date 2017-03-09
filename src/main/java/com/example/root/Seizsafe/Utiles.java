package com.example.root.Seizsafe;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.example.root.myapplication.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jmdns.ServiceEvent;

/**
 * Created by jonatan.Gonzalez on 11/11/2015.
 */
public class Utiles extends AppCompatActivity {

    private HiloLeerDispositivo hiloLeerDispositivo;
    private HiloEstado hiloEstado;
    private HiloEstadoCamara hiloEstadoCamara;
    private HiloEstadoSeizsafe hiloEstadoSeizsafe;
    private HiloLeerEstadisticas hiloLeerEstadisticas;
    private HiloGuardarGrabacion hiloGuardarGrabacion;
    private final Handler handler;

    private final Handler handler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String cadena = (String) msg.obj;
            System.out.println("Hilo "+cadena);
            if (cadena=="2"){
                System.out.println("encontrado");
            }
        }
    };

   // private Mdns scanner;
   // private ServiceAdapter adapter;


    public Utiles(Handler handler){
        this.handler=handler;
    }

    public Utiles(){
        handler = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public List<Series> crearSeries (InputStream in) throws IOException, ParseException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        return leerObjetoSeries(reader);
    }


    private List<Series> leerObjetoSeries (JsonReader reader) throws IOException, ParseException {
        List<Series> lseries = new ArrayList<>();//para un futuro por si se crean varias series, sino con
        // un objeto Series vale
        reader.beginObject();
        while (reader.hasNext()){
            String name = reader.nextName();
            switch (name){
                case "series":
                    lseries = leerArraySeries(reader);
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return lseries;
    }

    private List<Series> leerArraySeries (JsonReader reader) throws IOException, ParseException{
        List<Series> ls = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            ls.add(leerSeries(reader));
        }
        reader.endArray();
        return ls;

    }

    private Series leerSeries(JsonReader reader) throws IOException, ParseException{
        String nombre = null;
        String yaxis = null;
        String type = "";
        List<Dato> ldata = null;

        reader.beginObject();
        while (reader.hasNext()){
            String name = reader.nextName();
            switch (name){
                case "name":
                    nombre = reader.nextString();
                    break;
                case "yAxis":
                    yaxis = reader.nextString();
                    break;
                case "data":
                    ldata = readDataArray(reader);
                    break;
                case "type":
                    type= reader.nextString();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();

        return new Series(ldata,nombre,yaxis,type);
    }

    private List<Dato> readDataArray(JsonReader reader) throws IOException, ParseException {

        List<Dato> ldato = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            ldato.add(leerDato(reader));
        }
        reader.endArray();
        return ldato;
    }
    private Dato leerDato(JsonReader reader) throws IOException, ParseException{
        String fecha = null;
        String valor = "0";
        reader.beginArray();
        while (reader.hasNext()){
            JsonToken check = reader.peek();
            if (check != JsonToken.NULL)fecha = reader.nextString();
            else reader.nextNull();
            JsonToken check2 = reader.peek();
            if (check2 != JsonToken.NULL) valor = reader.nextString();
            else reader.nextNull();
        }
        reader.endArray();
        return new Dato(fecha,valor);
    }


    public Seizsafe crearSeizsafe(String aux) throws JSONException, ParseException {
        //Pre nos mandan la cadena del Json
        //Post devolvemos una avanzados en caso de haber recibido todo correcto, sino sera null
        Seizsafe ss = null;
        JSONObject object = new JSONObject(aux); //Creamos un objeto JSON a partir de la cadena


        ss = new Seizsafe(object);
        return ss;
    }

    public Estado crearEstado(String aux) throws JSONException, ParseException {
        //Pre nos mandan la cadena del Json
        //Post devolvemos una sesibilidad en caso de haber recibido todo correcto, sino sera null

        Estado e = null;
        JSONObject object = new JSONObject(aux); //Creamos un objeto JSON a partir de la cadena

        e = new Estado(object);
        return e;
    }

    public synchronized void iniciarServicioEstado(){
        hiloEstado = new HiloEstado();
        hiloEstado.start();
    }



    public synchronized void iniciarServicioDispositivo() //Seria el de leer toda la configuracion, sin excepcion.
    {
        hiloLeerDispositivo = new HiloLeerDispositivo();
        hiloLeerDispositivo.start();
    }

    public synchronized void iniciarServicioDispositivo(String ip) //Seria el de leer toda la configuracion, sin excepcion.
    {
        hiloLeerDispositivo = new HiloLeerDispositivo(ip);
        hiloLeerDispositivo.start();
    }


    public synchronized void iniciarServicioEstadoSeizSafe(){
        hiloEstadoSeizsafe= new HiloEstadoSeizsafe();
        hiloEstadoSeizsafe.start();
    }


    public synchronized void iniciarServicioCamara(){
        hiloEstadoCamara = new HiloEstadoCamara();
        hiloEstadoCamara.start();
    }

    public synchronized void iniciarServicioGuardarGrabacion(boolean guardar_Parar) //Seria el de leer toda la configuracion, sin excepcion.
    {
        hiloGuardarGrabacion = new HiloGuardarGrabacion(guardar_Parar);
        hiloGuardarGrabacion.start();
    }


    public synchronized  void iniciarServicioLeerEstadisticasMovimiento(String inicio, String fin){
        hiloLeerEstadisticas = new HiloLeerEstadisticas(inicio, fin);
        hiloLeerEstadisticas.start();
    }

    public synchronized void pararServiciosEstadoSeizsafe(){ hiloEstadoSeizsafe.interrupt();
    }


    private class HiloEstado extends Thread
    {
        String ip="";
        public HiloEstado()
        {

        }
        public HiloEstado(String ip)
        {

        }
        public void run()
        {
            InputStream con = null;
            String salida="";
            try {
                if (ip.equals("")){
                    ip=Menu.ip;
                }
                URL url = new URL("http://"+Menu.ip+"/api/dispositivo/estado");
                con = url.openStream();
                DataInputStream dis = new DataInputStream(con);
                String linea;
                while ((linea= dis.readLine())!= null){
                    salida = salida+linea;
                }
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
                salida="False";
            }finally {
                handler.obtainMessage(1,"Estado;"+salida).sendToTarget();
            }

        }
    }


    private class HiloLeerDispositivo extends Thread
    {
        private String direccion="";
        public HiloLeerDispositivo()
        {

        }

        public HiloLeerDispositivo(String direccion){
            this.direccion=direccion;
        }
        public void run()
        {
            InputStream con = null;
            String salida="";
            URL url=null;
            try {
                if (direccion.equals("")) direccion =Menu.ip;
                url = new URL("http://"+direccion+"/api/dispositivo");
                con = url.openStream();
                DataInputStream dis = new DataInputStream(con);
                String linea;
                while ((linea= dis.readLine())!= null){
                    salida = salida+linea;
                }
                dis.close();
                handler.obtainMessage(1,"Dispositivo;"+salida+";"+direccion).sendToTarget();
            } catch (IOException e) {
                handler.obtainMessage(1,"Error;"+direccion).sendToTarget();
                e.printStackTrace();
            }

        }
    }


    private class HiloEstadoSeizsafe extends Thread
    {
        public HiloEstadoSeizsafe()
        {

        }
        public void run()
        {
            InputStream con = null;
            String salida="False";
            try {
                URL url = new URL("http://"+Menu.ip);
                con = url.openStream();
                salida="True";
            } catch (IOException e) {
            }
            finally {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    salida="False";
                }
                handler.obtainMessage(1,"Seizsafe;"+salida).sendToTarget();
            }
        }
    }


    private class HiloEstadoCamara extends Thread
    {
        public HiloEstadoCamara()
        {

        }
        public void run()
        {
            InputStream con = null;
            String salida="";
            try {
                URL url = new URL("http://"+Menu.ip+"/api/dispositivo/estado");
                con = url.openStream();
                DataInputStream dis = new DataInputStream(con);
                String linea;
                while ((linea= dis.readLine())!= null){
                    salida = salida+linea;
                }
                dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                handler.obtainMessage(1,"Camara;"+salida).sendToTarget();
            }

        }
    }



//https://seizsafe.encore-lab.com/api/graficas/2/3/20160101-20160121
    private class HiloLeerEstadisticas extends Thread
    {
        private List<Series> ls;
        private String inicio;
        private String fin;
        public HiloLeerEstadisticas(String inicio, String fin){
            this.inicio= inicio;
            this.fin = fin;
        }
        public void run(){
            InputStream in = null;
            try{
                URL url = new URL("https://seizsafe.encore-lab.com/datos2.json");
                //URL url = new URL("https://seizsafe.encore-lab.com/datos.json");
                //URL url = new URL("http://" + Menu.ip + "/api/graficas/2/3/"+inicio+"-"+fin);
                in = new BufferedInputStream(url.openConnection().getInputStream());
                ls = crearSeries(in);
                handler.obtainMessage(1,ls).sendToTarget();
            }catch (Exception e ){
                e.printStackTrace();
            }
        }
    }

    //Lo nuevo para los sensores señores.

    public static Object jsonToMap(JSONObject json) throws JSONException {
        Object o = new Object();

        if(json != JSONObject.NULL) {
            o = toMap(json);
        }
        return o;
    }

    public static Object toMap(JSONObject object) throws JSONException {
        Object o = new Object();

        if (object.getString("tipo").equals("Acelerometros")){
            Acelerometro a = new Acelerometro(object);
            o = a;
        }else if (object.get("tipo").equals("Evento")){
            if (!object.isNull("id")){
                Evento e = new Evento(object);
                o = e;
            }
        } else if (object.get("tipo").equals("Sesion")){
        boolean b = object.getBoolean("iniciado");
        o = b;
    }
        return o;
    }



    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
    private class HiloGuardarGrabacion extends Thread
    {
        private boolean grabar_Parar;
        private Estado e = null;
        public HiloGuardarGrabacion(boolean grabar_Parar)
        {
            this.grabar_Parar=grabar_Parar;
        }

        public HiloGuardarGrabacion(Estado e)
        {
            this.e=e;
        }


        public void run()
        {
            InputStream inputStream = null;
            String result="";
            HttpPost httpost = null; //Se envia mediante put o post falta mirar
            try {
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make POST request to the given URL

                String json = "";
                JSONObject jsonObject = new JSONObject();

                if (e==null) {
                    if (grabar_Parar){
                        httpost = new HttpPost("http://" + Menu.ip + "/api/dispositivo/iniciar");
                        jsonObject.accumulate("", true);
                    }else{
                        httpost = new HttpPost("http://" + Menu.ip + "/api/dispositivo/parar");
                        jsonObject.accumulate("", true);
                    }

                }else {// Esta es la parte del estado
                    if (e.isAutomatico()){
                        httpost = new HttpPost("http://" + Menu.ip + "/api/dispositivo/programar");
                        jsonObject.accumulate("automatico", true);
                        jsonObject.accumulate("inicio", e.getProgramacion().getInicio());
                        jsonObject.accumulate("fin", e.getProgramacion().getFin());

                    }else{
                        httpost = new HttpPost("http://" + Menu.ip + "/api/dispositivo/programar");
                        jsonObject.accumulate("automatico", false);
                    }
                }

                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpost.setHeader("Accept", "application/json");
                httpost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();


                // 10. convert inputstream to string
                if(inputStream != null) {
                    result = httpResponse.getStatusLine()+";"+convertInputStreamToString(inputStream);
                }else
                    result = getString(R.string.Error_Json);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }
    }





}
