package com.example.root.Seizsafe;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.root.myapplication.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Estadisticas_prueba extends AppCompatActivity {
    private LinearLayout mainLayout;
    private LineChart mChart;
    private ProgressBar proces;

    private Utiles uth= null;
    private List<Series> ls ;
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ls = (List<Series>) msg.obj;
            cargarGrafico();
            proces.setVisibility(View.GONE);
            mChart.setVisibility(View.VISIBLE);
        }
    };

    private void cargarGrafico() {
        mChart = (LineChart) findViewById(R.id.chart1);

        setData();
        mChart.setDescription("");
        mChart.setNoDataText("Cargando");
        mChart.setNoDataTextDescription("La descripcion más precisa");

        mChart.setHighlightPerDragEnabled(true);

        mChart.setTouchEnabled(true);

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);//Puede escalarse sobre un eje x o y

        mChart.setDrawGridBackground(false);

        mChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);

        mChart.setBackgroundColor(Color.BLUE);

        LineData data = new LineData();
        data.setValueTextColor(Color.RED);


        Legend l = mChart.getLegend(); //Esto es para crear la leyenda, luego puede que lo quite
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis(); //Eje x
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);

        YAxis yl = mChart.getAxisLeft();// Eje y Izquierdo
        yl.setTextColor(Color.WHITE);
        yl.setDrawGridLines(true);

        YAxis yr = mChart.getAxisRight();
        yr.setEnabled(false);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas_prueba);
        mainLayout = (LinearLayout) findViewById(R.id.relativeG);
        uth = new Utiles(handler);
        uth.iniciarServicioLeerEstadisticasMovimiento("", "");
        proces = (ProgressBar) findViewById(R.id.progressBar3);


    }

    private void setData() {

        ArrayList<Entry> xVals = new ArrayList<>();
        ArrayList<String> yVals1 = new ArrayList<>();

        for (int i=0; i<ls.size();i++){
            for (int j=0; j<ls.get(i).getData().size();j++){
                xVals.add(new Entry(Float.parseFloat(ls.get(i).getData().get(j).getValor()),j));
                yVals1.add(new Date(Long.parseLong(ls.get(i).getData().get(j).getFecha())).toString());
            }
        }
      // create a dataset and give it a type

        LineDataSet set1 = new LineDataSet(xVals, "MG");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(yVals1, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (String key : Menu.hAtaque.keySet()) {
            if (Menu.hAtaque.get(key)){
                Intent i = new Intent(Estadisticas_prueba.this, Reproductor_Stream.class);
                i.putExtra("ip",key);
                startActivityForResult(i,1);
            }
        }
    }
    
}
