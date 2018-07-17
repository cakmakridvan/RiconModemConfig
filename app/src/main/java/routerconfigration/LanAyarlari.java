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

import com.database.Database;
import com.database.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import main.application.MyApplicaiton;
import searchablespinnermine.SearchableSpinner;
import sms.ConnectivityChangeReceiver;

/**
 * Created by kaya on 22/08/17.
 */

public class LanAyarlari extends AppCompatActivity implements View.OnClickListener, ConnectivityChangeReceiver.ConnectivityReceiverListener{

    EditText lan_ayar1,lan_ayar2,lan_ayar3,lan_ayar4,lan_subnet_mask;
    Spinner lokasyon;
    String get_lan_ayar1,get_lan_ayar2,get_lan_ayar3,get_lan_ayar4,get_lan_subnet_mask,get_lokasyon;
    Button gonder;

    String lan_first_message,cnvrt_lan_msg,lan_last_message;
    StringBuilder lan_msg;
    String mesaj;

    // DataBase connection variable
    ArrayList<HashMap<String, String>> kitap_liste;
    String kitap_adlari[];
    String kitap_no[];
    String get_cep_no;
    int kitap_idler[];
    String mobil_no;
    ArrayAdapter<String> dataAdapter;

    private Timer myTimer3;
    int userInteraction3;

    SearchableSpinner search_spin_lan;

    String get_msg = "";

    PendingIntent sentPI ;
    PendingIntent deliveredPI ;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS_LAN = 0 ;

    Handler handler_lan_show_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        getSupportActionBar().hide(); //<< this

        setContentView(R.layout.lan_ayarlari);

        // Inside OnCreate Method
        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));


        lan_first_message = "lan;modify;";
        lan_last_message = ";;;;";
        lan_msg = new StringBuilder(100);

        lan_ayar1 = (EditText) findViewById(R.id.edt_lanayari_lan_ip_1);
        lan_ayar2 = (EditText) findViewById(R.id.edt_lanayari_lan_ip_2);
        lan_ayar3 = (EditText) findViewById(R.id.edt_lanayari_lan_ip_3);
        lan_ayar4 = (EditText) findViewById(R.id.edt_lanayari_lan_ip_4);
        lan_subnet_mask = (EditText) findViewById(R.id.edt_lanayari_lan_ip_subnet_mask);

        //Searchable Spinner
        search_spin_lan = (SearchableSpinner) findViewById(R.id.search_spinner_lan);

        //lokasyon = (Spinner) findViewById(R.id.spn_lanayari_lokasyon_adlari);

        gonder = (Button) findViewById(R.id.btn_lanayari_gonder);
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
                search_spin_lan.setAdapter(dataAdapter);


                search_spin_lan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                        String spin_text = search_spin_lan.getSelectedItem().toString();

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

            case R.id.btn_lanayari_gonder:

                get_lan_ayar1 = lan_ayar1.getText().toString();
                get_lan_ayar2 = lan_ayar2.getText().toString();
                get_lan_ayar3 = lan_ayar3.getText().toString();
                get_lan_ayar4 = lan_ayar4.getText().toString();
                get_lan_subnet_mask = lan_subnet_mask.getText().toString();

                if(search_spin_lan.getSelectedItem() == null || search_spin_lan.getSelectedItem().equals("Lokasyonlar")){

                    get_lokasyon = "";
                }else{

                    get_lokasyon = search_spin_lan.getSelectedItem().toString();
                }



                if(get_lan_ayar1.matches("") || get_lan_ayar2.matches("") || get_lan_ayar3.matches("") || get_lan_ayar4.matches("") || get_lan_subnet_mask.matches("") || get_lokasyon.matches("") ||
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


                    lan_msg.append(get_lan_ayar1 + "." + get_lan_ayar2 + "." + get_lan_ayar3 + "." + get_lan_ayar4);
                    lan_msg.append("/");
                    lan_msg.append(get_lan_subnet_mask);

                    cnvrt_lan_msg = lan_first_message + lan_msg.toString() + lan_last_message;

                    sendSMSMessage(get_cep_no,cnvrt_lan_msg);

                    gonder.setEnabled(false);

                    //Starts Handler after 3 minutes
                    handler_lan_show_message = new Handler();
                    handler_lan_show_message.postDelayed(new Runnable() {
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

        get_msg = msg;

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);

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

                                ActivityCompat.requestPermissions(LanAyarlari.this,
                                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                                        MY_PERMISSIONS_REQUEST_SEND_SMS_LAN);
                            }
                        })
                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gonder.setEnabled(true);
                                dialog.dismiss();
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS_LAN);
            }
        }

        else{

            try {

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phone, null, msg, sentPI, deliveredPI);

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
                sms.sendTextMessage(get_cep_no, null, get_msg, sentPI, deliveredPI);

            }catch (Exception e){

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




            if(mesaj.equals("lan modify SUCCESS\n")){

                Toast.makeText(getApplicationContext(), "lan modify success\nİşlem Başarılı",
                        Toast.LENGTH_SHORT).show();

                gonder.setEnabled(true);

                finish();
                //startActivity(getIntent());

                //remove postDelay
                handler_lan_show_message.removeCallbacksAndMessages(null);

                Log.e("newmesage_phone", "" + cnvrt_lan_msg);
                Log.e("newmesage_mesaj", "" + lan_msg.toString());



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
                .make(findViewById(R.id.lan_ayari), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}
