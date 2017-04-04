package com.example.zs.ipc.service;

import android.app.Service;
import android.content.Intent;
import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyService extends Service {

    private final static String addrss = "transmit";
    private final static int BUFFER_SEZE = 32*8*1024;
    private boolean flag = false;
    private final static String TAG = "MyService";
    private final static String IMG_8M = "/IMG8M.jpg";
    private final static int DESTROY = 10;
    private Thread workThread;
    private boolean runnableExistence;
    private boolean excutorWorking;
    ExecutorService excutor = Executors.newSingleThreadExecutor();

    private ServerThread serverThread;

    public MyService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        runnableExistence = true;
        excutorWorking = true;
        if(null == serverThread) {
            runnableExistence = false;
            Log.i(TAG,"serverThread null");
            serverThread = new ServerThread();
        }

        if(excutor.isShutdown()) {
            excutorWorking = false;
            Log.i(TAG,"excutor is shutdown");
            excutor = Executors.newSingleThreadExecutor();
        }


        if(!excutorWorking || !runnableExistence) {
            flag = true;
            excutor.submit(serverThread);
        }

        return new MyBinder();
    }

    @Override
    public void onDestroy() {

        if(null != excutor) {
            excutor.shutdownNow();
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        flag = false;
        Log.i(TAG,"onUnbind()");
        return super.onUnbind(intent);
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
            } catch (IOException e) {
                e.getMessage();
                return;

            }
            Log.i(TAG,"obtan server");
                while (flag) {
                    try {
                        connect = server.accept();
                    } catch (IOException e) {
                        try {
                            server = new LocalServerSocket(addrss);
                            continue;
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        e.getMessage();
                    }
                    try {
                    Log.i(TAG, "enter while");

                    connect.setReceiveBufferSize(500000);
                    connect.setSendBufferSize(500000);
                    Log.i(TAG,"accept sucseed");
                    Credentials cre = connect.getPeerCredentials();
                    Log.i(TAG,"accept socket uid:"+cre.getUid());
                    byte[] buffer = new byte[BUFFER_SEZE];
                    File filesDir = getFilesDir();
                    File path = new File(filesDir,IMG_8M);
                    Log.i(TAG,"path is " + path.getAbsolutePath());
                    in = new BufferedInputStream(new FileInputStream(path));
                    os = new BufferedOutputStream(connect.getOutputStream());
                    int len;
                    while((len = in.read(buffer)) != -1) {
                     os.write(buffer,0,len);
                    }
                    os.flush();
                    Log.d(TAG,"send allow");
                } catch (IOException e) {
                        e.getMessage();
                    } finally{

                            Util.closeQuietly(in);
                            Util.closeQuietly(os);
                            Util.closeQuietly(connect);
                        try {
                            server.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    }
            }
        }

    class MyBinder extends Binder{

    }
}
