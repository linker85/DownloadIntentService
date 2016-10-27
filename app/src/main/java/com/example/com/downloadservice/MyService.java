package com.example.com.downloadservice;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class MyService extends IntentService {


    private int result = Activity.RESULT_CANCELED;
    public static final String URL          = "urlpath";
    public static final String FILENAME     = "filename";
    public static final String FILEPATH     = "filepath";
    public static final String RESULT       = "result";
    public static final String NOTIFICATION = "notification";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyService(String name) {
        super(name);
    }

    public MyService() {
        super("DownloadService");
    }

    // Called async by android
    @Override
    protected void onHandleIntent(Intent intent) {
        String urlPath = intent.getStringExtra(URL);
        String fileName = intent.getStringExtra(FILENAME);
        int result = Activity.RESULT_CANCELED;

        try {
            URL url = new URL(urlPath);
            InputStream input = url.openStream();
            //The sdcard directory e.g. '/sdcard' can be used directly, or
            //more safely abstracted with getExternalStorageDirectory()
            File storagePath = new File(Environment.getExternalStorageDirectory() + "/Pictures");
            OutputStream output = new FileOutputStream(new File(storagePath, fileName));
            try {
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                }
                result = Activity.RESULT_OK;
            } finally {
                output.close();
                input.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        publishResults(urlPath, result);
    }

    // Return result through broadcast
    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(FILEPATH, outputPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

}
