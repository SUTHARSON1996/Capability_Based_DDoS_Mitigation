package com.example.home.capabilityiot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
/**
 * Created by home on 20/3/18.
 */

public class Legitimate extends AppCompatActivity implements Serializable{
    static String temp="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.legitimate);
    }


    public void sendPacket(View view){
        EditText msg = (EditText)findViewById(R.id.message);
        final String message = msg.getText().toString();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket clientSocket = new Socket("10.3.141.1",4001);
                    OutputStream out = clientSocket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.writeBytes("192.117.24.37 142.161.38.46 48:9d:24:c9:df:a5 a2:31:48:b3:2c:a1 "+message);
                    dos.close();
                    out.close();
                    clientSocket.close();
                    Legitimate.temp="success";
                    Log.d("mytest","success");

                } catch (IOException e) {
                    Legitimate.temp = e.getMessage().toString();
                }
            }
        }).start();
        while(Legitimate.temp.equals("")){

        }
        Toast.makeText(this," "+Legitimate.temp,Toast.LENGTH_LONG).show();
    }

}
