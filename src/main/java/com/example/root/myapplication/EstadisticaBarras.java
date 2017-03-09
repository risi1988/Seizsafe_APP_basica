package com.example.root.myapplication;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.root.Seizsafe.Series;
import com.example.root.Seizsafe.Utiles;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EstadisticaBarras extends AppCompatActivity {

    private List<Series> ls;
    private BarChart chart;
    private ProgressBar proces;
    private Utiles uth;

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ls = (List<Series>) msg.obj;
            cargarGrafico();
            proces.setVisibility(View.GONE);
            chart.setVisibility(View.VISIBLE);
        }
    };

    private void cargarGrafico() {
        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("Grafico de Barras");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistica_barras);
        chart = (BarChart) findViewById(R.id.barchar1);
        proces = (ProgressBar) findViewById(R.id.progressBar4);
        uth = new Utiles(handler);
        uth.iniciarServicioLeerEstadisticasMovimiento("","");

    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        for (int i=0; i<ls.size();i++){
            for (int j=0; j<ls.get(i).getData().size();j++){
                BarEntry be = new BarEntry(Float.parseFloat(ls.get(i).getData().get(j).getValor()),j);
                valueSet1.add(be);
            }
        }



        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
        barDataSet1.setColor(Color.rgb(0, 155, 0));

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        for (int i=0; i<ls.size();i++){
            for (int j=0; j<ls.get(i).getData().size();j++){
                xAxis.add(new Date(Long.parseLong(ls.get(i).getData().get(j).getFecha())).toString());
            }
        }
        return xAxis;
    }

}
