package routerconfigration;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilhanem.sqllitedatabase.Database;
import com.mobilhanem.sqlliteexample.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import main.application.MyApplicaiton;
import searchablespinnermine.SearchableSpinner;
import sms.ConnectivityChangeReceiver;

/**
 * Created by kaya on 22/08/17.
 */

public class SunucuAyarlari extends AppCompatActivity implements View.OnClickListener, ConnectivityChangeReceiver.ConnectivityReceiverListener{

    EditText sunucu_ip1,sunucu_ip2,sunucu_ip3,sunucu_ip4,sunucu_port;
    Spinner lokasyon;
    Button gonder;
    String get_sunucu_ip1,get_sunucu_ip2,get_sunucu_ip3,get_sunucu_ip4,get_sunucu_port,get_lokasyon;

    String sunucu_first_message,sunucu_last_message,cnvrt_sunucu_msg;
    StringBuilder sunucu_msg;

    // DataBase connection variable
    ArrayList<HashMap<String, String>> kitap_liste;
    String kitap_adlari[];
    String kitap_no[];
    String get_cep_no;
    int kitap_idler[];
    String mobil_no;
    ArrayAdapter<String> dataAdapter;

    String mesaj;

    private Timer myTimer4;
    int userInteraction4;

    SearchableSpinner search_spin_sunucu;

    String get_msg_sunucu = "";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS_SUNUCU = 0 ;
    PendingIntent sentPI_sunucu  ;
    PendingIntent deliveredPI_sunucu ;

    Handler handler_sunucu_show_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.sunucu_ayarlari);

        // Inside OnCreate Method
        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));

        sunucu_msg = new StringBuilder(100);

        sunucu_first_message = "m2m;enable;!m2m;modify;";
        sunucu_last_message = ";4;40;3;30;!";

        sunucu_ip1 = (EditText) findViewById(R.id.edt_sunucuayarlari_ip_1);
        sunucu_ip2 = (EditText) findViewById(R.id.edt_sunucuayarlari_ip_2);
        sunucu_ip3 = (EditText) findViewById(R.id.edt_sunucuayarlari_ip_3);
        sunucu_ip4 = (EditText) findViewById(R.id.edt_sunucuayarlari_ip_4);
        sunucu_port = (EditText) findViewById(R.id.edt_sunucu_port);

        //Searchable Spinner
        search_spin_sunucu = (SearchableSpinner) findViewById(R.id.search_spinner_sunucu_ayar);

        //lokasyon = (Spinner) findViewById(R.id.spin_sunucuayarlari_lokasyon_adlari);

        gonder = (Button) findViewById(R.id.btn_sunucuayarlari_gonder);
        gonder.setOnClickListener(this);
/*
        // DataBase Connection
        Database db = new Database(getApplicationContext()); // Db bağlantısı olu�turuyoruz. ilk seferde database olu�turulur.
        kitap_liste = db.kitaplar();//kitap listesini alıyoruz
        if(kitap_liste.size()==0){//kitap listesi boşsa
            Toast.makeText(getApplicationContext(), "Henüz Kayıt Eklenmemiş", Toast.LENGTH_LONG).show();

        }else {
            kitap_adlari = new String[kitap_liste.size()];
            kitap_no = new String[kitap_liste.size()];
            // kitap adlar�n� tutucam�z string arrayi olusturduk.
            kitap_idler = new int[kitap_liste.size()]; // kitap id lerini tutucam�z string arrayi olusturduk.
            for (int i = 0; i < kitap_liste.size(); i++) {
                kitap_adlari[i] = kitap_liste.get(i).get("kitap_adi");
                kitap_no[i] = kitap_liste.get(i).get("yil");

                kitap_idler[i] = Integer.parseInt(kitap_liste.get(i).get("id"));
                //Yukar�daki ile ayn� tek fark� de�erleri integer a �evirdik.

                dataAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, kitap_adlari);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                lokasyon.setAdapter(dataAdapter);

                lokasyon
                        .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                       int pos, long id) {

                                get_cep_no = kitap_no[pos]; // get cep_No

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub

                            }
                        });
            }
        }
      */




        //Searchable Spinner

        Database db = new Database(getApplicationContext()); // Db bağlantısı olu�turuyoruz. ilk seferde database olu�turulur.
        kitap_liste = db.kitaplar();//kitap listesini alıyoruz
        if(kitap_liste.size()==0){//kitap listesi boşsa

            final Toast toast_kayit = Toast.makeText(getApplicationContext(), "Henüz Kayıt Eklenmemiş", Toast.LENGTH_SHORT);
            toast_kayit.show();

            Handler handler_kayit = new Handler();
            handler_kayit.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast_kayit.cancel();
                }
            }, 1300);


        }else {
            kitap_adlari = new String[kitap_liste.size()];
            kitap_no = new String[kitap_liste.size()];
            // kitap adlar�n� tutucam�z string arrayi olusturduk.
            kitap_idler = new int[kitap_liste.size()]; // kitap id lerini tutucam�z string arrayi olusturduk.
            for (int i = 0; i < kitap_liste.size(); i++) {
                kitap_adlari[i] = kitap_liste.get(i).get("kitap_adi");
                kitap_no[i] = kitap_liste.get(i).get("yil");

                kitap_idler[i] = Integer.parseInt(kitap_liste.get(i).get("id"));
                //Yukar�daki ile ayn� tek fark� de�erleri integer a �evirdik.

                dataAdapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, kitap_adlari);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                search_spin_sunucu.setAdapter(dataAdapter);


                search_spin_sunucu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                        String spin_text = search_spin_sunucu.getSelectedItem().toString();

                        for (int i = 0; i < kitap_liste.size(); i++) {
                            kitap_adlari[i] = kitap_liste.get(i).get("kitap_adi");
                            kitap_no[i] = kitap_liste.get(i).get("yil");

                            kitap_idler[i] = Integer.parseInt(kitap_liste.get(i).get("id"));
                            //Yukar�daki ile ayn� tek fark� de�erleri integer a �evirdik.

                            if(spin_text.equals(kitap_adlari[i])){

                                get_cep_no = kitap_no[pos]; // get cep_No
                                break;

                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });


            }
        }


    }
