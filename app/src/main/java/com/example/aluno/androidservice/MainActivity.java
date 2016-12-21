package com.example.aluno.androidservice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 5;
    private EditText editText;
    private Boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.edit_url);
    }

    public void onClickDownload(View view) {
        requestPermission();

        if (permissionGranted) {
            String url = editText.getText().toString();
            String fileName = url.substring(url.lastIndexOf("/") + 1);

            Intent intent = new Intent(this, DownloadService.class);
            intent.putExtra(DownloadService.URL_PATH, url);
            intent.putExtra(DownloadService.FILENAME, fileName);
            startService(intent);
            Toast.makeText(this, "Download started.", Toast.LENGTH_LONG).show();
            this.finish();
        } else {
            Toast.makeText(this, "Permissão não garantida.", Toast.LENGTH_LONG).show();
        }

    }

    private void requestPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED;

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = Boolean.TRUE;
                } else {
                    permissionGranted = Boolean.FALSE;
                }
        }
    }
}
