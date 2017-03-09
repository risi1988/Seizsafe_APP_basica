package com.example.root.Seizsafe;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by jonatan.Gonzalez on 13/04/2016.
 */
public class ThreadPool extends Thread{
    private Context context;
    private HashMap<String,String> hCod = new HashMap<>();
    private boolean parar = true;
    private String codigo;
    private boolean encontrado = false;
    private String codIP ="";
    private Menu menu;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String cadena = (String) msg.obj;
            String[] arrayCod = cadena.split("/");
            menu.hCod.put(arrayCod[0],arrayCod[1]);
            if ( menu.hCod.containsKey(codigo) && encontrado ==false){
                encontrado=true;
                codIP=codigo+"/"+menu.hCod.get(codigo);
            }

        }
    };

    public ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(5);
    private ExecutorService pool;


    public ThreadPool(Context context, String codigo, Menu menu) {
        pool = new ThreadPoolExecutor(
                10, 10, 4,
                TimeUnit.SECONDS, queue);
        this.context = context;
        this.codigo=codigo;
        this.menu=menu;
    }


    public void run(){
        MDNS2 mdns2 = new MDNS2(context, handler);
        try {
            while (parar) {
                pool.execute(mdns2);
            }
        }catch (Exception e ){
            e.printStackTrace();
            pool.shutdownNow();
        }finally {
            mdns2.Cerrar();
            pool.shutdownNow();
        }
    }

    public void setParar(boolean parar){
        this.parar=parar;
    }

    public HashMap<String, String> gethCod() {
        return hCod;
    }

    public boolean isEncontrado() {
        return encontrado;
    }

    public String getCodIP() {
        return codIP;
    }
}