/*
    @Override
    public void onBackPressed() {
        // your code.

        Intent in = new Intent(getApplicationContext(),
                ParametreDegistir.class);

        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(in);
    }
*/
    // Motion
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
                    .getBottom())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_sunucuayarlari_gonder:

                get_sunucu_ip1 = sunucu_ip1.getText().toString();
                get_sunucu_ip2 = sunucu_ip2.getText().toString();
                get_sunucu_ip3 = sunucu_ip3.getText().toString();
                get_sunucu_ip4 = sunucu_ip4.getText().toString();
                get_sunucu_port = sunucu_port.getText().toString();

                if(search_spin_sunucu.getSelectedItem() == null || search_spin_sunucu.getSelectedItem().equals("Lokasyonlar")){

                    get_lokasyon = "";
                }else{

                    get_lokasyon = search_spin_sunucu.getSelectedItem().toString();
                }

                if(get_sunucu_ip1.matches("") || get_sunucu_ip2.matches("") ||get_sunucu_ip3.matches("") || get_sunucu_ip4.matches("") || get_sunucu_port.matches("") || get_lokasyon.matches("") ||
                        get_lokasyon.matches("null")){

                    final Toast toast_bilgiler = Toast.makeText(getApplicationContext(), "Lütfen Tüm Bilgileri Giriniz", Toast.LENGTH_SHORT);
                    toast_bilgiler.show();

                    Handler handler_bilgiler = new Handler();
                    handler_bilgiler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast_bilgiler.cancel();
                        }
                    }, 1000);


                }else{

                    sunucu_msg.append(get_sunucu_ip1 + "." + get_sunucu_ip2 + "." + get_sunucu_ip3 + "." + get_sunucu_ip4);
                    sunucu_msg.append(";");
                    sunucu_msg.append(get_sunucu_port);

                    cnvrt_sunucu_msg = sunucu_first_message + sunucu_msg.toString() + sunucu_last_message;


                        sendSMSMessage(get_cep_no,cnvrt_sunucu_msg);

                        gonder.setEnabled(false);

                    //Starts Handler after 3 minutes
                    handler_sunucu_show_message = new Handler();
                    handler_sunucu_show_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "Hata!!! İşlem Gerçekleştirilemedi",
                                    Toast.LENGTH_SHORT).show();

                            gonder.setEnabled(true);

                        }
                    }, 120000);

                }

                break;
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected void sendSMSMessage(String phone,String msg) {

        get_msg_sunucu = msg;

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        sentPI_sunucu = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
        deliveredPI_sunucu = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                switch (getResultCode()) {

                    case Activity.RESULT_OK:

                        final Toast toast = Toast.makeText(getApplicationContext(), "SMS gönderildi",
                                Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 800);

                        //text_durum1.setText("Sms Gönderildi");

                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                        gonder.setEnabled(true);

                        final Toast toast_ariza = Toast.makeText(getApplicationContext(), "Genel Arıza",
                                Toast.LENGTH_SHORT);
                        toast_ariza.show();

                        Handler handler_ariza = new Handler();
                        handler_ariza.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast_ariza.cancel();
                            }
                        }, 1000);

                        //text_durum1.setText("Tel'noyu kontrol ediniz");

                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:

                        gonder.setEnabled(true);

                        final Toast toast_no_Service = Toast.makeText(getApplicationContext(), "Servis Yok",
                                Toast.LENGTH_SHORT);
                        toast_no_Service.show();

                        Handler handler_no_Service = new Handler();
                        handler_no_Service.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast_no_Service.cancel();
                            }
                        }, 1000);

                        //text_durum1.setText("Servis Yok");

                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:

                        gonder.setEnabled(true);

                        final Toast toast_null_pdu = Toast.makeText(getApplicationContext(), "Null PDU",
                                Toast.LENGTH_SHORT);
                        toast_null_pdu.show();

                        Handler handler_null_pdu = new Handler();
                        handler_null_pdu.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast_null_pdu.cancel();
                            }
                        }, 1000);

                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:

                        gonder.setEnabled(true);

                        final Toast toast_radio_off = Toast.makeText(getApplicationContext(), "Radio off",
                                Toast.LENGTH_SHORT);
                        toast_radio_off.show();

                        Handler handler_radio_off = new Handler();
                        handler_radio_off.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast_radio_off.cancel();
                            }
                        }, 1000);

                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {

                switch (getResultCode()) {

                    case Activity.RESULT_OK:

                        final Toast toast_sms_deliver = Toast.makeText(getApplicationContext(), "SMS teslim edildi",
                                Toast.LENGTH_SHORT);
                        toast_sms_deliver.show();

                        Handler handler_deliver = new Handler();
                        handler_deliver.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast_sms_deliver.cancel();
                            }
                        },1000);

                        break;

                    case Activity.RESULT_CANCELED:

                        gonder.setEnabled(true);

                        final Toast toast_cancel = Toast.makeText(getApplicationContext(), "SMS teslim hatası!!!",
                                Toast.LENGTH_SHORT);
                        toast_cancel.show();

                        Handler handler_cancel = new Handler();
                        handler_cancel.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast_cancel.cancel();
                            }
                        }, 1000);

                        //text_durum1.setText("Sms İletilemedi!!!");

                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)+ ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)|| ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)|| ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS) ) {

                new AlertDialog.Builder(this).setTitle("İzin Gerekli")
                        .setMessage("Uygulama için izinler gerekli")
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ActivityCompat.requestPermissions(SunucuAyarlari.this,
                                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                                        MY_PERMISSIONS_REQUEST_SEND_SMS_SUNUCU);
                            }
                        })
                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                gonder.setEnabled(true);
                            }
                        }).create().show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS_SUNUCU);
            }
        }

        else{

            try {

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phone, null, msg, sentPI_sunucu, deliveredPI_sunucu);

            }catch (Exception e){

                gonder.setEnabled(true);

                final Toast toast_islem_iptal = Toast.makeText(getApplicationContext(), "İşlem gerçekleştirilemedi!!!!",
                        Toast.LENGTH_SHORT);
                toast_islem_iptal.show();

                Handler handler_islem_iptal = new Handler();
                handler_islem_iptal.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast_islem_iptal.cancel();
                    }
                }, 1000);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        // We have many permission so we need get to all
        for (int permission : grantResults) {
            permissionCheck = permissionCheck + permission;
        }
        if (grantResults.length > 0
                && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(get_cep_no, null, get_msg_sunucu, sentPI_sunucu, deliveredPI_sunucu);

            }catch(Exception e){

                gonder.setEnabled(true);

                final Toast toast_islem_iptal2 = Toast.makeText(getApplicationContext(), "İşlem gerçekleştirilemedi!!!!",
                        Toast.LENGTH_SHORT);
                toast_islem_iptal2.show();

                Handler handler_islem_iptal2 = new Handler();
                handler_islem_iptal2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast_islem_iptal2.cancel();
                    }
                }, 1000);
            }


        } else {

            gonder.setEnabled(true);

            final Toast toast_izin = Toast.makeText(getApplicationContext(),
                    "İşlem Gerçekleştirilemedi ,Gerekli İzinler Verilmeli", Toast.LENGTH_SHORT);
            toast_izin.show();

            Handler handler_izin = new Handler();
            handler_izin.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toast_izin.cancel();
                }
            }, 1000);

        }



    }


    // Add this inside your class
    BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Bundle b = intent.getExtras();

            //phonem = b.getString("phone");
            mesaj = b.getString("mesaj");

            //Log.e("newmesage_phone", "" + phonem);
            Log.e("newmesage_mesaj", "" + mesaj);


            if(mesaj.equals("m2m enable SUCCESS\n")){

                Toast.makeText(getApplicationContext(), "m2m enable success\nİşlem Başarılı",
                        Toast.LENGTH_SHORT).show();

                gonder.setEnabled(true);

                finish();
                //startActivity(getIntent());

                Log.e("newmesage_phone", "" + cnvrt_sunucu_msg);
                Log.e("newmesage_mesaj", "" + sunucu_msg.toString());

                //remove postDelay
                handler_sunucu_show_message.removeCallbacksAndMessages(null);

            }



        }


    };

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
                .make(findViewById(R.id.sunucu_ayari), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}
