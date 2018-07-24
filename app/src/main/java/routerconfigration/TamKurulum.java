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
import android.view.KeyEvent;
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

/**
 * Created by kaya on 21/08/17.
 */

import com.database.Database;
import com.database.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import main.application.MyApplicaiton;
import searchablespinnermine.SearchableSpinner;
import sms.ConnectivityChangeReceiver;


public class TamKurulum extends AppCompatActivity implements View.OnClickListener, ConnectivityChangeReceiver.ConnectivityReceiverListener {

    ArrayList<HashMap<String, String>> kitap_liste;
    String kitap_adlari[];
    String kitap_no[];
    String get_cep_no;

    String mesaj = "bos";

    int kitap_idler[];

    private Timer myTimer,myTimer2,myTimer3,myTimer4;
    int userInteraction,userInteraction2,userInteraction3,userInteraction4;

    Spinner spin_location, spin_natmode;
    EditText apn_Adi, kullanici_Adi, parola, router_lan_ip_1, router_lan_ip_2, router_lan_ip_3, router_lan_ip_4, sunucu_port_ip_1, sunucu_port_ip_2, sunucu_port_ip_3, sunucu_port_ip_4, port,router_lan_ip_sbnetmsk;
    Button gonder;

    String get_apn_Adi, get_kullanici_Adi, get_parola, get_router_lan_ip_1, get_router_lan_ip_2, get_router_lan_ip_3, get_router_lan_ip_4, get_sunucu_port_ip_1, get_sunucu_port_ip_2, get_sunucu_port_ip_3, get_sunucu_port_ip_4, get_port,get_router_lan_ip_sbnetmsk, get_spin_location;
    String get_nat_mode;
    String mobil_no;

    ArrayAdapter<String> dataAdapter;

    List<String> nat_list;
    ArrayAdapter<String> nat_adapter;

    //TextView get_no;

    String apn_first_message,cnvrt_apn_mesaj,nat_message,routed_message,lan_message,cnvrt_lan_router_message,m2m_first_message,cnvrt_m2m_message;
    StringBuilder apn_message,lan_router_message,m2m_message;

    SearchableSpinner search_spin_kurulum;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS_TAM_KURULUM = 0 ;

    PendingIntent sentPI_Tam_Kurulum  ;
    PendingIntent deliveredPI_Tam_Kurulum ;
    String get_msg = "";

    int delay = 40000;// in ms

    Timer timer;

    Handler handler_show_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        getSupportActionBar().hide(); //<< this

        setContentView(R.layout.tam_kurulum);

        //spin_location = (Spinner) findViewById(R.id.spn_lokasyon_adları);
        //get_no = (TextView) findViewById(R.id.txt_cep_no);
        spin_natmode = (Spinner) findViewById(R.id.spin_nat_mode);

        apn_Adi = (EditText) findViewById(R.id.edt_apn_adi);
        kullanici_Adi = (EditText) findViewById(R.id.edt_kullanici_adi);
        parola = (EditText) findViewById(R.id.edt_parola);
        router_lan_ip_1 = (EditText) findViewById(R.id.edt_router_lan_ip_1);
        router_lan_ip_2 = (EditText) findViewById(R.id.edt_router_lan_ip_2);
        router_lan_ip_3 = (EditText) findViewById(R.id.edt_router_lan_ip_3);
        router_lan_ip_4 = (EditText) findViewById(R.id.edt_router_lan_ip_4);
        sunucu_port_ip_1 = (EditText) findViewById(R.id.edt_sunucu_ip_1);
        sunucu_port_ip_2 = (EditText) findViewById(R.id.edt_sunucu_ip_2);
        sunucu_port_ip_3 = (EditText) findViewById(R.id.edt_sunucu_ip_3);
        sunucu_port_ip_4 = (EditText) findViewById(R.id.edt_sunucu_ip_4);
        port = (EditText) findViewById(R.id.edt_port);
        router_lan_ip_sbnetmsk = (EditText) findViewById(R.id.edt_router_lan_ip_subnet_mask);

        //Searchable Spinner
        search_spin_kurulum = (SearchableSpinner) findViewById(R.id.search_spinner_tamkurulum);

        gonder = (Button) findViewById(R.id.btn_gonder);

        gonder.setOnClickListener(this);

        //StringBuilder defines
        apn_message = new StringBuilder(100);
        lan_router_message = new StringBuilder(100);
        m2m_message = new StringBuilder(100);

        //Sms messages
        apn_first_message = "modem;modify;0;";
        nat_message = "masq;add;modem;";
        routed_message = "masq;delete;modem;";
        lan_message = "lan;modify;";
        m2m_first_message = "m2m;enable;!m2m;modify;";

        nat_list = new ArrayList<String>();
        nat_list.add("NAT");
        nat_list.add("Routed");

