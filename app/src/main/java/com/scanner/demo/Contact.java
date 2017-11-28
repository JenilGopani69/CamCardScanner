package com.scanner.demo;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class Contact extends ActionBarActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        tv = (TextView)findViewById(R.id.scantext);
        Intent intent =  getIntent();
        Bundle bundle  = intent.getExtras();
        String text = (String)bundle.get("scanText");
        tv.setText(text);



    }
}
