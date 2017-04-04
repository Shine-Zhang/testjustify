package com.example.zs.callimgapplicaiton;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView img;

    InputStream result;

    private boolean isBound = false;

    private final static int BUFFER_SEZE = 32*8*1024;

    private final static String ACTION = "com.huawei.sockettransfer";
    private Intent intent;
    private final static int ACQUIRE_IMG_SUCCESS = 11;
    private final static int ACQUIRE_IMG_FAILED = 12;
    private final static String TAG = "TransmitClient";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACQUIRE_IMG_SUCCESS:
                    Log.i(TAG,"ACQUIRE_IMG_SUCCESS");
                    byte[] bytBitmap = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytBitmap,0,bytBitmap.length);
                    img.setImageBitmap(bitmap);
                    break;
                case ACQUIRE_IMG_FAILED:
                    Toast.makeText(MainActivity.this,"the result size is null !",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.socket_img);
    }

    ServiceConnection myConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            Log.i(TAG,"binding success");
            Log.i(TAG,"serviece connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"serviece disconnected");
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        intent = new Intent();
        intent.setAction(ACTION);
        Intent explicitIntent = getExplicitIntent(getApplicationContext(), intent);
        //intent.setPackage("com.example.zs.service");
        if (null != explicitIntent) {
            bindService(explicitIntent,myConn,BIND_AUTO_CREATE);
        }else {
            Toast.makeText(MainActivity.this,"make explicitIntent failed",Toast.LENGTH_SHORT).show();
        }

    }

    public void getImg(View view) {
        ClientConnect client = new ClientConnect();
        client.connect();
        result = client.recv();
        new Thread() {
            @Override
            public void run() {
                super.run();
                byte[] realRestul = getByteArrayResult();
                if(null == realRestul) {
                    mHandler.sendEmptyMessage(ACQUIRE_IMG_FAILED);
                } else {
                    Message message = mHandler.obtainMessage(ACQUIRE_IMG_SUCCESS,realRestul);
                    mHandler.sendMessage(message);
                }

            }
        }.start();


/*        if(null == realRestul) {
            Toast.makeText(this,"the result size is null !",Toast.LENGTH_SHORT).show();
            return;
        }*/
        Log.i(TAG,"get bitMap success!");
/*        Bitmap bitmap = BitmapFactory.decodeStream(result);
        img.setImageBitmap(bitmap);*/
    }

    private byte[] getByteArrayResult() {
        byte[] cache = new byte[BUFFER_SEZE];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        try {
            while((len = result.read(cache)) != -1) {
                out.write(cache,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            Util.closeQuietly(out);
            Util.closeQuietly(result);
        }

        return out.toByteArray();
    }

    @Override
    protected void onStop() {
        super.onStop();

            if(isBound) {
                unbindService(myConn);
            }
        
    }

    public Intent getExplicitIntent(Context context, Intent implicitIntent){
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfos == null || resolveInfos.size()!= 1) {
            Log.i(TAG,"query fail " + resolveInfos.size());
            return null;
        }
        Intent explicitIntent = null;
        ResolveInfo info = resolveInfos.get(0);
        String packageName = info.serviceInfo.packageName;
        String className = info.serviceInfo.name;
        ComponentName component = new ComponentName(packageName,className);
        explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