        nat_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,nat_list);
        nat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_natmode.setAdapter(nat_adapter);

        // Inside OnCreate Method
        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));

        router_lan_ip_1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                String ip1 = router_lan_ip_1.getText().toString();

                if(ip1.length() == 3) {
                    router_lan_ip_2.requestFocus();
                }
                return false;
            }
        });

        router_lan_ip_2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String ip2 = router_lan_ip_2.getText().toString();

                if(ip2.length() == 3) {
                    router_lan_ip_3.requestFocus();
                }
                return false;
            }
        });


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
                spin_location.setAdapter(dataAdapter);

                spin_location
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
                search_spin_kurulum.setAdapter(dataAdapter);


                search_spin_kurulum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                        String spin_text = search_spin_kurulum.getSelectedItem().toString();

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

            case R.id.btn_gonder:

                //Get all data of EditText

                get_apn_Adi = apn_Adi.getText().toString();
                get_kullanici_Adi = kullanici_Adi.getText().toString();
                get_parola = parola.getText().toString();
                get_router_lan_ip_1 = router_lan_ip_1.getText().toString();
                get_router_lan_ip_2 = router_lan_ip_2.getText().toString();
                get_router_lan_ip_3 = router_lan_ip_3.getText().toString();
                get_router_lan_ip_4 = router_lan_ip_4.getText().toString();
                get_sunucu_port_ip_1 = sunucu_port_ip_1.getText().toString();
                get_sunucu_port_ip_2 = sunucu_port_ip_2.getText().toString();
                get_sunucu_port_ip_3 = sunucu_port_ip_3.getText().toString();
                get_sunucu_port_ip_4 = sunucu_port_ip_4.getText().toString();
                get_port = port.getText().toString();
                get_router_lan_ip_sbnetmsk = router_lan_ip_sbnetmsk.getText().toString();

                if(search_spin_kurulum.getSelectedItem() == null || search_spin_kurulum.getSelectedItem().equals("Lokasyonlar")){

                    get_spin_location = "";
                }else{

                    get_spin_location = search_spin_kurulum.getSelectedItem().toString();
                }


                get_nat_mode = spin_natmode.getSelectedItem().toString();
                mobil_no = get_cep_no;

               if(get_nat_mode.matches("") || get_nat_mode.matches("null")   ||
                        get_router_lan_ip_1.matches("") || get_router_lan_ip_2.matches("") || get_router_lan_ip_3.matches("") ||
                       get_router_lan_ip_4.matches("") || get_sunucu_port_ip_1.matches("") || get_sunucu_port_ip_2.matches("") || get_sunucu_port_ip_3.matches("") || get_sunucu_port_ip_4.matches("") ||
                       get_port.matches("") || get_router_lan_ip_sbnetmsk.matches("") || get_spin_location.matches("") || get_spin_location.matches("null")){

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

                   if(get_apn_Adi.matches("") || get_kullanici_Adi.matches("") || get_parola.matches("")){


                       apn_message.append(";;;");

                   }else{

                       apn_message.append(get_apn_Adi);
                       apn_message.append(";;");
                       apn_message.append(get_kullanici_Adi);
                       apn_message.append(";");
                       apn_message.append(get_parola);
                   }


// get APN message info
                   apn_message.append(";;auto;1;pppd;");
                   cnvrt_apn_mesaj = apn_first_message + apn_message.toString();
// get LAN message info
                   lan_router_message.append(get_router_lan_ip_1 + "." + get_router_lan_ip_2 + "." + get_router_lan_ip_3 + "." + get_router_lan_ip_4);
                   lan_router_message.append("/" + get_router_lan_ip_sbnetmsk);
                   lan_router_message.append(";;;;");
                   cnvrt_lan_router_message = lan_message + lan_router_message.toString();
// get M2M message info
                   m2m_message.append(get_sunucu_port_ip_1 + "." + get_sunucu_port_ip_2 + "." + get_sunucu_port_ip_3 + "." + get_sunucu_port_ip_4 + ";");
                   m2m_message.append(get_port);
                   m2m_message.append(";4;40;3;30;!");
                   cnvrt_m2m_message = m2m_first_message + m2m_message.toString();

                               sendSMSMessage(mobil_no,cnvrt_apn_mesaj);
                               gonder.setEnabled(false);

                   //Starts Handler after 3 minutes
                   handler_show_message = new Handler();
                   handler_show_message.postDelayed(new Runnable() {
                       @Override
                       public void run() {

                           Toast.makeText(getApplicationContext(), "Hata!!! İşlem Gerçekleştirilemedi",
                                   Toast.LENGTH_SHORT).show();

                           gonder.setEnabled(true);

                       }
                   }, 150000);
/*
                   Handler handler1 = new Handler();
                   handler1.postDelayed(new Runnable() {
                       @Override
                       public void run() {

                           //Handler works that: After delayMills,work process of inside run

                           if(get_nat_mode.equals("NAT")){

                               sendSMSMessage(mobil_no, nat_message);

                           }
                           else if(get_nat_mode.equals("Routed")){

                               sendSMSMessage(mobil_no, routed_message);

                           }


                       }
                   }, 10000);


                   Handler handler2 = new Handler();
                   handler2.postDelayed(new Runnable() {
                       @Override
                       public void run() {

                           sendSMSMessage(mobil_no, cnvrt_lan_router_message);

                       }
                   }, 15000);


                   Handler handler3 = new Handler();
                   handler3.postDelayed(new Runnable() {
                       @Override
                       public void run() {

                           sendSMSMessage(mobil_no, cnvrt_m2m_message);
                       }
                   }, 20000);
*/
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

         sentPI_Tam_Kurulum = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
         deliveredPI_Tam_Kurulum = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);

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

