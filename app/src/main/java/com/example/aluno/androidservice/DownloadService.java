package com.example.aluno.androidservice;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Aluno on 21/12/2016.
 */
public class DownloadService extends IntentService {

    private static final String TAG = "DownloadService";

    public static final String URL_PATH = "urlpath";
    public static final String FILENAME = "filename";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "DownloadService onHandleIntent");

        boolean success = false;
        String urlPath = intent.getStringExtra(URL_PATH);
        String fileName = intent.getStringExtra(FILENAME);
        String contentType = "";

        File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        InputStream stream = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            contentType = connection.getContentType();

            stream = new BufferedInputStream(connection.getInputStream());
            fos = new FileOutputStream(output.getPath());

            Log.i(TAG, "DownloadService downloading...");
            int next;
            byte[] buffer = new byte[512];
            while ((next = stream.read(buffer, 0, 512)) != -1) {
                fos.write(buffer, 0, next);
            }
            success = true;
            Log.i(TAG, "DownloadService finished downloading.");
        } catch (MalformedURLException e) {
            Log.i(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.i(TAG, e.getMessage(), e);
        } finally {
            closeResources(fos, stream);
        }

        publishResults(fileName, output.getAbsolutePath(), contentType, success);
    }

    private void closeResources(FileOutputStream outputStream, InputStream inputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            Log.i(TAG, e.getMessage(), e);
        }
    }

    private void publishResults(String fileName, String filePath, String contentType, boolean success) {
        Intent newIntent;
        String msg;

        if (success) {
            Uri uri = Uri.parse("file://" + filePath);
            newIntent = new Intent(Intent.ACTION_VIEW);
            newIntent.setDataAndType(uri, contentType);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            msg = "Completed: " + fileName;
        } else {
            newIntent = new Intent(this, MainActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            msg = "Failed: " + filePath;
        }
        showNotification(newIntent, msg);
    }

    private void showNotification(Intent intent, String msg) {

    }
}
