package com.example.nitcemagazine.getStarted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nitcemagazine.MainActivityPages.MainActivity2;
import com.example.nitcemagazine.R;


public class getStartedPage extends AppCompatActivity {

    LinearLayout getStartedButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started_page);

        if(restorePrefData())
        {
            Intent intent = new Intent(getStartedPage.this, MainActivity2.class);
            startActivity(intent);
            finish();
        }

        getStartedButton = findViewById(R.id.linearLayoutGetStarted);

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savrPrefsData();
                Intent intent = new Intent(getStartedPage.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref",MODE_PRIVATE);
        return pref.getBoolean("isGetStartedOpened",false);
    }

    private void savrPrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isGetStartedOpened",true);
        editor.commit();
    }
}