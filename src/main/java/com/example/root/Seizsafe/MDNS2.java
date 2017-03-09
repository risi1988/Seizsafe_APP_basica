package com.example.root.Seizsafe;

/**
 * Created by jonatan.Gonzalez on 15/04/2016.
 */
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class MDNS2  extends Thread{

    private Context Context;

    private WifiManager.MulticastLock lock;
    private static final String DNS_TYPE = "_http._tcp.local.";
    private static final String DNS_NAME = "Solarfighter Gateway";
    private JmDNS jmdns = null;
    private ServiceListener listener;

    // Bonjour JellyBean 4.1.XX a 6.X
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager mNsdManager;
    private Handler handler;

    public MDNS2(Context context, Handler handler) {
        this.Context=context;
        this.handler= handler;
        setUpDiscoInfo();
    }

    @Override
    public void run() {
        super.run();
        setUpDiscoInfoPreJellyBean();
    }

    private void setUpDiscoInfo() {
        Log.d("DNS", "Setting up Bonjour");
        discoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d("DNS","Service resolved: " + serviceInfo.getServiceName() + " host:" + serviceInfo.getHost() + " port:"
                        + serviceInfo.getPort() + " type:" + serviceInfo.getServiceType()+ ". IP: " + serviceInfo.getHost().getHostAddress());
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                // TODO Auto-generated method stub

            }
        };
        mNsdManager = (NsdManager) Context.getSystemService(Context.NSD_SERVICE);
        mNsdManager.discoverServices(DNS_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    private void setUpDiscoInfoPreJellyBean() {
        Log.d("DNS", "Setting up Bonjour Pre-JellyBean");
        try {
            listener = new ServiceListener() {

                @Override
                public void serviceResolved(ServiceEvent event) {
                    Log.d("DNS", "Resolved event " + event.getName() + " " + event.getType() + ". IP: " + event.getInfo().getInet4Addresses()[0]);
                    handler.obtainMessage(1, event.getName() + "" + event.getInfo().getInet4Addresses()[0]).sendToTarget();

                }

                @Override
                public void serviceRemoved(ServiceEvent event) {
                    // TODO Auto-generated method stub.

                }

                @Override
                public void serviceAdded(ServiceEvent event) {
                    // TODO Auto-generated method stub

                }
            };

        }catch (Exception e){
            e.printStackTrace();
            Cerrar();
        }

        WifiManager wifi = (WifiManager) Context.getSystemService(Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("SolarFighterBonjour");
        lock.setReferenceCounted(true);
        lock.acquire();
        try {
            int intaddr = wifi.getConnectionInfo().getIpAddress();

            byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff),
                    (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff) };
            InetAddress addr = InetAddress.getByAddress(byteaddr); // Need to
            // process
            // UnknownHostException

            jmdns = JmDNS.create(addr);
            jmdns.addServiceListener(DNS_TYPE, listener);
        } catch (IOException e) {
            e.printStackTrace();
            Cerrar();
        }
    }

    public void Cerrar() {

        if (jmdns != null) {
            try {
                jmdns.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (lock != null) {
            Log.d("MDNS", "Estoy cerrando el look");
            lock.release();
        }
    }

    protected void onStop() {

        if (jmdns != null) {
            jmdns.removeServiceListener(DNS_TYPE, listener);
        }

        if(mNsdManager!=null){
            mNsdManager.stopServiceDiscovery(discoveryListener);
        }

    }
}