/*
                        myTimer = new Timer();
                        myTimer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {

                                if(userInteraction == 15){

                                    TamKurulum.this.runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            // your stuff to update the UI


                                            Toast.makeText(getApplicationContext(), "İşlem Başarılı",
                                                    Toast.LENGTH_SHORT).show();

                                            gonder.setEnabled(true);

                                            finish();
                                            startActivity(getIntent());

                                        }
                                    });
                                }

                                userInteraction++;

                            }
                        },0,1000);

*/

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
                    Manifest.permission.READ_PHONE_STATE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)|| ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS) ) {

                new AlertDialog.Builder(this).setTitle("İzin Gerekli")
                        .setMessage("Uygulama için izinler gerekli")
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ActivityCompat.requestPermissions(TamKurulum.this,
                                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                                        MY_PERMISSIONS_REQUEST_SEND_SMS_TAM_KURULUM);
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
                        MY_PERMISSIONS_REQUEST_SEND_SMS_TAM_KURULUM);
            }
        }

        else{

            try {

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phone, null, msg, sentPI_Tam_Kurulum, deliveredPI_Tam_Kurulum);

            }catch (Exception e){

                gonder.setEnabled(true);

                final Toast toast_islemler = Toast.makeText(getApplicationContext(), "İşlem gerçekleştirilemedi!!!!",
                        Toast.LENGTH_SHORT);
                toast_islemler.show();

                Handler handler_islemler = new Handler();
                handler_islemler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //do UI update
                        toast_islemler.cancel();
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
                sms.sendTextMessage(get_cep_no, null, get_msg, sentPI_Tam_Kurulum, deliveredPI_Tam_Kurulum);

            }catch(Exception e){

                gonder.setEnabled(true);

                final Toast toast_islemler2 = Toast.makeText(getApplicationContext(), "İşlem gerçekleştirilemedi!!!!",
                        Toast.LENGTH_SHORT);
                toast_islemler2.show();

                Handler handler_islemler2 = new Handler();
                handler_islemler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast_islemler2.cancel();
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

            if(mesaj.equals("modem modify SUCCESS\n")){

                final Toast toast_modem_success = Toast.makeText(getApplicationContext(), "modem modify success",
                        Toast.LENGTH_SHORT);
                toast_modem_success.show();


                if(get_nat_mode.equals("NAT")){

                    sendSMSMessage(mobil_no, nat_message);

                }
                else if(get_nat_mode.equals("Routed")){

                    sendSMSMessage(mobil_no, routed_message);

                }



                //gonder.setEnabled(true);

                //finish();
                //startActivity(getIntent());


            }

            if(mesaj.equals("masq add SUCCESS\n")){

                final Toast toast_masq_success = Toast.makeText(getApplicationContext(), "masq add Success",
                        Toast.LENGTH_SHORT);
                toast_masq_success.show();

                sendSMSMessage(mobil_no, cnvrt_lan_router_message);


            }

            if(mesaj.equals("masq delete SUCCESS\n")){

                final Toast toast_masq_delete_success = Toast.makeText(getApplicationContext(), "masq delete Success",
                        Toast.LENGTH_SHORT);
                toast_masq_delete_success.show();

                sendSMSMessage(mobil_no, cnvrt_lan_router_message);



            }

            if(mesaj.equals("lan modify SUCCESS\n")){

                final Toast toast_lan_modify_success = Toast.makeText(getApplicationContext(), "lan modify success",
                        Toast.LENGTH_SHORT);
                toast_lan_modify_success.show();

                sendSMSMessage(mobil_no, cnvrt_m2m_message);


                //gonder.setEnabled(true);

                //finish();
                //startActivity(getIntent());

            }

            if(mesaj.equals("m2m enable SUCCESS\n")){

                final Toast toast_m2m_enable_success = Toast.makeText(getApplicationContext(), "m2m enable success\nİşlem Başarılı şekilde Tamamlandı",
                        Toast.LENGTH_SHORT);
                toast_m2m_enable_success.show();



                gonder.setEnabled(true);

                finish();

                //remove postDelay
                handler_show_message.removeCallbacksAndMessages(null);




            }





        }
    };


    /*
            timer = new Timer();

        timer.schedule( new TimerTask(){
            public void run() {

                if(mesaj.equals("bos")){

                    TamKurulum.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // your stuff to update the UI

                            gonder.setEnabled(true);

                            Toast.makeText(getApplicationContext(), "İşlemler Başarılı şekilde yapıldı",
                                    Toast.LENGTH_SHORT).show();

                            finish();
                            startActivity(getIntent());

                        }
                    });

                }
            }
        }, delay);
     */




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
                .make(findViewById(R.id.tam_kurulum), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}
