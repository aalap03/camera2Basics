package com.example.kohil.mypractice;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kohil.mypractice.MVPLogin_Crap.LoginActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button save, next, cacheExample;
    EditText username, password;


    public int mul(int i, int j){
        return i*j;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        save = (Button) findViewById(R.id.save);
        next = (Button) findViewById(R.id.next);
        cacheExample = (Button) findViewById(R.id.cache);
        username  = (EditText) findViewById(R.id.usn);
        password  = (EditText) findViewById(R.id.psn);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream fileOutputStream = null;
                File file = getFilesDir();
                String myUsername = username.getText().toString() + " ";

                try {
                    fileOutputStream = openFileOutput("Aalap.txt", Context.MODE_PRIVATE);
                    fileOutputStream.write(myUsername.getBytes());
                    fileOutputStream.write(password.getText().toString().getBytes());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                Toast.makeText(MainActivity.this, "Data saved in" + file, Toast.LENGTH_LONG);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DispInternalStorage.class));
            }
        });

        cacheExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}
