package hkcc.ccn3165.wifitracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class LoadFile extends AppCompatActivity {
    private TextView txtwifilist;
    private String wifiList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_file);
        Intent intent = getIntent();
        wifiList = intent.getStringExtra("showList");
        txtwifilist=(TextView)findViewById(R.id.txtwifilist);
        txtwifilist.setMovementMethod(new ScrollingMovementMethod());
        txtwifilist.setText(wifiList);
    }
}
