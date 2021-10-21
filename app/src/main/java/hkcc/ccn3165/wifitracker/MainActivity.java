package hkcc.ccn3165.wifitracker;

import android.Manifest;
import java.util.Calendar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.aware.WifiAwareManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String FILE_NAME = "example.txt";
    private WifiReceiver receiverWifi;
    private TextView txtlong, txtlati, txtssid, txttest;
    private Button btnstart, btnstarttimer;
    private EditText edittime;
    private FusedLocationProviderClient client;
    private WifiManager mainWifi;
    List<ScanResult> wifiList;
    ListAdapter adapter;
    ListView wifiDetails;
    private CountDownTimer countDownTimer;
    private long timeLeftInMs = 1800000, interval; // 30mins
    public static String  str, strtime;
    public static String log, lat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiDetails = (ListView) findViewById(R.id.wifiDetails);
        txtlong = (TextView) findViewById(R.id.txtlong);
        txtlati = (TextView) findViewById(R.id.txtlati);
        btnstart = (Button) findViewById(R.id.btnstart);
        btnstarttimer = (Button) findViewById(R.id.btnstarttimer);
        edittime = (EditText) findViewById(R.id.edittime);
        txttest = (TextView) findViewById(R.id.txttest);
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        client = LocationServices.getFusedLocationProviderClient(this);
        getWiFiList();
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWiFiList();
            }
        });
        btnstarttimer.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                interval = 1000;
                if (!(((edittime.getText().toString()).equals(""))||((edittime.getText().toString()).equals("0"))))
                    interval = 1000 * Long.parseLong(edittime.getText().toString());
                countDownTimer = new CountDownTimer(timeLeftInMs, interval) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeftInMs = millisUntilFinished;
                        getWiFiList();
                    }

                    @Override
                    public void onFinish() {
                    }
                }.start();
            }
        });

    }
    private void scanWifiList() {
        mainWifi.startScan();
        wifiList = mainWifi.getScanResults();
        setAdapter();
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{  Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET}, 10);
    }
    private void setAdapter() {
        adapter = new ListAdapter(getApplicationContext(), wifiList);
        wifiDetails.setAdapter(adapter);
    }


    public void save(View v){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = null;
        SimpleDateFormat times = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("yyyyMMdd");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            str = df.format(c.getTime());
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            times = new SimpleDateFormat("HHmmss");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            strtime = times.format(c.getTime());
        }
        FILE_NAME = str+strtime+".txt";
        int co= wifiDetails.getAdapter().getCount();
        String message;
       message = ""+"Date: "+str+" Time: "+strtime+" (HHmmSS)\n"+ "Longitude: " + log + "\nLatitude: " +lat + "\n";
       for (int i = 0; i<co ; i++){
            message += "SSID :: " + wifiList.get(i).SSID
                    + "\nBSSID :: " + wifiList.get(i).BSSID+ "\n";
        }

       // message = "Longitude: " + log + " Latitude: " +lat + "\n" + ListAdapter.wifiString;
        //MainActivity.counter++;
        //getWiFiList();
        FileOutputStream fos = null;
        //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        try {
        //    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            fos = openFileOutput(FILE_NAME,MODE_PRIVATE);
            fos.write(message.getBytes());
       //    message = "";
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public void load(View v){
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null){
                sb.append(text).append("\n");
            }

            loadList(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void Email(View v){
        sendMail();
    }
    public void getWiFiList(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    log = Double.toString(location.getLongitude());
                    lat = Double.toString(location.getLatitude());
                    txtlong.setText(log);
                    txtlati.setText(lat);
                }
            }
        });
        scanWifiList();
    }
    public void loadList(String mainList){
        Intent intent = new Intent(this, LoadFile.class);
        intent.putExtra("showList", mainList);
       // finish();
        startActivity(intent);
    }

    private void sendMail(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = null;
        SimpleDateFormat times = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            df = new SimpleDateFormat("yyyyMMdd");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            str = df.format(c.getTime());
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            times = new SimpleDateFormat("HHmmss");
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            strtime = times.format(c.getTime());
        }
        FILE_NAME = str+strtime+".txt";
        int co= wifiDetails.getAdapter().getCount();
        String message2;
        message2 = ""+"Date: "+str+" Time: "+strtime+" (HHmmSS)\n"+ "Longitude: " + log + "\nLatitude: " +lat + "\n";
        for (int i = 0; i<co ; i++){
            message2 += "SSID :: " + wifiList.get(i).SSID
                    + "\nBSSID :: " + wifiList.get(i).BSSID+ "\n";
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message2);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Please choose an e-mail client"));
    }
    class WifiReceiver extends BroadcastReceiver{
        public void onReceive(Context c, Intent intent){

        }
    }
}
