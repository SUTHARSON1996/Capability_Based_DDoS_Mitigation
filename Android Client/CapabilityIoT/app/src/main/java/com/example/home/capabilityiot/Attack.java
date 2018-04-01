package com.example.home.capabilityiot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SectionIndexer;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Created by home on 20/3/18.
 */

public class Attack extends AppCompatActivity implements Serializable{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attack);
    }


    public void sendAttackPacket(View v) throws InterruptedException {
        EditText msgNum = (EditText)findViewById(R.id.noOfPackets);
        int n = Integer.parseInt(msgNum.getText().toString());
        Toast.makeText(this,"Initiating Attack",Toast.LENGTH_LONG).show();
        for(int i=0;i<n;i++){
            releaseMessage();
            Thread.sleep(1000);
            Toast.makeText(this,"Packet no "+i,Toast.LENGTH_SHORT).show();

        }

        Toast.makeText(this,"Attack complete",Toast.LENGTH_LONG).show();
    }



    public void releaseMessage(){
        IP_Packet ip_packet = new IP_Packet("192.117.24.37","142.161.38.46",new TCP_Segment("attack"));
        final Ether_Frame_IOT e = new Ether_Frame_IOT("48:9d:24:c9:df:a5","a2:31:48:b3:2c:a1", ip_packet);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket clientSocket = new Socket("10.3.141.1",4001);
                    OutputStream out = clientSocket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.writeBytes("192.117.24.37 142.161.38.46 48:9d:24:c9:df:a5 a2:31:48:b3:2c:a1 attack");
                    dos.close();
                    out.close();
                    clientSocket.close();
                    Log.d("mytest","success");

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();

    }

}
