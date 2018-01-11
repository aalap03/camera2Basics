package com.example.kohil.mypractice;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CacheStorage extends AppCompatActivity {

    EditText mEditText;
    Button internalCache, externalCache, externalPrivate, externalPublic, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_storage);

        internalCache = (Button) findViewById(R.id.iCache);
        externalCache = (Button) findViewById(R.id.eCache);
        externalPrivate = (Button) findViewById(R.id.ePrivate);
        externalPublic = (Button) findViewById(R.id.ePublic);
        next = (Button) findViewById(R.id.next);
        mEditText = (EditText) findViewById(R.id.editText);
    }

    public void iCache(View view){
        String data = mEditText.getText().toString();
        File folder = getCacheDir();
        File myFile = new File(folder, "icache.txt");
        writeData(myFile, data);


    }

//    public void eCache(View view){
//        String data = mEditText.getText().toString();
//        File folder = getExternalCacheDir();
//        File myFile = new File(folder, "ecache.txt");
//        writeData(myFile, data);
//    }

    public void ePrivate(View view){
        String data = mEditText.getText().toString();
        File folder = getExternalFilesDir("Cache Example");
        File myFile = new File(folder, "eprivate.txt");
        writeData(myFile, data);
    }

    public void ePublic(View view){
        String data = mEditText.getText().toString();
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File myFile = new File(folder, "epublic.txt");
        writeData(myFile, data);
    }

    public void next(View view){

    }

    private void writeData(File myFile, String data) {

        FileOutputStream fos =null;

        try {
            fos = new FileOutputStream(myFile);
            fos.write(data.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
