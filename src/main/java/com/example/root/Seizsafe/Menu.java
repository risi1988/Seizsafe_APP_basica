package com.example.root.Seizsafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.root.myapplication.R;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.jmdns.ServiceEvent;

@SuppressWarnings("serial")
public class Menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Serializable {
    private Estado estado;
    public String cadena="";
    public Utiles uth = null; //Con el handler para el hilo.
    public static String ip = "";
    public static String codigo = "";
    public static List<String> lip = new ArrayList<>();
    private List lipCod = new ArrayList();
    private List<String> lcod = new ArrayList<>(); //Nombres de las raspberrys,lo usamos para mostrar datos
    private List<String> lreiniciar = new ArrayList<>(); //Ip de las raspberrys que se han quedado sin conexion
    public static HashMap <String, Boolean> hAtaque = new HashMap<>(); //Nos indica que raspberry tiene un ataque para arrancar la ventana de reproductor en streaming
    public HashMap<String,Seizsafe> hDis = new HashMap<>(); //Ip y el objeto Seizsafe
    public HashMap<String,String> hNomIp = new HashMap<>(); //Nombre de la raspberry y la ip que tiene
    public HashMap<String,String> hCod = new HashMap<>();
    private ListView listRasp;//Lista Raspberry que el usuario a introducido.
    public String idioma = "es";
    public static final String nomFichero= "Ip.txt";
    public static final String nomFichero_idioma= "Idioma.txt";
    private NotificationManager nm;
    private BroadcastReceiver receiver;
    public Intent si =null;
    private boolean error_Conexion = false;
    private boolean seizsafe = true;
    private final int COD_BUSWIFI = 1001;
    private ServicioWifi s;
    public static ImageButton ImageAnadir;
    public static String evento;
    public static boolean sesion = false; //necesario para el reproductor de video en Streaming ya que sino falla al parar el vidéo
    public boolean parar=false; //variable del txtcenter que hace que al usuario no activar y desactivar la camara al instante



    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg)
        {
            cadena = (String) msg.obj;
            String [] estadoCamara = cadena.split(";");
            if(cadena.startsWith("Seizsafe")){
                uth.pararServiciosEstadoSeizsafe();
                if (estadoCamara[1].equals("True")) {
                    seizsafe = true;
                    estadoSeizsafe();
                }
                else {
                    seizsafe = false;
                    estadoSeizsafe();
                }
            }else if (cadena.startsWith("Estado")) {
                if (estadoCamara[1].startsWith("False")){
                    Toast toast1 = Toast.makeText(getApplicationContext(),getString(R.string.Conexion_erronea), Toast.LENGTH_LONG);
                    noEnlazado();
                    ImageAnadir.setVisibility(View.VISIBLE);
                    toast1.show();
                    seizsafe = false;
                }else{
                    seizsafe = true;
                }
            }else if (cadena.startsWith("Camara")){
                try {
                    estado = uth.crearEstado(estadoCamara[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (estado.isEstado()){
                    Intent i = new Intent(Menu.this, Reproductor_Stream.class);
                    i.putExtra("ip",Menu.ip);
                    startActivityForResult(i, 1);
                }else{
                    Toast toast1 = Toast.makeText(getApplicationContext(), getString(R.string.Camara_Off), Toast.LENGTH_LONG);
                    toast1.show();
                }
            }else if (cadena.startsWith("Dispositivo")){
                ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progressBar6);
                if (progressBar2.getVisibility()==View.VISIBLE) progressBar2.setVisibility(View.INVISIBLE);
                if (listRasp.getVisibility()==View.INVISIBLE) listRasp.setVisibility(View.VISIBLE);
                if (ImageAnadir.getVisibility()==View.VISIBLE) ImageAnadir.setVisibility(View.INVISIBLE);
                String[] dispositivos = cadena.split(";");
                try {
                    hDis.put(dispositivos[2], uth.crearSeizsafe(dispositivos[1]));
                    hDis.get(dispositivos[2]).setIp(dispositivos[2]);
                    hNomIp.put(hDis.get(dispositivos[2]).getNombre(), dispositivos[2]);
                    String nombreArray[] = hDis.get(dispositivos[2]).getNombre().split(" ");
                    starServicio(dispositivos[2]);
                    //mensajeAlerta(dispositivos[2]);
                    ip = dispositivos[2];
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else if (cadena.startsWith("Error")){
                ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progressBar6);
                if (progressBar2.getVisibility()==View.VISIBLE) progressBar2.setVisibility(View.INVISIBLE);
                if (listRasp.getVisibility()==View.INVISIBLE) listRasp.setVisibility(View.VISIBLE);
                if (ImageAnadir.getVisibility()==View.INVISIBLE) ImageAnadir.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), getString(R.string.Codigo_Incorrecto), Toast.LENGTH_LONG).show();
            }
        }
    };

    public Menu() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (String key : hAtaque.keySet()) {
            if (hAtaque.get(key)){
                Intent i = new Intent(Menu.this, Reproductor_Stream.class);
                i.putExtra("ip",key);
                i.putExtra("notificar", Menu.evento);
                startActivityForResult(i,1);
            }
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
        setContentView(R.layout.activity_menu);
        uth = new Utiles(handler); //Con el handler para el hilo.
        listRasp = (ListView) findViewById(R.id.listRasp);
        listRasp.setEnabled(false);
        leerIP();

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {

            }
        };

        registerReceiver(receiver, new IntentFilter(MiServiceIntent.INTENTSERVICE));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        leerIdioma();
        cargarIdioma();

        if (comprobarAntenaWifi()) {
            //Iniciamos el servicio de recepcion de alertas
            ServicioWifi.setUpdateListener(this);
            si = new Intent(this, ServicioWifi.class);
            startService(si);
            try {
                getApplicationContext().bindService(si, sConnectionIB, Context.BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
            }


            //Cargar las raspberrys en el mapa ip,Seizsafe
            if (!ip.equals("")) {//Esta por aqui el errorr
                if (!lip.contains(Menu.ip)) lip.add(Menu.ip);
                for (int i = lip.size() - 1; i >= 0; i--)
                    uth.iniciarServicioDispositivo(lip.get(i));
            }



            //Comprobar si hay conexion con seizsafe
            estadoSeizsafe();
            //Arrancamos los hilos con todas las raspberris.
            //mensajeAlerta("");


            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            ImageAnadir = (ImageButton) findViewById(R.id.AnadirSeizsafe);
            if (Menu.ip != "") {
                ImageAnadir.setVisibility(View.GONE);
            }
            ImageAnadir.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mensajeIp();
                }
            });

        }else Toast.makeText(getApplicationContext(), getString(R.string.AvisoErrorWifiReinicio), Toast.LENGTH_SHORT).show();

    }

    public boolean comprobarAntenaWifi(){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }



    @Override
    public void onNewIntent(Intent intent){
        Bundle extras = intent.getExtras();
        String direccion;
        if(extras != null){
            if(extras.containsKey("Conexion"))
            {
                // extract the extra-data in the Notification
                error_Conexion = extras.getBoolean("Conexion");
                direccion = extras.getString("ip");
                if (error_Conexion){
                    mensaje();
                }
            }else if (extras.containsKey("wifi")){
                mensajeErrorWifi();
            }
        }
    }

    private void mensajeErrorWifi() {

        //Pre nada
        //Post Pedimos al usuario una respuesta ya que no podemos tomar la decision nosotros de si desea reintentar.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        s.reiniciarErrores(Menu.ip);
        builder.setMessage(getString(R.string.Alertas_Off))
                .setTitle(getString(R.string.Conexion_erronea))
                .setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "Confirmacion Aceptada.");
                        dialog.cancel();
                    }
                });

        builder.show();

    }

    private void leerIP() {
        //Pre nada
        //Post lemos del fichero Ip.txt cual es la IP de la última raspberry conectada.
        String[] auxIP;
        String[] auxCodIP;
        try
        {
            BufferedReader fin =new BufferedReader(new InputStreamReader(openFileInput(nomFichero)));
            String texto = fin.readLine();
            fin.close();
            auxCodIP = texto.split(";");
            for (int i =0; i<auxCodIP.length; i++){
                auxIP= auxCodIP[i].split("-");
                if (!lip.contains(auxIP[0])){
                    lip.add(auxIP[0]);
                    lipCod.add(auxCodIP[i]);
                }
            }
            auxIP= auxCodIP[0].split("-");
            ip=auxIP[0];
            codigo=auxIP[1];
            Log.i("Ficheros", "Texto: " + texto);
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }
    }

    private void leerIdioma() {
        //Pre nada
        //Post llemos del fichero Idioma.txt el idioma que el usuario ha guardado.
        try
        {
            BufferedReader fin =new BufferedReader(new InputStreamReader(openFileInput(nomFichero_idioma)));
            String texto = fin.readLine();
            fin.close();
            idioma=texto;
            Log.i("Ficheros", "Texto: " + texto);
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        String aux ="";
        for (int i = 0;i<lipCod.size(); i++){
            aux=aux+lipCod.get(i).toString()+";";
        }
        guardarIP(aux);
        if (s!=null) {
            s.pararhilos();
            stopServicio();
        }
        hAtaque.clear();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (comprobarAntenaWifi()) {
            if (id == R.id.nav_video_directo) {
                // es necesario comprobar
                leerIP();
                System.out.println("Como esta la sesion "+ sesion);
                if (sesion) {
                    if (!Menu.ip.equals("")) {
                        uth.iniciarServicioEstado();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (seizsafe) {
                            uth.iniciarServicioCamara();

                        }
                    }
                }else {
                    Toast toast1 = Toast.makeText(getApplicationContext(), getString(R.string.Camara_Off), Toast.LENGTH_LONG);
                    toast1.show();
                }
            } else if (id == R.id.nav_borrar_Seizsafe) {
                if (Menu.ip != "") {
                    mensajeBorrar();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.ErrorIP), Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.nav_idioma) {
                SelecionarIdioma();
            } /*else if (id == R.id.nav_manual) {
                Intent i = new Intent(this, Manual.class);
                startActivity(i);
            }*/

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }else Toast.makeText(getApplicationContext(), getString(R.string.AvisoErrorWifi), Toast.LENGTH_SHORT).show();
        return true;
    }

    public void mensajeBorrar(){
        //Pre nada
        //Post borra la raspberry del sistema
        AlertDialog.Builder builder =
                new AlertDialog.Builder(Menu.this);
        //Aqui hay que comprobar las cosas -----------------------------------------------------
        if (hDis.get(Menu.ip)==null){
            cadena = "Fallo";
        }else cadena =hDis.get(Menu.ip).getNombre();
        final String finalCadena = cadena;
        builder.setMessage("¿ " + getString(R.string.Mensaje_Borrar_Seizsafe) + " "+cadena + " ?")
                .setTitle(getString(R.string.Borrar_Seizsafe))
                .setPositiveButton(getString(R.string.Aceptar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "Confirmacion Aceptada.");
                        pararAlertas(hNomIp.get(finalCadena));
                        borrarAlertas(hNomIp.get(finalCadena));
                        if (lip.size()==0){
                            guardarIP("", "");
                        }
                        ImageAnadir.setVisibility(View.VISIBLE);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(getString(R.string.Cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "Confirmacion Cancelada.");
                        dialog.cancel();
                    }
                });
        builder.show();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COD_BUSWIFI) {
            if (si!=null){
                stopServicio();
            }
            leerIP();
            mensajeAlerta(ip);
        }else if (requestCode==3){
            if (resultCode == Activity.RESULT_OK){
                String nombre_viejo=data.getStringExtra("nombre_viejo");
                String nombre_nuevo=data.getStringExtra("nombre_nuevo");
                cambioNombre(nombre_viejo,nombre_nuevo);
            }

        }

    }

    private void estadoSeizsafe(){
        //Pre empezamos a coprobar la camara cuando sepamos que la raspberry esta conectada.
        //Post comprobamos como esta la camara cada 20 seg aprox. En caso de ser true cambiamos el estado del txtEstadoCamara.
        uth.iniciarServicioEstado();
    }

    private void mensaje(){
        //Pre nada
        //Post Pedimos al usuario una respuesta ya que no podemos tomar la decision nosotros de si desea reintentar.
        lreiniciar.clear();
        for (String key : s.gethError().keySet()) {
            if (s.getError() == s.gethError().get(key)){
                lreiniciar.add(key);
            }
        }

        for (int i =0;i<lreiniciar.size();i++){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final int finalI = i;
            builder.setMessage(getString(R.string.Alertas_Off) + hDis.get(lreiniciar.get(i)).getNombre())
                        .setTitle(getString(R.string.Conexion_erronea))
                        .setPositiveButton(getString(R.string.Reintentar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i("Dialogos", "Confirmacion Aceptada.");
                                s.reiniciarErrores(lreiniciar.get(finalI));
                                starServicio(lreiniciar.get(finalI));
                                dialog.cancel();
                            }
                        })

                        .setNegativeButton(getString(R.string.parar),

                                new DialogInterface.OnClickListener()

                                {
                                    public void onClick(DialogInterface dialog, int id) {
                                        s.reiniciarErrores(lreiniciar.get(finalI));
                                        Log.i("Dialogos", "Confirmacion Cancelada.");
                                        dialog.cancel();
                                    }
                                });
            builder.show();
        }
    }

    public void mensajeIp(){
        //Pre nada
        //Post abrimos un layout como si seria un dialog custom pero en este caso es personalizado.


        final LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.ip, null);
        final EditText etAsunto = (EditText) dialoglayout.findViewById(R.id.et_EmailAsunto);
        Button btnEnviarMail = (Button) dialoglayout.findViewById(R.id.btnEnviarMail);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setView(dialoglayout);
        alertDialog.show();

        btnEnviarMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listRasp.setVisibility(View.INVISIBLE);
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar6);
                progressBar.setVisibility(View.VISIBLE);
                final String subject = etAsunto.getText().toString();
                final ThreadPool pool = new ThreadPool(Menu.this, "seizsafe-"+subject,Menu.this);
                new Thread (new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pool.start();
                            int i = 0;
                            boolean encontrado = false;
                            while (i < 35 && encontrado == false) {
                                Thread.sleep(1000);
                                i++;
                                encontrado = pool.isEncontrado();
                            }
                            pool.setParar(false);
                            pool.interrupt();
                            if (!hCod.containsKey("seizsafe-"+subject)) {
                                guardarIP("", "");
                                leerIP();
                                uth.iniciarServicioDispositivo();
                            }else{
                                guardarIP(hCod.get("seizsafe-" + subject), subject);
                                leerIP();
                                if (!lip.contains(hCod.get("seizsafe-"+subject))) lip.add(hCod.get("seizsafe-"+subject));
                                uth.iniciarServicioDispositivo();
//                                Menu.ImageAnadir.setVisibility(View.INVISIBLE);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                alertDialog.cancel();
            }
        });


    }



    private void guardarIP(String ipCod){//Esta funcion esta tambien en el fichero de Configurar_Conexion y en Buscar_SeizSafe
        //Pre nos mandan una String con la IP que queremos que se guarde en el fichero
        //Post guardamos la IP en el fichero acordado
        try
        {
            OutputStreamWriter fout=new OutputStreamWriter(openFileOutput(Menu.nomFichero, Context.MODE_PRIVATE));
            fout.write(ipCod);
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    private void guardarIP(String ip, String codigo){//Esta funcion esta tambien en el fichero de Configurar_Conexion y en Buscar_SeizSafe
        //Pre nos mandan una String con la IP que queremos que se guarde en el fichero
        //Post guardamos la IP en el fichero acordado
        try
        {
            OutputStreamWriter fout=new OutputStreamWriter(openFileOutput(Menu.nomFichero, Context.MODE_PRIVATE));
            this.ip=ip;
            this.codigo=codigo;
            fout.write(ip+"-"+codigo);
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    private void guardarIdioma(String idioma){//Esta funcion esta tambien en el fichero de Configurar_Conexion y en Buscar_SeizSafe
        //Pre nos mandan una String con el idioma que queremos que se guarde en el fichero
        //Post guardamos la IP en el fichero acordado
        try
        {
            OutputStreamWriter fout=new OutputStreamWriter(openFileOutput(Menu.nomFichero_idioma, Context.MODE_PRIVATE));
            fout.write(idioma);
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }
    }

    private void cargarIdioma() {
        //Pre nada
        //Post Aqui en caso de que el usuario decida cambiar el lenguaje de la aplicación, lo podra realizar
        if (idioma.equals("es")){
            String languageToLoad = "es"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

        }else if(idioma.equals("en")){
            String languageToLoad = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

        }
    }


    public void mensajeAlerta(final String direccion){
        //Pre nada
        //Post antes de nada comprobamos que no este activo ya las alertas. Si no es asi lo que hacemos es preguntar
        // al usuario que desea hacer y segun lo que nos inidique activaremos el servicio de alertas o no haremos
        // nada de nada.
        if (direccion.equals("")){
            starServicio(direccion);
        }else{
            if (!conectado(direccion)) {
                try {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(this);

                    builder.setMessage(getString(R.string.Activar_Alertas)+"  "+hDis.get(direccion).getNombre())
                            .setTitle(getString(R.string.Activar_alertas))
                            .setPositiveButton(getString(R.string.Aceptar), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.i("Dialogos", "Confirmacion Aceptada.");
                                    if (direccion.equals("")){
                                        if(lip.size()>0){
                                            //Arancamos todos los hilos
                                            for (int i =0; i < lip.size();i++){
                                                starServicio(lip.get(i));
                                            }
                                        }
                                    }
                                    starServicio(direccion);
                                    dialog.cancel();

                                }
                            })
                            .setNegativeButton(getString(R.string.Cancelar), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                Log.i("Dialogos", "Confirmacion Cancelada.");

                                    List <ListSeizsafeMenu> listMenu = new ArrayList<>();
                                    listRasp = (ListView) findViewById(R.id.listRasp);
                                    s.setListMenu(listMenu);
                                    listMenu.add(0, new ListSeizsafeMenu(hDis.get(direccion).getNombre(), false, direccion,null));
                                    AdaptadorSeizsafeMenu ad = new AdaptadorSeizsafeMenu(Menu.this,s,listMenu, Menu.this);
                                    listRasp.setAdapter(ad);

                                dialog.cancel();
                                }
                            });
                    builder.show();
                }catch (Exception e ){
                    e.printStackTrace();
                }
            }else{
            }
        }
    }

    public synchronized void stopServicio(){
        //Paramos el servicio.
        if (si!=null){
            s.onDestroy();
            stopService(new Intent(this, ServicioWifi.class));
            try {
                getApplicationContext().unbindService(sConnectionIB);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private ServiceConnection sConnectionIB = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            s= ((ServicioWifi.MyBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            s= null;
        }
    };

    public synchronized void starServicio(String direccion) {
        if (s==null) {
            ServicioWifi.setUpdateListener(this);
            si = new Intent(this, ServicioWifi.class);
            startService(si);
            try {
                getApplicationContext().bindService(si, sConnectionIB, Context.BIND_AUTO_CREATE);
            } catch (Exception e ){
                e.printStackTrace();
            }
        }else {
            s.anadirRaspberry(direccion);
            Log.i("Menu", "Iniciar servicio");
            s.iniciarRaspberry(direccion);
        }
    }

    public void borrarAlertas(String direccion){
        int i=0;
        boolean encontrado=false;
        if (s!=null) {
            List<ListSeizsafeMenu> lSeizsafeMenus= null;
            s.reiniciarErrores(direccion);
            lip.remove(direccion);
            lipCod.remove(ip+"-"+codigo);
            guardarIP("", "");
            mensajeIp();
            lSeizsafeMenus=s.getListMenu();
            while (i<lSeizsafeMenus.size() && encontrado==false){
                if (lSeizsafeMenus.get(i).getIp().equals(direccion)){
                    encontrado=true;
                }else{
                    i++;
                }
            }
            if (encontrado){
                lSeizsafeMenus.remove(i);
                s.setListMenu(lSeizsafeMenus);
            }
            hDis.remove(direccion);
        }
    }
    private void pararAlertas (String direccion){
        if (s!=null){
            s.pararhilo(direccion);
        }

    }

    private boolean conectado(String direccion){
        Log.i("Menu", "Conectado");
        if (!direccion.equals("")){
            return s.gethDisp().containsKey(direccion);
        }
        else{
            return false;
        }
    }

    public void cambioNombre(String viejo_nombre, String nuevo_nombre){
        int i=0;
        boolean encontrado= false;
        String[] arraynombre = nuevo_nombre.split(" ");
        lcod.remove(viejo_nombre);
        lcod.add(nuevo_nombre);

        while (i<s.getListMenu().size() && encontrado==false){
            if (s.getListMenu().get(i).getIp().equals(hNomIp.get(viejo_nombre))){
                s.getListMenu().get(i).setDispositivo(nuevo_nombre);
            }
            i++;
        }
        s.setListMenu(s.getListMenu());
        hDis.get(hNomIp.get(viejo_nombre)).setNombre(nuevo_nombre);
        hNomIp.remove(viejo_nombre);
        hNomIp.put(nuevo_nombre, ip);

    }


    private void SelecionarIdioma() {
        //Pre nada
        //Post una vez el usuario haya presionado sobre el lenguaje que desea se recarga el programa en el idioma indicado

        final String[] idiomas = {getString(R.string.Español), getString(R.string.Ingles)};

        AlertDialog.Builder builder =
                new AlertDialog.Builder(Menu.this);

        builder.setTitle(getString(R.string.Seleccionar_idioma))
                .setItems(idiomas, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Log.i("Dialogos", "Opción elegida: " + idiomas[item]);

                        if (idiomas[item].equals(getString(R.string.Ingles))){
                            if (s != null) {
                                s.pararhilos();
                                stopServicio();
                            }
                            String languageToLoad = "en"; // your language
                            Locale locale = new Locale(languageToLoad);
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            getBaseContext().getResources().updateConfiguration(config,
                                    getBaseContext().getResources().getDisplayMetrics());
                            Intent i = new Intent(Menu.this, Menu.class);
                            guardarIdioma("en");
                            finish();
                            startActivity(i);

                        }else if (idiomas[item].equals(getString(R.string.Español))){
                            String languageToLoad = "es"; // tu lenguaje
                            if (s != null) {
                                s.pararhilos();
                                stopServicio();
                            }
                            Locale locale = new Locale(languageToLoad);
                            Locale.setDefault(locale);
                            Configuration config = new Configuration();
                            config.locale = locale;
                            getBaseContext().getResources().updateConfiguration(config,
                                    getBaseContext().getResources().getDisplayMetrics());
                            Intent i = new Intent(Menu.this, Menu.class);
                            guardarIdioma("es");
                            finish();
                            startActivity(i);
                        }
                    }
                });

        builder.show();
    }

    private void noEnlazado() {
        List<ListSeizsafeMenu> lista = new ArrayList<>();
        ListSeizsafeMenu listSeizsafeMenu = new ListSeizsafeMenu(false);
        lista.add(listSeizsafeMenu);
        AdaptadorSeizsafeMenu adaptadorSeizsafeMenu = new AdaptadorSeizsafeMenu(getApplicationContext(),false,lista,Menu.this);
        listRasp.setAdapter(adaptadorSeizsafeMenu);
    }

}
