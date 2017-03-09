package com.example.root.Seizsafe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ListView;

import com.example.root.myapplication.R;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by jonatan.Gonzalez on 18/12/2015.
 */
public class ServicioWifi extends Service {
    private NotificationManager nm;
    private static final int ID_NOTIFICACION_CREAR = 1;
    private HashMap<String, Integer> hError = new HashMap<>();
    private HashMap<String , Boolean> hEstado = new HashMap<>();
    private HashMap <String,HiloDispositivo> hDisp = new HashMap();
    private List <ListSeizsafeMenu> listMenu = new ArrayList<>();//Creo que ya no se usa pero por tiempo no puedo mirar
    private ListView listRasp;
    private int Error = 60;
    public static Menu UPDATE_LISTENER;
    private String cadena;
    private String[] rasp;
    private Evento e;
    // Prueba con el flas
    private Camera camera = null;
    private Camera.Parameters parameters;
    //--------------


    private boolean cerrado = false;


    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Evento) {
                e = (Evento) msg.obj;
                e.setRevisado(true);
                e.setEsCrisis(false);
                showNotification(e.getIp());
                cambiarEstado(e.getIp());
            } else if (msg.obj instanceof Acelerometro){
                cadena = "Open";
                Acelerometro a = (Acelerometro) msg.obj;
                modificar(a.getIp());
                cadena = cadena+":"+a.getIp();
                setListRasp(cadena, a.getlSensor());
            }else if (msg.obj instanceof String){
                if (!cerrado){// Como al cerrar entra en error tengo que controlar cuando relamete se ha cerrado la conexion
                    cadena = (String) msg.obj;
                    rasp = cadena.split(":");
                    //añadirError(rasp[0]);
                    if (rasp[1].equals("Error")){
                        if (!cerrado){// Como al cerrar entra en error tengo que controlar cuando relamete se ha cerrado la conexion
                            añadirError(rasp[0]);
                            setListRasp(cadena);//Se popdria quitar ya que se hace al final del if pero por tiempo no pruebo
                        }
                    }else if (rasp[1].equals("Open")){
                        modificar(rasp[0]);
                        UPDATE_LISTENER.sesion = true;
                    }else if (rasp[1].equals("Sesion")){
                        if (rasp[2].equals("False")){
                            pararRaspberry(rasp[0]);
                            UPDATE_LISTENER.sesion = false;
                        }
                    }
                    setListRasp(cadena);
                }
            }
        }
    };



    private void cambiarEstado(String direccion) {
        UPDATE_LISTENER.hAtaque.put(direccion, true);
    }

    private void modificar(String s) {
        if (hError.containsKey(s)) {
            hError.remove(s);
            hError.put(s, 0);
        }
    }

    private void añadirError(String s) {
        int cont=0;
        pararRaspberry(s);
        if (comprobarAntenaWifi()) {//Comprobar si la antena wifi esta conectada.
            if (hError.containsKey(s)) {
                cont = hError.get(s);
                if (hEstado.get(s)) {//Necesario, ya que los hilos Thread no funciona el interrup
                    if (cont >= Error) {
                        pararRaspberry(s);
                        NotificationError(s);//Luego mandar la ip para saber de cual se ha perdido la conexion.
                        reiniciarErrores(s);
                    } else {
                        hError.remove(s);
                        hError.put(s, cont + 1);
                        anadirRaspberry(s);
                        iniciarRaspberry(s);
                    }
                } else {
                    Log.d("Sevicio", "El servicio esta parado");
                }
            } else {
                if (hEstado.get(s)) {
                    hError.put(s, cont);
                    anadirRaspberry(s);
                    iniciarRaspberry(s);
                } else {
                }
            }
        }else{
            NotificationConexion();
        }
    }

    public boolean comprobarAntenaWifi(){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public ServicioWifi() {
    }

    /**
     * Establece quien va ha recibir las actualizaciones del socket
     *
     * @param poiService
     */

    public static void setUpdateListener(Menu poiService) {
        UPDATE_LISTENER = poiService;
    }

    public void onCreate(){
        super.onCreate();
        try {
            listRasp = (ListView) UPDATE_LISTENER.findViewById(R.id.listRasp);
        }catch (Exception e){
            e.printStackTrace();
        }
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        Log.i("Local Service", "Received start id " + startId + ": " + intent);
        cerrado = false;
        //Queremos que el servicio continúe ejecutándose hasta que es explícitamente parado, así que devolvemos sticky
        return START_REDELIVER_INTENT;
    }

    private void showNotification(String direccion){// esta es la de la alerta ,------------------------------------
        CharSequence text = getText(R.string.local_service_started);
        //Ajustamos el icono, desplazamiento del texto y la hora y fecha
        Notification notification = new Notification(R.drawable.alerta, text, System.currentTimeMillis());
        long[] pattern = {500,1000,500,1000,500,1000,500,1000,500};

        notification.vibrate = pattern;
        notification.sound =  Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarma);

        Intent i = new Intent(ServicioWifi.this, Reproductor_Stream.class);
        i.putExtra("ip", direccion);
        Gson gson = new Gson();
        i.putExtra("notificar", gson.toJson(e));
        Menu.evento=gson.toJson(e);
        i.setData((Uri.parse("foobar://" + SystemClock.elapsedRealtime())));

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);
        //Ajustamos la información para mostrar en el panel de notificación
        notification.setLatestEventInfo(this, getText(R.string.local_service_label), text, contentIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //Enviamos la notificación
        nm.notify(ID_NOTIFICACION_CREAR, notification);

        /*Pruebas con el flash*/


        Thread th = new Thread( new Runnable() {
            public void run() {
                if(camera == null) {
                    try {
                        camera = Camera.open();
                        parameters = camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }finally {
                    try {
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        camera.release();
                        camera = null;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();

        encenderPantalla();

    }

    private void NotificationError(String direccion){
        //Pre nada
        //Post lo que conseguimos en este metodo es que cuando se pierda la conexion durante x tiempo nos salte una notificacion indicandonos que hemos perdido la comunicacion.
        CharSequence text = getText(R.string.local_service_started_conexion);
        //Ajustamos el icono, desplazamiento del texto y la hora y fecha
        Notification notification = new Notification(R.drawable.stop, text, System.currentTimeMillis());
        long[] pattern = {500, 1000, 500,1000,500,1000,500,1000,500};

        notification.vibrate = pattern;
        notification.sound =  Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alerta);

        //PendingIntent para lanzar nuestra actividad si el usuario selecciona esta notificación
        Intent i = new Intent(this, Menu.class);
        i.putExtra("Conexion", true);
        i.putExtra("ip", direccion);
        i.setData((Uri.parse("foobar://" + SystemClock.elapsedRealtime())));
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        //Ajustamos la información para mostrar en el panel de notificación
        notification.setLatestEventInfo(this, getText(R.string.local_service_conexion), text, contentIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //Enviamos la notificación
        nm.notify(ID_NOTIFICACION_CREAR, notification);
        encenderPantalla();
    }

    private void NotificationConexion(){
        //Pre nada
        //Post lo que conseguimos en este metodo es que cuando se pierda la conexion durante x tiempo nos salte una notificacion indicandonos que hemos perdido la comunicacion.
        CharSequence text = getText(R.string.local_service_started_conexion);
        //Ajustamos el icono, desplazamiento del texto y la hora y fecha
        Notification notification = new Notification(R.drawable.stop, text, System.currentTimeMillis());
        long[] pattern = {500, 1000, 500,1000,500,1000,500,1000,500};

        notification.vibrate = pattern;
        notification.sound =  Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alerta);

        //PendingIntent para lanzar nuestra actividad si el usuario selecciona esta notificación
        Intent i = new Intent(this, Menu.class);
        i.putExtra("wifi", true);
        i.setData((Uri.parse("foobar://" + SystemClock.elapsedRealtime())));
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        //Ajustamos la información para mostrar en el panel de notificación
        notification.setLatestEventInfo(this, getText(R.string.local_service_conexion), text, contentIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //Enviamos la notificación
        nm.notify(ID_NOTIFICACION_CREAR, notification);
        encenderPantalla();
    }

    public void pararhilos(){
        cerrado = true;
        for (String key : hEstado.keySet()) {
            hEstado.put(key,false);
            if (hDisp.containsKey(key)) hDisp.get(key).pararServicio();
        }
    }

    public void pararhilo(String direccion){
        if (hDisp.containsKey(direccion)){
            hEstado.put(direccion, false);
            hDisp.get(direccion).pararServicio();
            hDisp.remove(direccion);
        }
    }


    public class MyBinder extends Binder {
        ServicioWifi getService() {
            return ServicioWifi.this;
        }
    }

    private final IBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void anadirRaspberry(String ip){
        // Pre nos envian la direccion ip del dispositivo al cual vamos a relizar el seguimineto
        //Pos guardamos un nuevo hilo en el Hasmap.
        if (!hDisp.containsKey(ip)) {
            HiloDispositivo hd = new HiloDispositivo(ip, handler);
            hDisp.put(ip, hd);
        }
        hEstado.put(ip,true);
    }

    public void iniciarRaspberry(String ip){
        //Pre nos mandan la direccion ip que queremos que arranque.
        //Post iniciamos el servicio de alertas de la ip que nos han indicado.
        Log.i("Raspberry", "Creacion de raspberry"+ip);
        System.out.println(hDisp.toString());
        if (!ip.equals(""))if (hDisp.containsKey(ip)) if (!hDisp.get(ip).isAlive()){
            hDisp.get(ip).start();
        }
    }

    public void pararRaspberry(String ip){
        //Pre nos indican la direccion ip que quieren parar
        //Post borramos del Hasmap el hilo correspondiente.
        Log.i("Raspberry", "Parar raspberry");
        int i=0;
        boolean encontrado=false;
        hDisp.remove(ip);
        while (i<listMenu.size() && encontrado==false){
            if (listMenu.get(i).getIp().equals(ip)){
                listMenu.remove(i);
                encontrado=true;
            }else{
                i++;
            }
        }
    }

    public void reiniciarErrores(String ip){
        //Pre nos mandan la direccion ip de la raspberry que quieren reiniciar el sistema de errores
        //Post ponemos a 0 el contador del hasmap de errores.
        Log.i("Raspberry", "Reiniciar secuencia de errores");
        if (hError.containsKey(ip)){
            hError.remove(ip);
            hError.put(ip,0);
        }
    }


    public void setListRasp(String direc_esta){
        //Pre mandamos el la ip y el estado de la conexion, 192.168.1.91:Open o Error
        //Post mostramos el estado del sistema de alertas para cada raspberry
        String[] dieresta = direc_esta.split(":");
        boolean encontrado= false;
        int i =0;
        if (listMenu.size()>0) {
            while (i< listMenu.size()&& encontrado!=true){
                if (UPDATE_LISTENER.hDis.containsKey(dieresta[0])){
                    if (listMenu.get(i).getIp().equals(dieresta[0])){
                        encontrado=true;
                        if (dieresta[1].equals("Open")){
                            //MAñana hay que trabajar aqui
                            listMenu.set(i, new ListSeizsafeMenu(UPDATE_LISTENER.hDis.get(dieresta[0]).getNombre(),Menu.codigo, true, dieresta[0]));
                        }else{
                            listMenu.set(i, new ListSeizsafeMenu(UPDATE_LISTENER.hDis.get(dieresta[0]).getNombre(),Menu.codigo, false, dieresta[0]));
                        }
                    }
                }
                i++;
            }
        }
        if (i==listMenu.size()&&!encontrado){
            if (UPDATE_LISTENER.hDis.containsKey(dieresta[0])){
                if (dieresta[1].equals("Open")){
                    listMenu.add( new ListSeizsafeMenu(UPDATE_LISTENER.hDis.get(dieresta[0]).getNombre(),Menu.codigo,true, true, dieresta[0]));
                }else{
                    listMenu.add( new ListSeizsafeMenu(UPDATE_LISTENER.hDis.get(dieresta[0]).getNombre(),Menu.codigo,true, false, dieresta[0]));
                }
            }
        }

        AdaptadorSeizsafeMenu ad = new AdaptadorSeizsafeMenu(this,getServicio(), listMenu, UPDATE_LISTENER);
        if (listRasp!=null) listRasp.setAdapter(ad);
    }

    public void setListRasp(String direc_esta, List<Sensor> lSensor){
        //Pre mandamos el la ip y el estado de la conexion, 192.168.1.91:Open o Error
        //Post mostramos el estado del sistema de alertas para cada raspberry

        String[] dieresta = direc_esta.split(":");
        boolean encontrado= false;
        int i =0;

        if (listMenu.size()>0) {
            while (i< listMenu.size()&& encontrado!=true){
                if (UPDATE_LISTENER.hDis.containsKey(dieresta[1])){
                   if (listMenu.get(i).getIp().equals(dieresta[1])){
                       encontrado=true;
                       if (dieresta[0].equals("Open")){
                           listMenu.set(i, new ListSeizsafeMenu(UPDATE_LISTENER.hDis.get(dieresta[1]).getNombre(),Menu.codigo,true,  true, dieresta[1],lSensor));
                       }else{
                           listMenu.set(i, new ListSeizsafeMenu(UPDATE_LISTENER.hDis.get(dieresta[1]).getNombre(),Menu.codigo,true,  false, dieresta[1],lSensor));
                       }
                   }
                }
                i++;
            }
        }
        if (i==listMenu.size()&&!encontrado){
            if (UPDATE_LISTENER.hDis.containsKey(dieresta[1])){
                if (dieresta[0].equals("Open")){
                    listMenu.add( new ListSeizsafeMenu(UPDATE_LISTENER.hDis.get(dieresta[1]).getNombre(),Menu.codigo,true,  true, dieresta[1],lSensor));
                }else{
                    listMenu.add( new ListSeizsafeMenu(UPDATE_LISTENER.hDis.get(dieresta[1]).getNombre(),Menu.codigo,true,  false, dieresta[1],lSensor));
                }
            }
        }

        AdaptadorSeizsafeMenu ad = new AdaptadorSeizsafeMenu(this,getServicio(),listMenu,UPDATE_LISTENER);
        listRasp.setAdapter(ad);

    }

    public HashMap<String, Integer> gethError() {
        return hError;
    }

    public void sethError(HashMap<String, Integer> hError) {
        this.hError = hError;
    }

    public int getError() {
        return Error;
    }


    public List<ListSeizsafeMenu> getListMenu() {
        return listMenu;
    }

    public void setListMenu(List<ListSeizsafeMenu> listMenu) {
        this.listMenu = listMenu;
        AdaptadorSeizsafeMenu ad = new AdaptadorSeizsafeMenu(this,getServicio(),listMenu,UPDATE_LISTENER);
        listRasp.setAdapter(ad);
    }
    public ListView getListRasp() {
        return listRasp;
    }

    public void setListRasp(ListView listRasp) {
        this.listRasp = listRasp;
    }

    public HashMap<String, HiloDispositivo> gethDisp() {
        return hDisp;
    }

    private void encenderPantalla(){
        PowerManager.WakeLock screenLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();

        screenLock.release();
    }

    private ServicioWifi getServicio(){
        return this;
    }
}
