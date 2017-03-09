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
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LineBarra extends AppCompatActivity {
    private ProgressBar proces;
    private Utiles uth;
    private List<Series> ls;
    private String[] mMonths;
    private int max=0;
    private int ejemax = 0;// Sabemos cual es el grafico que tiene el eje mas largo

    private CombinedChart mChart;
    private final int itemcount = 12;

    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ls = (List<Series>) msg.obj;
            for (int i = 0; i<ls.size();i++){
                if (max<ls.get(i).getData().size()){
                    if (ls.get(i).getType().equals("column")) {
                        max = ls.get(i).getData().size();
                        ejemax = i;
                    }
                }
            }
            cargarGrafico();
            proces.setVisibility(View.GONE);
            mChart.setVisibility(View.VISIBLE);
        }
    };

    private void cargarEjex() {
        for (int j=0; j<ls.get(ejemax).getData().size();j++){
            if (ls.get(ejemax).getData().get(j).getFecha()!=null) {
                Date d = new Date(Long.parseLong(ls.get(ejemax).getData().get(j).getFecha()));
                mMonths[j] = d.toString();
            }
        }
    }

    private void cargarGrafico() {
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);

        // draw bars behind lines
        mChart.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        mMonths = new String[max];
        cargarEjex();
        CombinedData data = new CombinedData(mMonths);


        BarData bar;
        bar = generateBarData(1);
        /*
        ArrayList<BarDataSet> dataSet = new ArrayList<>();
        for (int i =0; i<ls.size();i++){
            if (ls.get(i).getType().equals("column")){
                dataSet.add(generateBarData(i).getDataSetByIndex(0));
            }else if (ls.get(i).getType().equals("spline")){
                data.setData(generateLineData(i));
            }
        }
        System.out.println("Tamaño: " + mMonths.length);
        BarData data2 = new BarData(mMonths,dataSet);
        data.setData(data2);*/
        data.setData(bar);


        //data.setData(generateLineData());
        //data.setData(generateBarData());
        //data.setData(generateBubbleData());
        //data.setData(generateScatterData());
        //data.setData(generateCandleData());

        mChart.setData(data);
        mChart.invalidate();
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_barra);
        uth = new Utiles(handler);
        uth.iniciarServicioLeerEstadisticasMovimiento("", "");
        proces = (ProgressBar) findViewById(R.id.progressBar5);
        mChart = (CombinedChart) findViewById(R.id.chart11);

    }

    private LineData generateLineData(int serie) {

        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<Entry>();
        for (int j=0; j<ls.get(serie).getData().size();j++) {
            entries.add(new Entry(Float.parseFloat(ls.get(serie).getData().get(j).getValor()), j));
        }
        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleSize(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(true);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData(int serie) {

        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for(int j=0; j < ls.get(serie).getData().size();j++){
            entries.add(new BarEntry(Float.parseFloat(ls.get(serie).getData().get(j).getValor()), j));
        }

        BarDataSet set = new BarDataSet(entries, "Bar DataSet");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    private BarDataSet generateBarData2(int serie) {


        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for(int j=0; j < ls.get(serie).getData().size();j++){
            entries.add(new BarEntry(Float.parseFloat(ls.get(serie).getData().get(j).getValor()), j));
        }

        BarDataSet set = new BarDataSet(entries, "Bar DataSet");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);

        return set;
    }


    protected ScatterData generateScatterData() {

        ScatterData d = new ScatterData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(getRandom(20, 15), index));

        ScatterDataSet set = new ScatterDataSet(entries, "Scatter DataSet");
        set.setColor(Color.GREEN);
        set.setScatterShapeSize(7.5f);
        set.setDrawValues(false);
        set.setValueTextSize(10f);
        d.addDataSet(set);

        return d;
    }

    protected CandleData generateCandleData() {

        CandleData d = new CandleData();

        ArrayList<CandleEntry> entries = new ArrayList<CandleEntry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new CandleEntry(index, 20f, 10f, 13f, 17f));

        CandleDataSet set = new CandleDataSet(entries, "Candle DataSet");
        set.setColor(Color.rgb(80, 80, 80));
        set.setBodySpace(0.3f);
        set.setValueTextSize(10f);
        set.setDrawValues(false);
        d.addDataSet(set);

        return d;
    }

    protected BubbleData generateBubbleData() {

        BubbleData bd = new BubbleData();

        ArrayList<BubbleEntry> entries = new ArrayList<BubbleEntry>();

        for (int i =0;i<ls.size();i++){
            for(int j=0; j < ls.get(i).getData().size()-2000;j++){
                entries.add(new BubbleEntry(j,Float.parseFloat(ls.get(i).getData().get(j).getValor()),Float.parseFloat(ls.get(i).getData().get(j).getValor())));
            }
        }


        BubbleDataSet set = new BubbleDataSet(entries, "Bubble DataSet");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.WHITE);
        set.setHighlightCircleWidth(1.5f);
        set.setDrawValues(true);
        bd.addDataSet(set);

        return bd;
    }

    private float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }


}


