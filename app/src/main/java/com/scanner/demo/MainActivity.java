package com.scanner.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Bitmap bitmap = null;
    FloatingActionButton fabbutton;
    int REQUEST_CODE = 99;
    int preference = ScanConstants.OPEN_CAMERA;
    private static final int REQUEST_CAMERA = 0;

    SQLiteHelper SQLITEHELPER;
    SQLiteDatabase SQLITEDATABASE;
    Cursor cursor;
    SQLiteListAdapter ListAdapter ;

    ArrayList<String> ID_ArrayList = new ArrayList<String>();
    ArrayList<String> NAME_ArrayList = new ArrayList<String>();
    ArrayList<String> COMPANY_ArrayList = new ArrayList<String>();
    ArrayList<String> TITLE_ArrayList = new ArrayList<String>();
    ArrayList<String> MOBILE_ArrayList = new ArrayList<String>();
    ArrayList<String> LANDLINE_ArrayList = new ArrayList<String>();
    ArrayList<String> EMAIL_ArrayList = new ArrayList<String>();
    ArrayList<String> WEBPAGE_ArrayList = new ArrayList<String>();
    ArrayList<String> ADDRESS_ArrayList = new ArrayList<String>();
    ListView LISTVIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabbutton = (FloatingActionButton) findViewById(R.id.fabcamara);

        LISTVIEW = (ListView) findViewById(R.id.listView1);

        SQLITEHELPER = new SQLiteHelper(this);

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

        ShowSQLiteDBdata() ;

        super.onResume();
    }

    private void ShowSQLiteDBdata() {

        SQLITEDATABASE = SQLITEHELPER.getWritableDatabase();

        cursor = SQLITEDATABASE.rawQuery("SELECT * FROM demoA", null);

        ID_ArrayList.clear();
        NAME_ArrayList.clear();
        COMPANY_ArrayList.clear();
        TITLE_ArrayList.clear();
        MOBILE_ArrayList.clear();
        LANDLINE_ArrayList.clear();
        EMAIL_ArrayList.clear();
        WEBPAGE_ArrayList.clear();
        ADDRESS_ArrayList.clear();

        if (cursor.moveToFirst()) {
            do {
                ID_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_ID)));

                NAME_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Name)));

                COMPANY_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Company)));

                TITLE_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Title)));

                MOBILE_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Mobile)));

                LANDLINE_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Landline)));

                EMAIL_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Email)));

                WEBPAGE_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Webpage)));

                ADDRESS_ArrayList.add(cursor.getString(cursor.getColumnIndex(SQLiteHelper.KEY_Address)));

            } while (cursor.moveToNext());
        }

        ListAdapter = new SQLiteListAdapter(MainActivity.this,

                ID_ArrayList,
                NAME_ArrayList,
                COMPANY_ArrayList,
                TITLE_ArrayList,
                MOBILE_ArrayList,
                LANDLINE_ArrayList,
                EMAIL_ArrayList,
                WEBPAGE_ArrayList,
                ADDRESS_ArrayList

        );

        LISTVIEW.setAdapter(ListAdapter);

        cursor.close();
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