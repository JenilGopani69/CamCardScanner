package com.scanner.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.scanner.demo.Adapter.Card;
import com.scanner.demo.Adapter.dataAdapter;
import com.scanner.demo.Hepler.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap = null;
    FloatingActionButton fabbutton;
    int REQUEST_CODE = 99;
    int preference = ScanConstants.OPEN_CAMERA;
    private static final int REQUEST_CAMERA = 0;
    private DatabaseHelper db;
    private dataAdapter data;
    private  ListView lv;
    private Card dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate database handler
        db=new DatabaseHelper(this);
        lv = (ListView) findViewById(R.id.listView1);

        fabbutton = (FloatingActionButton) findViewById(R.id.fabcamara);

        fabbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                /*if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermission();
                } else {
                    openCamera();
                }*/
            }
        });

    }

    @Override
    protected void onResume() {

        ShowRecords();

        super.onResume();
    }

    private void ShowRecords(){
        final ArrayList<Card> contacts = new ArrayList<>(db.getAllContacts());
        data=new dataAdapter(this, contacts);

        lv.setAdapter(data);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dataModel = contacts.get(position);

                Toast.makeText(getApplicationContext(),String.valueOf(dataModel.getID()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }


    private void openCamera() {
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                //Intent intent = new Intent(MainActivity.this, Contact.class);
                //intent.putExtra("BitmapImage", bitmap);
                //startActivity(intent);

                Intent i = new Intent(this, Contact.class);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
                i.putExtra("byteArray", bs.toByteArray());
                startActivity(i);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}