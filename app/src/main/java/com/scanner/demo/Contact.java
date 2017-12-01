package com.scanner.demo;import android.app.AlertDialog;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.res.AssetManager;import android.graphics.Bitmap;import android.graphics.BitmapFactory;import android.provider.ContactsContract;import android.support.design.widget.FloatingActionButton;import android.support.v7.app.ActionBarActivity;import android.os.Bundle;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.PopupMenu;import android.util.Log;import android.view.LayoutInflater;import android.view.MenuItem;import android.view.View;import android.widget.Button;import android.widget.EditText;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.TextView;import android.widget.Toast;import com.googlecode.tesseract.android.TessBaseAPI;import com.scanlibrary.ScanConstants;import java.io.File;import java.io.FileNotFoundException;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.io.OutputStream;import java.util.regex.Matcher;import java.util.regex.Pattern;public class Contact extends AppCompatActivity {    Bitmap bitmap = null;    FloatingActionButton fabbutton,fabadd;    private ImageView scannedImageView;    private TessBaseAPI mTess;    String datapath = "";    EditText tvname,tvcompany,tvtitle,tvmobile,tvlandline,tvemail,tvweb,tvhome;    LinearLayout container;    String TAG = "myapp";    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_contact);        container = (LinearLayout)findViewById(R.id.container);        tvname = (EditText) findViewById(R.id.tvname);        tvcompany = (EditText) findViewById(R.id.tvcompany);        tvtitle = (EditText) findViewById(R.id.tvtitle);        tvmobile = (EditText) findViewById(R.id.tvmobile);        tvlandline = (EditText) findViewById(R.id.tvlandline);        tvemail = (EditText) findViewById(R.id.tvemail);        tvweb = (EditText) findViewById(R.id.tvweb);        tvhome = (EditText) findViewById(R.id.tvhome);        scannedImageView = (ImageView) findViewById(R.id.scannedImage);        fabbutton = (FloatingActionButton)findViewById(R.id.fabscan);        fabadd = (FloatingActionButton)findViewById(R.id.fabadd);        if(getIntent().hasExtra("byteArray")) {            bitmap = BitmapFactory.decodeByteArray(                    getIntent().getByteArrayExtra("byteArray"),0,getIntent()                            .getByteArrayExtra("byteArray").length);            scannedImageView.setImageBitmap(bitmap);        }        String language = "eng";        datapath = getFilesDir()+ "/tesseract/";        mTess = new TessBaseAPI();        checkFile(new File(datapath + "tessdata/"));        mTess.init(datapath, language);        fabbutton.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                String OCRresult = null;                mTess.setImage(bitmap);                OCRresult = mTess.getUTF8Text();                extractName(OCRresult);                extractEmail(OCRresult);                extractPhone(OCRresult);                extractWeb(OCRresult);                fabinvisible();                OCRresult = OCRresult.replaceAll("[-`$#%~¯‘?<>—“–\\\\{}/_()'!;^|\"]*", "");                String multiLines = OCRresult;                String[] line;                String delimiter = "\n";                line = multiLines.split(delimiter);                for (int i=0; i<line.length; i++){                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);                    final View addView = layoutInflater.inflate(R.layout.row, null);                    final TextView textOut = (TextView)addView.findViewById(R.id.textout);                    textOut.setText(line[i]);                    final ImageView buttonInsert = (ImageView)addView.findViewById(R.id.insert);                    buttonInsert.setOnClickListener(new View.OnClickListener(){                        @Override                        public void onClick(View v) {                            PopupMenu popup = new PopupMenu(Contact.this, buttonInsert);                            popup.getMenuInflater().inflate(R.menu.pop, popup.getMenu());                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {                                @Override                                public boolean onMenuItemClick(MenuItem item) {                                    switch (item.getItemId()) {                                        case R.id.menu_name:                                            tvname.setText(textOut.getText().toString());                                            return true;                                        case R.id.menu_company:                                            tvcompany.setText(textOut.getText().toString());                                            return true;                                        case R.id.menu_title:                                            tvtitle.setText(textOut.getText().toString());                                            return true;                                        case R.id.menu_mobile:                                            tvmobile.setText(textOut.getText().toString());                                            return true;                                        case R.id.menu_landline:                                            tvlandline.setText(textOut.getText().toString());                                            return true;                                        case R.id.menu_email:                                            tvemail.setText(textOut.getText().toString());                                            return true;                                        case R.id.menu_webpage:                                            tvweb.setText(textOut.getText().toString());                                            return true;                                        case R.id.menu_address:                                            tvhome.setText(textOut.getText().toString());                                            return true;                                        default:                                    }                                    return true;                                }                            });                            popup.show();                        }});                    container.addView(addView);                }            }        });        fabadd.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                Intent addcontact = new Intent(Intent.ACTION_INSERT);                addcontact.setType(ContactsContract.Contacts.CONTENT_TYPE);                addcontact.putExtra(ContactsContract.Intents.Insert.NAME,tvname.getText().toString());                addcontact.putExtra(ContactsContract.Intents.Insert.COMPANY,tvcompany.getText().toString());                addcontact.putExtra(ContactsContract.Intents.Insert.JOB_TITLE,tvtitle.getText().toString());                addcontact.putExtra(ContactsContract.Intents.Insert.PHONE,tvmobile.getText().toString());                addcontact.putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE,tvlandline.getText().toString());                addcontact.putExtra(ContactsContract.Intents.Insert.EMAIL,tvemail.getText().toString());                addcontact.putExtra(ContactsContract.Intents.Insert.POSTAL,tvhome.getText().toString());                startActivity(addcontact);            }        });    }    private void fabinvisible() {        fabbutton.setVisibility(View.GONE);        fabadd.setVisibility(View.VISIBLE);    }    private void checkFile(File dir) {        //directory does not exist, but we can successfully create it        if (!dir.exists() && dir.mkdirs()) {            copyFiles();        }        //The directory exists, but there is no data file in it        if (dir.exists()) {            String datafilepath = datapath + "/tessdata/eng.traineddata";            File datafile = new File(datafilepath);            if (!datafile.exists()) {                copyFiles();            }        }    }    private void copyFiles() {        try {            //location we want the file to be at            String filepath = datapath + "/tessdata/eng.traineddata";            //get access to AssetManager            AssetManager assetManager = getAssets();            //open byte streams for reading/writing            InputStream instream = assetManager.open("tessdata/eng.traineddata");            OutputStream outstream = new FileOutputStream(filepath);            //copy the file to the location specified by filepath            byte[] buffer = new byte[1024];            int read;            while ((read = instream.read(buffer)) != -1) {                outstream.write(buffer, 0, read);            }            outstream.flush();            outstream.close();            instream.close();        } catch (FileNotFoundException e) {            e.printStackTrace();        } catch (IOException e) {            e.printStackTrace();        }    }    public void extractName(String str){        final String NAME_REGEX1 = "^([A-Z]([A-Z]*|\\.) *){1,2}([A-Z][A-Z]+-?)+$";        final String NAME_REGEX2 = "^([A-Z][a-z]*((\\s)))+[A-Z][a-z]*$";        final String NAME_REGEX3= "^([A-Z]([a-z]*|\\.) *){1,2}([A-Z][a-z]+-?)+$";        Pattern p1 = Pattern.compile(NAME_REGEX1, Pattern.MULTILINE);        Pattern p2 = Pattern.compile(NAME_REGEX2, Pattern.MULTILINE);        Pattern p3 = Pattern.compile(NAME_REGEX3, Pattern.MULTILINE);        Matcher m1 =  p1.matcher(str);        Matcher m2 =  p2.matcher(str);        Matcher m3 =  p3.matcher(str);        if(m1.find()){System.out.println(m1.group());tvname.setText(m1.group());}        if(m2.find()){System.out.println(m2.group());tvname.setText(m2.group());}        if(m3.find()){System.out.println(m3.group());tvname.setText(m3.group());}    }    public void extractEmail(String str) {        final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";        Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.MULTILINE);        Matcher m = p.matcher(str);   // get a matcher object        if(m.find()){            System.out.println(m.group());            tvemail.setText(m.group());        }    }    public void extractPhone(String str){        final String PHONE_REGEX1="\\+?\\d[\\d -]{8,12}\\d";        final String PHONE_REGEX2="(?:^|\\D)(\\d{3})[)\\-. ]*?(\\d{3})[\\-. ]*?(\\d{4})(?:$|\\D)";        final String PHONE_REGEX3="^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$";        Pattern p1 = Pattern.compile(PHONE_REGEX1, Pattern.MULTILINE);        Pattern p2 = Pattern.compile(PHONE_REGEX2, Pattern.MULTILINE);        Pattern p3 = Pattern.compile(PHONE_REGEX3, Pattern.MULTILINE);        Matcher m1 = p1.matcher(str);        Matcher m2 = p2.matcher(str);        Matcher m3 = p3.matcher(str);// get a matcher object        if(m1.find()){tvmobile.setText(m1.group());}        if(m2.find()){tvmobile.setText(m2.group());}        if(m3.find()){tvmobile.setText(m3.group());}    }    public void extractWeb(String str){        final String WEB_REGEX1="^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";        final String WEB_REGEX2="(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})";        Pattern p1 = Pattern.compile(WEB_REGEX1, Pattern.MULTILINE);        Pattern p2 = Pattern.compile(WEB_REGEX2, Pattern.MULTILINE);        Matcher m1 = p1.matcher(str);        Matcher m2 = p2.matcher(str);        if(m1.find()){tvweb.setText(m1.group());}        if(m2.find()){tvweb.setText(m2.group());}    }}