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
import java.util.List;
import java.util.Timer;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import main.application.MyApplicaiton;
import searchablespinnermine.SearchableSpinner;
import sms.ConnectivityChangeReceiver;

/**
 * Created by kaya on 22/08/17.
 */

public class ApnAyarlari extends AppCompatActivity implements View.OnClickListener, ConnectivityChangeReceiver.ConnectivityReceiverListener {

    EditText apn_name,apn_username,apn_password;
    Spinner apn_nat_mode,apn_lokasyon,apn_net_type;
    Button apn_gonder;
    String get_apn_name,get_apn_username,get_apn_password,get_apn_nat_mode,get_apn_lokasyon,get_apn_net_type;
    StringBuilder apn_msg;
    String apn_first_message,apn_last_message,cnvrt_apn_msg;
    String mesaj;
    String nat_masseage,routed_message;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS_APN = 0 ;


    PendingIntent sentPI  ;
    PendingIntent deliveredPI ;

    String get_msg = "";


// DataBase connection variable
    ArrayList<HashMap<String, String>> kitap_liste;
    String kitap_adlari[];
    String kitap_no[];
    String get_cep_no;
    int kitap_idler[];
    String mobil_no;
    ArrayAdapter<String> dataAdapter;


    //NatMode connection
    List<String> nat_list;
    ArrayAdapter<String> nat_adapter;

    //NetType connection
    List<String> net_type_list;
    ArrayAdapter<String> net_type_adapter;

    private Timer myTimer2;
    int userInteraction2;

    SearchableSpinner search_spin;

    Handler handler_apn_show_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        getSupportActionBar().hide(); //<< this
        setContentView(R.layout.apn_ayarlari);

        // Inside OnCreate Method
        registerReceiver(broadcastReceiver, new IntentFilter("broadCastName"));

        apn_name = (EditText) findViewById(R.id.edt_apnayarlari_apn_adi);
        apn_username = (EditText) findViewById(R.id.edt_apnayarlari_kullanici_adi);
        apn_password = (EditText) findViewById(R.id.edt_apnayarlari_parola);
        apn_nat_mode = (Spinner) findViewById(R.id.spin_apnayarlari_nat_mode);
        //apn_lokasyon = (Spinner) findViewById(R.id.spin_apnayarlari_lokasyon_adları);
        apn_net_type = (Spinner) findViewById(R.id.spin_apnayarlari_net_type);

        //Searchable Spinner
        search_spin = (SearchableSpinner) findViewById(R.id.search_spinner);

        apn_gonder = (Button) findViewById(R.id.btn_apn_ayarlari_gonder);
        apn_gonder.setOnClickListener(this);

//Sms Message
        apn_msg = new StringBuilder(100);
        apn_first_message = "modem;modify;0;";
        apn_last_message = ";1;pppd;";
        nat_masseage = "masq;add;modem;";
        routed_message = "masq;delete;modem;";

//NatMode connection
        nat_list = new ArrayList<String>();
        nat_list.add("NAT");
        nat_list.add("Routed");

