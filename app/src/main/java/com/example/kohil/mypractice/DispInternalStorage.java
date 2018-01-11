package com.example.kohil.mypractice;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DispInternalStorage extends AppCompatActivity {

    Button load, previous;
    TextView mUsername, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disp_internal_storage);

        load = (Button) findViewById(R.id.load);
        previous = (Button) findViewById(R.id.previous);
        mUsername  =(TextView) findViewById(R.id.editText3);
        mPassword  =(TextView) findViewById(R.id.editText4);

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int read = -1;
                StringBuffer stringBuffer = new StringBuffer();

                try {
                    FileInputStream fileInputStream = openFileInput("Aalap.txt");
                    while((read=fileInputStream.read())!=-1){
                        stringBuffer.append((char)read);
                    }

                    String text1 = stringBuffer.substring(0, stringBuffer.indexOf(" "));
                    String text2 = stringBuffer.substring(stringBuffer.indexOf(" ")+1);

                    mUsername.setText(text1);
                    mPassword.setText(text2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DispInternalStorage.this, MainActivity.class));
            }
        });
    }
}
