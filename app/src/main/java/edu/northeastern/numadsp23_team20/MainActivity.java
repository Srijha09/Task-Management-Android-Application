package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onAtYourServiceButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, AtYourService.class);
        this.startActivity(intent);
    }
}