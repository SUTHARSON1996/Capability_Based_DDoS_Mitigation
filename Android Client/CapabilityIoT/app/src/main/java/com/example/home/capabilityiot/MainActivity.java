package com.example.home.capabilityiot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void sendLegitimateTraffic(View v){
        Intent i = new Intent(this,Legitimate.class);
        startActivity(i);

    }

    public void attackServer(View v){
        Intent i = new Intent(this,Attack.class);
        startActivity(i);
    }
}
