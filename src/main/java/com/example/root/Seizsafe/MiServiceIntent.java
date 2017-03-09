package com.example.root.Seizsafe;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import com.example.root.myapplication.R;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.URL;

//Sirve para unir el servicio con el menu.
public class MiServiceIntent extends IntentService {
	
	public static final String INTENTSERVICE = "intentservice";
	
	public MiServiceIntent() {
		super("MiIntentService");
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Toast.makeText(this, getString(R.string.Servicio_Iniciado), Toast.LENGTH_SHORT).show();
	    return super.onStartCommand(intent,flags,startId);
	}
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		//Pre nada
		//Post nos conectamos a la raspberry para poder ver el numero de sesiones que hay en el servidor.
		InputStream con = null;
		String salida="";
		try {
			URL url = new URL("http://" + Menu.ip + "/api/sesiones");
			if (url.openConnection() != null) {
				con = url.openStream();
				DataInputStream dis = new DataInputStream(con);
				String linea;
				while ((linea = dis.readLine()) != null) {
					salida = salida + linea;
				}
				dis.close();
			}
		}catch (Exception e ){
		}
		Intent set_progreso = new Intent(INTENTSERVICE);
		set_progreso.putExtra("progreso", salida);
		sendBroadcast(set_progreso);
	}
	
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, getString(R.string.Servicio_Finalizado), Toast.LENGTH_SHORT).show();
	}

}