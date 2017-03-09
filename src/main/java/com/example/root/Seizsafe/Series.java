package com.example.root.Seizsafe;

import java.util.List;

/**
 * Created by jonatan.Gonzalez on 21/01/2016.
 */
public class Series {
    private String name;
    private String yaxis;
    private List<Dato> data;
    private String type;

    public Series(List<Dato> data, String name, String yaxis, String type) {
        this.data = data;
        this.name = name;
        this.yaxis = yaxis;
        this.type = type;
    }


    public List<Dato> getData() {
        return data;
    }

    public void setData(List<Dato> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYaxis() {
        return yaxis;
    }

    public void setYaxis(String yaxis) {
        this.yaxis = yaxis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Series{" +
                "data=" + data +
                ", name='" + name + '\'' +
                ", yaxis='" + yaxis + '\'' +
                '}';
    }
}
