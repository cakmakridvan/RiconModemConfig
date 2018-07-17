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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import main.application.MyApplicaiton;
import searchablespinnermine.SearchableSpinner;
import sms.ConnectivityChangeReceiver;

/**
 * Created by kaya on 22/08/17.
 */
import com.database.Database;
import com.database.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class  DurumSorgula extends AppCompatActivity implements View.OnClickListener, ConnectivityChangeReceiver.ConnectivityReceiverListener{

    ArrayList<HashMap<String, String>> kitap_liste;
    String kitap_adlari[];
    String kitap_no[];
    String get_cep_no;
    int kitap_idler[];

    String mobil_no;
    String get_lokasyon;

    Spinner lokasyon;

    ArrayAdapter<String> dataAdapter;

    String send_sms_mesaj;
    String mesaj;

    Button durm_kontrol;

    EditText gelen_mesaj;

    StringBuilder msj;

    List<String> temp_list;
    ArrayAdapter<String> temp_adapter;

    SearchableSpinner search_spin_durum_sorgula;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0 ;

    private static final int REQUEST_READ_PHONE_STATE = 0;

    PendingIntent sentPI ;
    PendingIntent deliveredPI ;

    Boolean checking_internet = null;

    Handler handler_durum_show_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        getSupportActionBar().hide(); //<< this


        setContentView(R.layout.durum_sorgula);

        // Inside OnCreate Method
        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));

        //registerReceiver(broadcastReceiver_network, new IntentFilter("isNetworkConnected"));

        gelen_mesaj = (EditText) findViewById(R.id.edt_yazi);

        msj = new StringBuilder(100);

        //lokasyon = (Spinner) findViewById(R.id.spn_durum_sorgu_lokasyon_adları);
        durm_kontrol = (Button) findViewById(R.id.btn_durum_sorgu);
        durm_kontrol.setOnClickListener(this);

        //Searchable Spinner
        search_spin_durum_sorgula = (SearchableSpinner) findViewById(R.id.search_spinner_durum);

        send_sms_mesaj = "modem;show;";
/*
        Database db = new Database(getApplicationContext()); // Db ba�lant�s� olu�turuyoruz. �lk seferde database olu�turulur.
        kitap_liste = db.kitaplar();//kitap listesini al�yoruz
        if(kitap_liste.size()==0){//kitap listesi bo�sa
            Toast.makeText(getApplicationContext(), "Henüz Kayıt Eklenmemiş", Toast.LENGTH_LONG).show();
        }else {
            kitap_adlari = new String[kitap_liste.size()];
            kitap_no = new String[kitap_liste.size()];
            // kitap adlar�n� tutucam�z string arrayi olusturduk.
            kitap_idler = new int[kitap_liste.size()]; // kitap id lerini tutucam�z string arrayi olusturduk.
            for (int i = 0; i < kitap_liste.size(); i++) {
                kitap_adlari[i] = kitap_liste.get(i).get("kitap_adi");
                kitap_no[i] = kitap_liste.get(i).get("yil");
                //kitap_liste.get(0) bize arraylist i�indeki ilk hashmap arrayini d�ner. Yani tablomuzdaki ilk sat�r de�erlerini
                //kitap_liste.get(0).get("kitap_adi") //bize arraylist i�indeki ilk hashmap arrayin anahtar� kitap_adi olan value d�ner



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

                                //get_no.setText(workRequestType);


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
                search_spin_durum_sorgula.setAdapter(dataAdapter);


                search_spin_durum_sorgula.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                        String spin_text = search_spin_durum_sorgula.getSelectedItem().toString();

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

    protected void sendSMSMessage(String phone,String msg) {

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

                        durm_kontrol.setEnabled(true);

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

                        durm_kontrol.setEnabled(true);

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

                        durm_kontrol.setEnabled(true);

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

                        durm_kontrol.setEnabled(true);

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
                        }, 800);


                        break;

                    case Activity.RESULT_CANCELED:

                        durm_kontrol.setEnabled(true);

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
                        Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_SMS)|| ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECEIVE_SMS)) {

                    new AlertDialog.Builder(this).setTitle("İzin Gerekli")
                                                         .setMessage("Uygulama için izinler gerekli")
                                                         .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {

                                                                 ActivityCompat.requestPermissions(DurumSorgula.this,
                                                                         new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                                                                         MY_PERMISSIONS_REQUEST_SEND_SMS);
                                                             }
                                                         })
                                                         .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 dialog.dismiss();
                                                                 durm_kontrol.setEnabled(true);
                                                             }
                                                         }).create().show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            }

            else{

                try {

                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(get_cep_no, null, send_sms_mesaj, sentPI, deliveredPI);

                }catch (Exception e){

                    durm_kontrol.setEnabled(true);

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
                sms.sendTextMessage(get_cep_no, null, send_sms_mesaj, sentPI, deliveredPI);

            }catch (Exception e){

                durm_kontrol.setEnabled(true);

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

                    durm_kontrol.setEnabled(true);

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
    // it takes messages from sms.IncoomingSms (class)
    BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Bundle b = intent.getExtras();

            //phonem = b.getString("phone");
            mesaj = b.getString("mesaj");

            if(mesaj != null){

                //Log.e("newmesage_phone", "" + phonem);
                Log.e("newmesage_mesaj", "" + mesaj);


                msj.append(mesaj);

                gelen_mesaj.setText(msj.toString());


                //remove postDelay
                handler_durum_show_message.removeCallbacksAndMessages(null);


            }


            }


        };

    /*

    // Add this inside your class
    BroadcastReceiver broadcastReceiver_network =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Bundle b = intent.getExtras();

            checking_internet = b.getBoolean("internet_state");

            if(checking_internet != null){

                if(checking_internet == true){


                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Do something on UiThread

                            Toast.makeText(getApplicationContext(),
                                    "aktif", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                else if(checking_internet == false){

                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Do something on UiThread


                            Toast.makeText(getApplicationContext(),
                                    "Pasif", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        }


    };

*/

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_durum_sorgu:

                if(search_spin_durum_sorgula.getSelectedItem() == null || search_spin_durum_sorgula.getSelectedItem().equals("Lokasyonlar")){

                    get_lokasyon = "";
                }else{

                    get_lokasyon = search_spin_durum_sorgula.getSelectedItem().toString();
                }

                if(get_lokasyon.matches("") || get_lokasyon.matches("null")){

                    final Toast toast_tum_bilgiler = Toast.makeText(getApplicationContext(), "Lütfen Tüm Bilgileri Giriniz", Toast.LENGTH_SHORT);
                    toast_tum_bilgiler.show();

                    Handler handler_tum_bilgiler = new Handler();
                    handler_tum_bilgiler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast_tum_bilgiler.cancel();
                        }
                    }, 1000);

                }else {

                           sendSMSMessage(get_cep_no, send_sms_mesaj);
// Button disabled
                           durm_kontrol.setEnabled(false);

                    //Starts Handler after 3 minutes
                    handler_durum_show_message = new Handler();
                    handler_durum_show_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "Hata!!! İşlem Gerçekleştirilemedi",
                                    Toast.LENGTH_SHORT).show();

                            durm_kontrol.setEnabled(true);

                        }
                    }, 150000);


                }

                break;
        }

    }

  //Checking internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
                .make(findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
/*
    @Override
    protected void onStop()
    {
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }
    */
}
