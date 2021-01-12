package com.example.locater;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void gotoMap(View view) {
        startActivity(new Intent(Home.this,MapsActivity.class));
    }

    public void gotogefence(View view) {
        startActivity(new Intent(Home.this,settingGeoFence.class));
    }

    public void gotoMain(View view) {
        startActivity(new Intent(Home.this,Main2Activity.class));

    }
}
