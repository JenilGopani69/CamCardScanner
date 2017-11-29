package com.scanner.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    Bitmap bitmap = null;
    private Button cameraButton,scan;
    private ImageView scannedImageView;
    int REQUEST_CODE = 99;
    private TessBaseAPI mTess;
    String datapath = "";
    TextView tv,tvname,tvemail,tvmobile,tvweb;
    int preference = ScanConstants.PICKFILE_REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvemail = (TextView) findViewById(R.id.tvemail);
        tv = (TextView) findViewById(R.id.tv);
        tvmobile = (TextView) findViewById(R.id.tvmobile);
        tvname = (TextView) findViewById(R.id.tvname);
        tvweb = (TextView) findViewById(R.id.tvweb);
        cameraButton = (Button)findViewById(R.id.cameraButton);
        scan = (Button)findViewById(R.id.scan);
        scannedImageView = (ImageView) findViewById(R.id.scannedImage);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });


        String language = "eng";
        datapath = getFilesDir()+ "/tesseract/";
        mTess = new TessBaseAPI();
        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, language);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String OCRresult = null;
                mTess.setImage(bitmap);
                OCRresult = mTess.getUTF8Text();
                extractName(OCRresult);
                extractEmail(OCRresult);
                extractPhone(OCRresult);
                extractWeb(OCRresult);
                OCRresult = OCRresult.replaceAll("[-`$#%~¯‘?<>—“–\\\\{}/_()'!;^|\"]*", "");
                tv.setText(OCRresult);


                /*
                Intent intent = new Intent(getApplicationContext(),Contact.class);
                intent.putExtra("scanText",OCRresult);
                //intent.putExtras("scanImage",scannedImageView.setImageBitmap());
                startActivity(intent);
*/

                //separate into multiple string
                String multiLines = OCRresult;
                String[] line;
                String delimiter = "\n";
                line = multiLines.split(delimiter);

                //total number of line
                //int total = tv.getLineCount();

                /*
                //alert dialog
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("OCR Text");
                alertDialog.setMessage("1st line is : " + line[0] + "\n2nd line is : " + line[1] +"\n3rd line is : " + line[2] + "\n4th line is : " + line[3] + "\nTotal line : " + total);
                alertDialog.setButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {alertDialog.cancel();}
                });
                alertDialog.show();
                */
            }
        });


    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if (dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                scannedImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void extractName(String str){
        final String NAME_REGEX1 = "^([A-Z]([A-Z]*|\\.) *){1,2}([A-Z][A-Z]+-?)+$";
        final String NAME_REGEX2 = "^([A-Z][a-z]*((\\s)))+[A-Z][a-z]*$";
        final String NAME_REGEX3= "^([A-Z]([a-z]*|\\.) *){1,2}([A-Z][a-z]+-?)+$";
        Pattern p1 = Pattern.compile(NAME_REGEX1, Pattern.MULTILINE);
        Pattern p2 = Pattern.compile(NAME_REGEX2, Pattern.MULTILINE);
        Pattern p3 = Pattern.compile(NAME_REGEX3, Pattern.MULTILINE);
        Matcher m1 =  p1.matcher(str);
        Matcher m2 =  p2.matcher(str);
        Matcher m3 =  p3.matcher(str);
        if(m1.find()){System.out.println(m1.group());tvname.setText("Name : "+ m1.group());}
        if(m2.find()){System.out.println(m2.group());tvname.setText("Name : "+ m2.group());}
        if(m3.find()){System.out.println(m3.group());tvname.setText("Name : "+ m3.group());}
    }

    public void extractEmail(String str) {
        final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);
        Matcher m = p.matcher(str);   // get a matcher object
        if(m.find()){
            System.out.println(m.group());
            tvemail.setText("Email : "+m.group());
        }
    }

    public void extractPhone(String str){
        final String PHONE_REGEX1="\\+?\\d[\\d -]{8,12}\\d";
        final String PHONE_REGEX2="(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";
        final String PHONE_REGEX3="^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";

        Pattern p1 = Pattern.compile(PHONE_REGEX1, Pattern.MULTILINE);
        Pattern p2 = Pattern.compile(PHONE_REGEX2, Pattern.MULTILINE);
        Pattern p3 = Pattern.compile(PHONE_REGEX3, Pattern.MULTILINE);

        Matcher m1 = p1.matcher(str);
        Matcher m2 = p2.matcher(str);
        Matcher m3 = p3.matcher(str);// get a matcher object
        if(m1.find()){tvmobile.setText("Mobile : "+ m1.group());}
        if(m2.find()){tvmobile.setText("Mobile : "+ m2.group());}
        if(m3.find()){tvmobile.setText("Mobile : "+ m3.group());}
    }

    public void extractWeb(String str){
        final String WEB_REGEX1="^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
        final String WEB_REGEX2="(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})";
        Pattern p1 = Pattern.compile(WEB_REGEX1, Pattern.MULTILINE);
        Pattern p2 = Pattern.compile(WEB_REGEX2, Pattern.MULTILINE);
        Matcher m1 = p1.matcher(str);
        Matcher m2 = p2.matcher(str);
        if(m1.find()){tvweb.setText("WebPage : "+ m1.group());}
        if(m2.find()){tvweb.setText("WebPage : "+ m2.group());}
    }


}
