package com.example.zs.service;

import android.app.Service;
import android.content.Intent;
import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import static android.content.ContentValues.TAG;

public class MyService extends Service {

    private final static String addrss = "com.repackaging.localsocket";
    private final static int BUFFER_SEZE = 32*8*1024;
    private boolean flag = false;
    private final static String TAG = "MyService";
    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        flag = true;
        new Thread(new ServerThread()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        flag = false;
        super.onDestroy();

    }

    class ServerThread implements Runnable {

        @Override
        public void run() {
            LocalServerSocket server = null;
            InputStream in = null;
            OutputStream os = null;
            LocalSocket connect = null;
            try {
                server = new LocalServerSocket(addrss);
                while (flag) {
                    connect = server.accept();
                    Credentials cre = connect.getPeerCredentials();
                    Log.i(TAG,"accept socket uid:"+cre.getUid());
                    connect.setReceiveBufferSize(500000);
                    connect.setSendBufferSize(500000);
                    byte[] buffer = new byte[BUFFER_SEZE];
                    File filesDir = getFilesDir();
                    File path = new File(filesDir,"/IMG8M.jpg");
                    in = new BufferedInputStream(new FileInputStream(path));
                    os = new BufferedOutputStream(connect.getOutputStream());
                    int len;
                    while((len = in.read(buffer)) != -1) {
                     os.write(buffer,0,len);
                    }
                    os.flush();
                    Log.d(TAG,"send allow");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                try {
                    Util.closeQuietly(in);
                    Util.closeQuietly(os);
                    Util.closeQuietly(connect);
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
