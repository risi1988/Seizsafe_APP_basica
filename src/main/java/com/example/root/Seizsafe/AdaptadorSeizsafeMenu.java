package com.example.root.Seizsafe;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.root.myapplication.R;

import java.util.List;

/**
 * Created by jonatan.Gonzalez on 03/03/2016.
 */
public class AdaptadorSeizsafeMenu extends ArrayAdapter<ListSeizsafeMenu> {
    private List<ListSeizsafeMenu> lSeizsafeMenus;
    private Context context;
    private ServicioWifi s;
    private Menu menu;
    private Utiles ut;
    private boolean enlazado;

    LayoutInflater inflater = LayoutInflater.from(getContext());
    View item = inflater.inflate(R.layout.textcenter, null);

    final TextView txtEnlazado = (TextView) item.findViewById(R.id.txtEnlazado);
    final TextView txtEstado_2 = (TextView) item.findViewById(R.id.txtEstado_2);
    final TextView txtSensores = (TextView) item.findViewById(R.id.txtSensores);
    final ImageView image_1 =(ImageView) item.findViewById(R.id.imageMenu_1);
    final ImageView image_2 =(ImageView) item.findViewById(R.id.imageMenu_2);
    TextView txtdispositivo = (TextView) item.findViewById(R.id.txtDipositivo);


    public AdaptadorSeizsafeMenu(Context context, ServicioWifi s, List<ListSeizsafeMenu> lista, Menu menu){
        super(context,R.layout.textcenter,lista);
        this.lSeizsafeMenus= lista;
        this.s = s;
        this.menu= menu;
        ut = new Utiles();
        this.context=context;
        enlazado = true;

    }

    public AdaptadorSeizsafeMenu(Context context, boolean enlazado, List<ListSeizsafeMenu> lista, Menu menu){
        super(context,R.layout.textcenter,lista);
        this.context=context;
        this.enlazado=enlazado;
        this.menu=menu;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        String cadenaSensores = "";
        image_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Apagar todo el sistema
                if (enlazado) {
                } else {
                 //   image_1.setImageResource(R.drawable.recargar_gris);
                    menu.mensajeIp();
                }
            }
        });

        if (enlazado) {
            image_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!menu.parar) {
                        if (lSeizsafeMenus.get(position).isEstado()) {
                            //apagar camara y sistema de alertas
                            ut.iniciarServicioGuardarGrabacion(false);
                            txtEstado_2.setText(v.getContext().getString(R.string.Monitorifando) + " : " + v.getContext().getString(R.string.No));
                            image_2.setImageResource(R.drawable.onoff);
                            s.pararhilo(lSeizsafeMenus.get(position).getIp());
                            menu.parar = true;
                            menu.sesion = false;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                        menu.parar = false;
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();

                        } else {
                            //encender camara y sistema de alertas
                            s.pararhilo(lSeizsafeMenus.get(position).getIp());
                            ut.iniciarServicioGuardarGrabacion(true);
                            menu.starServicio(lSeizsafeMenus.get(position).getIp());
                            image_2.setImageResource(R.drawable.onoff_gris);
                            menu.parar = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                        menu.parar = false;
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                }
            });
        }

        if (enlazado){
            txtdispositivo.setText(lSeizsafeMenus.get(position).getCodigo()+" "+ lSeizsafeMenus.get(position).getDispositivo());
            txtEnlazado.setText(this.getContext().getString(R.string.Vinculado)+" : "+ lSeizsafeMenus.get(position).getIp());
            txtEstado_2.setVisibility(View.VISIBLE);
            image_2.setVisibility(View.VISIBLE);
            image_1.setVisibility(View.GONE);
            if (lSeizsafeMenus.get(position).isEstado()) {
                txtEstado_2.setText(this.getContext().getString(R.string.Monitorifando) + " : " + this.getContext().getString(R.string.Si));
                image_2.setImageResource(R.drawable.onoff_gris);
            }else{
                txtEstado_2.setText(this.getContext().getString(R.string.Monitorifando)+" : " + this.getContext().getString(R.string.No));
                //image_2.setImageResource(R.drawable.onoff);
            }
            if (lSeizsafeMenus.get(position).getlSensor()!=null){
                for (int i =0; i<lSeizsafeMenus.get(position).getlSensor().size();i++){
                    cadenaSensores=cadenaSensores+this.getContext().getString(R.string.Sensor)+" : "+lSeizsafeMenus.get(position).getlSensor().get(i).getTipo()+" "+lSeizsafeMenus.get(position).getlSensor().get(i).getNombre()+"\r\n";
                }
            }
        }else{
            txtEnlazado.setText(this.getContext().getString(R.string.Desvinculado));
            image_1.setImageResource(R.drawable.recargar);
        }
        txtSensores.setText(cadenaSensores);
        return item;
    }


}