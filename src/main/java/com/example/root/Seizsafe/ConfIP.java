package com.example.root.Seizsafe;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by jonatan.Gonzalez on 14/12/2015.
 */
public class ConfIP {
    private String address;
    private String netmask;
    private String gateway;

    public ConfIP(String address, String gatewaty, String netmask) {
        this.address = address;
        this.gateway = gatewaty;
        this.netmask = netmask;
    }

    public ConfIP(JSONObject jsonObject) throws JSONException, ParseException {
        this.address = jsonObject.getString("address");
        this.gateway = jsonObject.getString("gateway");
        this.netmask = jsonObject.getString("netmask");
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGatewaty() {
        return gateway;
    }

    public void setGatewaty(String gatewaty) {
        this.gateway = gatewaty;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    @Override
    public String toString() {
        return "ConfIP{" +
                "address='" + address + '\'' +
                ", netmask='" + netmask + '\'' +
                ", gateway='" + gateway + '\'' +
                '}';
    }
}
