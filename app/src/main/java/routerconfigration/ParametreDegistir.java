package routerconfigration;

import android.app.Activity;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import main.application.MyApplicaiton;
import sms.ConnectivityChangeReceiver;

/**
 * Created by kaya on 22/08/17.
 */

import com.database.R;



public class ParametreDegistir extends AppCompatActivity implements View.OnClickListener, ConnectivityChangeReceiver.ConnectivityReceiverListener {

    Button apn_ayar,lan_ayar,sunucu_ayar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.parametre_degistir);

        apn_ayar = (Button) findViewById(R.id.btn_apn_ayarlari);
        lan_ayar = (Button) findViewById(R.id.btn_lan_ayarlari);
        sunucu_ayar = (Button) findViewById(R.id.btn_sunucu_ayarlari);

        apn_ayar.setOnClickListener(this);
        lan_ayar.setOnClickListener(this);
        sunucu_ayar.setOnClickListener(this);
    }
/*
    @Override
    public void onBackPressed() {
        // your code.

        Intent in = new Intent(getApplicationContext(),
                DashBoard.class);

        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(in);
    }
*/
    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_apn_ayarlari:

                Intent go_apn = new Intent(ParametreDegistir.this,ApnAyarlari.class);
                go_apn.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(go_apn);

                break;

            case R.id.btn_lan_ayarlari:

                Intent go_lan = new Intent(ParametreDegistir.this,LanAyarlari.class);
                go_lan.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(go_lan);

                break;

            case R.id.btn_sunucu_ayarlari:

                Intent go_sunucu = new Intent(ParametreDegistir.this,SunucuAyarlari.class);
                go_sunucu.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(go_sunucu);

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        MyApplicaiton.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.parametre_change), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}
