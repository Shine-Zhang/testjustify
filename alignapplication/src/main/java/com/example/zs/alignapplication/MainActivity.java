package com.example.zs.callimgapplicaiton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView img;

    InputStream result;

    private final static int BUFFER_SEZE = 32*8*1024;

    private final static String ACTION = "com.huawei.sokettransfer";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.socket_img);
    }

    @Override
    protected void onStart() {
        super.onStart();
        intent = new Intent();
        intent.setAction(ACTION);
        intent.setPackage("com.example.zs.ipc");
        startService(intent);
    }

    public void getImg(View view) {
        ClientConnect client = new ClientConnect();
        client.connect();
        result = client.recv();

        byte[] realRestul = getByteArrayResult();

        if(null == realRestul) {
            Toast.makeText(this,"the result size is null !",Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeByteArray(realRestul,0,realRestul.length);
        img.setImageBitmap(bitmap);
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
        if(null != intent) {
            stopService(intent);
        };
    }
}