        nat_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,nat_list);
        nat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apn_nat_mode.setAdapter(nat_adapter);


        //NetType connection
        net_type_list = new ArrayList<String>();
        net_type_list.add("Auto");
        net_type_list.add("3G");
        net_type_list.add("LTE");

        net_type_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,net_type_list);
        net_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apn_net_type.setAdapter(net_type_adapter);



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
                apn_lokasyon.setAdapter(dataAdapter);

                apn_lokasyon
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
                search_spin.setAdapter(dataAdapter);


                        search_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                                       int pos, long id) {

                                String spin_text = search_spin.getSelectedItem().toString();

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

        Intent inm = new Intent(getApplicationContext(),
                ParametreDegistir.class);

        inm.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        inm.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(inm);
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

            case R.id.btn_apn_ayarlari_gonder:

                get_apn_name = apn_name.getText().toString();
                get_apn_username = apn_username.getText().toString();
                get_apn_password = apn_password.getText().toString();

                if(search_spin.getSelectedItem() == null || search_spin.getSelectedItem().equals("Lokasyonlar")){

                    get_apn_lokasyon = "";
                }else{

                    get_apn_lokasyon = search_spin.getSelectedItem().toString();
                }


                get_apn_nat_mode = apn_nat_mode.getSelectedItem().toString();
                get_apn_net_type = apn_net_type.getSelectedItem().toString();


                if(get_apn_lokasyon.matches("") || get_apn_lokasyon.matches("null") ||
                        get_apn_nat_mode.matches("") || get_apn_nat_mode.matches("null") || get_apn_net_type.matches("") || get_apn_net_type.matches("null")){


                    final Toast toast_bilgiler = Toast.makeText(getApplicationContext(), "Lütfen Tüm Bilgileri Giriniz", Toast.LENGTH_SHORT);
                    toast_bilgiler.show();

                    Handler handler_bilgiler = new Handler();
                    handler_bilgiler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast_bilgiler.cancel();
                        }
                    }, 1000);
                }
                else{

                    if(get_apn_name.matches("") || get_apn_username.matches("") || get_apn_password.matches("")){


                        apn_msg.append(";;;");

                    }else {

                        apn_msg.append(get_apn_name);
                        apn_msg.append(";;");
                        apn_msg.append(get_apn_username);
                        apn_msg.append(";");
                        apn_msg.append(get_apn_password);
                    }

                    //cnvrt_apn_msg = apn_first_message + apn_msg.toString() + apn_last_message;

                    if(get_apn_net_type.equals("Auto")){
                        //auto
                        cnvrt_apn_msg = apn_first_message + apn_msg.toString() + ";;auto" + apn_last_message;

                    }

                   else if(get_apn_net_type.equals("3G")){
                        //wcdma
                        cnvrt_apn_msg = apn_first_message + apn_msg.toString() + ";;wcdma" + apn_last_message;
                    }

                    else if(get_apn_net_type.equals("LTE")){
                        //lte
                        cnvrt_apn_msg = apn_first_message + apn_msg.toString() + ";;lte" + apn_last_message;
                    }


                        sendSMSMessage(get_cep_no, cnvrt_apn_msg);

                        apn_gonder.setEnabled(false);

                    //Starts Handler after 3 minutes
                    handler_apn_show_message = new Handler();
                    handler_apn_show_message.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(), "Hata!!! İşlem Gerçekleştirilemedi",
                                    Toast.LENGTH_SHORT).show();

                            apn_gonder.setEnabled(true);

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

                        apn_gonder.setEnabled(true);

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

                        apn_gonder.setEnabled(true);

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

                        apn_gonder.setEnabled(true);

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

                        apn_gonder.setEnabled(true);

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

                        apn_gonder.setEnabled(true);

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

                                ActivityCompat.requestPermissions(ApnAyarlari.this,
                                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                                        MY_PERMISSIONS_REQUEST_SEND_SMS_APN);


                            }
                        })
                        .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                apn_gonder.setEnabled(true);
                            }
                        }).create().show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS_APN);
            }
        }

        else{

            try {

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phone, null, msg, sentPI, deliveredPI);

            }catch (Exception e){

                apn_gonder.setEnabled(true);

                final Toast toast_islem_iptal =  Toast.makeText(getApplicationContext(), "İşlem gerçekleştirilemedi!!!!",
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
               }
            catch (Exception e){

                apn_gonder.setEnabled(true);

                final Toast toast_islem_iptal2 =  Toast.makeText(getApplicationContext(), "İşlem gerçekleştirilemedi!!!!",
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

                    apn_gonder.setEnabled(true);

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
/*
    public void crashClick(View view) {
        throw new RuntimeException("This is a crash");
    }
*/

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

                Toast.makeText(getApplicationContext(), "modem modify success\n",
                        Toast.LENGTH_SHORT).show();

                Log.e("newmesage_phone", "" + cnvrt_apn_msg);
                Log.e("newmesage_mesaj", "" + apn_msg.toString());

                if(get_apn_nat_mode.equals("NAT")){

                    sendSMSMessage(get_cep_no, nat_masseage);

                }

                else if(get_apn_nat_mode.equals("Routed")){

                    sendSMSMessage(get_cep_no, routed_message);
                }

            }


            if(mesaj.equals("masq add SUCCESS\n")){

                Toast.makeText(getApplicationContext(), "masq added\nİşlem Başarılı",
                        Toast.LENGTH_SHORT).show();

                apn_gonder.setEnabled(true);

                finish();
                //startActivity(getIntent());

                Log.e("newmesage_phone", "" + cnvrt_apn_msg);
                Log.e("newmesage_mesaj", "" + apn_msg.toString());

                //remove postDelay
                handler_apn_show_message.removeCallbacksAndMessages(null);


            }

            if(mesaj.equals("masq delete SUCCESS\n")){

                Toast.makeText(getApplicationContext(), "masq deleted\nİşlem Başarılı",
                        Toast.LENGTH_SHORT).show();

                apn_gonder.setEnabled(true);

                finish();
                //startActivity(getIntent());

                //remove postDelay
                handler_apn_show_message.removeCallbacksAndMessages(null);


                Log.e("newmesage_phone", "" + cnvrt_apn_msg);
                Log.e("newmesage_mesaj", "" + apn_msg.toString());


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
                .make(findViewById(R.id.apn_ayar), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
}
