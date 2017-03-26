package com.example.zs.callimgapplicaiton;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/26 0026.
 */

public class ClientConnect {
    private static final String TAG = "ClientConnect";
    private static final String name = "com.repackaging.localsocket";
    private LocalSocket Client = null;
    private OutputStream os = null;
    private InputStream is = null;
    private int timeout = 30000;
    private List<Closeable> closeables = new ArrayList<>();

    public void connect(){
        try {
            Client = new LocalSocket();
            Client.connect(new LocalSocketAddress(name));
            Client.setSoTimeout(timeout);
            closeables.add(Client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String[] data) {
        try {
            os = Client.getOutputStream();
            os.flush();
            Log.d(TAG,"send");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            Util.closeQuietly(os);
        }
    }

    public InputStream recv() {
        Log.d(TAG,"recv");
        try {
            is = Client.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            Util.closeQuietly(is);
        }
        return is;
    }


}


