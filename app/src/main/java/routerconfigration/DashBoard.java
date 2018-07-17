package routerconfigration;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import main.application.MyApplicaiton;
import sms.ConnectivityChangeReceiver;

import com.database.R;

import static com.database.R.id.btn_cikis;
import static com.database.R.id.transitions_container;

/**
 * Created by kaya on 21/08/17.
 */

public class DashBoard extends AppCompatActivity implements View.OnClickListener, ConnectivityChangeReceiver.ConnectivityReceiverListener {

    Button rehber,tamkurulum,durumsorgu,parametre,exit;
    ImageView img,img2,img3,img4;
    // Animation
    Animation rotation;
    LinearLayout ekran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.dashboard);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        // Manually checking internet connection
        //checkConnection();

        ekran = (LinearLayout) findViewById(R.id.transitions_container);

        img = (ImageView) findViewById(R.id.imageView2);
        img2 = (ImageView) findViewById(R.id.imageView3);
        img3 = (ImageView) findViewById(R.id.imageView4);
        img4 = (ImageView) findViewById(R.id.imageView5);



        rehber = (Button) findViewById(R.id.btn_rehber);
        tamkurulum = (Button) findViewById(R.id.btn_tam_kurulum);
        durumsorgu = (Button) findViewById(R.id.btn_durum_sorgula);
        parametre = (Button) findViewById(R.id.btn_parametre);
        exit = (Button) findViewById(btn_cikis);

        rehber.setOnClickListener(this);
        tamkurulum.setOnClickListener(this);
        durumsorgu.setOnClickListener(this);
        parametre.setOnClickListener(this);
        exit.setOnClickListener(this);
        ekran.setOnClickListener(this);

    }


    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityChangeReceiver.isConnected();
        showSnack(isConnected);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_rehber:

                img2.clearAnimation();
                img3.clearAnimation();
                img4.clearAnimation();

                //img.setRotation(img.getRotation() + 180);
                rotation = AnimationUtils.loadAnimation(DashBoard.this, R.anim.rotate);
                rotation.setFillAfter(true);
                img.startAnimation(rotation);


                Intent go_rehber = new Intent(DashBoard.this,Rehber.class);
                go_rehber.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(go_rehber);
                overridePendingTransition(0,0); //0 for no animation

                break;

            case R.id.btn_tam_kurulum:

                img.clearAnimation();
                img3.clearAnimation();
                img4.clearAnimation();

                //img2.setRotation(img2.getRotation() + 180);
                rotation = AnimationUtils.loadAnimation(DashBoard.this, R.anim.rotate);
                rotation.setFillAfter(true);
                img2.startAnimation(rotation);

                Intent go_tam_kurulum = new Intent(DashBoard.this,TamKurulum.class);
                go_tam_kurulum.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(go_tam_kurulum);


                break;

            case R.id.btn_durum_sorgula:

                img.clearAnimation();
                img2.clearAnimation();
                img3.clearAnimation();


                //img4.setRotation(img4.getRotation() + 180);
                rotation = AnimationUtils.loadAnimation(DashBoard.this, R.anim.rotate);
                rotation.setFillAfter(true);
                img4.startAnimation(rotation);

                Intent go_durum_sorgu = new Intent(DashBoard.this,DurumSorgula.class);
                go_durum_sorgu.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(go_durum_sorgu);

                break;

            case R.id.btn_parametre:

                img.clearAnimation();
                img2.clearAnimation();
                img4.clearAnimation();
                //img3.setRotation(img3.getRotation() + 180);
                rotation = AnimationUtils.loadAnimation(DashBoard.this, R.anim.rotate);
                rotation.setFillAfter(true);
                img3.startAnimation(rotation);

                Intent go_parametre = new Intent(DashBoard.this,ParametreDegistir.class);
                go_parametre.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(go_parametre);

                break;

            case btn_cikis:

                img.clearAnimation();
                img2.clearAnimation();
                img3.clearAnimation();
                img4.clearAnimation();

                finish();
                moveTaskToBack(true);

                break;

            case transitions_container:

                img.clearAnimation();
                img2.clearAnimation();
                img3.clearAnimation();
                img4.clearAnimation();

                break;


        }

    }

    // Showing the status in Snackbar
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
                .make(findViewById(R.id.transitions_container), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();


        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityChangeReceiver connectivityReceiver = new ConnectivityChangeReceiver();
        registerReceiver(connectivityReceiver, intentFilter);

        // register connection status listener
        MyApplicaiton.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
