package com.example.zs.ipc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    private File externalStoragePublicDirectory;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.myImg);
        externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //Toast.makeText(this, externalStoragePublicDirectory.getAbsolutePath(),Toast.LENGTH_LONG).show();
        if (!(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            requestCameraPermission();
        }
    }

    private static final int REQUEST_PERMISSION_CAMERA_CODE = 1;
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CAMERA_CODE);
    }

    public void trytry (View view){
        File file = new File(externalStoragePublicDirectory,"/MyPic/IMG8M.jpg");
        Bitmap bit = BitmapFactory.decodeFile(file.getAbsolutePath());
        img.setImageBitmap(bit);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA_CODE) {
            int grantResult = grantResults[0];
            boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
            Log.i("lala", "onRequestPermissionsResult granted=" + granted);
            File file = new File(externalStoragePublicDirectory,"/MyPic/IMG8M.jpg");
            Bitmap bit = BitmapFactory.decodeFile(file.getAbsolutePath());
            img.setImageBitmap(bit);
        }
    }
}
