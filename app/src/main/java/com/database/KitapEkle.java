package com.database;

import com.database.R;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import main.application.MyApplicaiton;
import sms.ConnectivityChangeReceiver;

public class KitapEkle extends AppCompatActivity implements ConnectivityChangeReceiver.ConnectivityReceiverListener {

	Button b1;
	EditText e1,e2,e3;

	// DataBase connection variable
	ArrayList<HashMap<String, String>> kitap_liste;
	String kitap_adlari[];
	String kitap_no[];
	String get_cep_no;
	int kitap_idler[];
	String mobil_no;
	ArrayAdapter<String> dataAdapter;
	String lokasyon_state,kod_state,sim_state;
	Database db;
	private Spinner spin_model;
	ArrayAdapter<String> adapter_spin;
	private List<String> list_spin;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		getSupportActionBar().hide(); //<< this
		setContentView(R.layout.activity_kitapekle);

		/*
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Kayıt Listesi");
		*/
		b1 = (Button)findViewById(R.id.button1);
		e1 = (EditText)findViewById(R.id.editText1);
		e2 = (EditText)findViewById(R.id.editText2);
		e3 = (EditText)findViewById(R.id.editText3);

		spin_model = findViewById(R.id.spin_kayit_mod);

		list_spin = new ArrayList<String>();
		list_spin.add("MLTE");
		list_spin.add("LTE");

		adapter_spin = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list_spin);
		adapter_spin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_model.setAdapter(adapter_spin);





		// DataBase Connection
		db = new Database(getApplicationContext()); // Db bağlantısı oluşturuyoruz. ilk seferde database oluşturulur.


		
		b1.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String adi,yazari,yili,fiyati,get_spin_model;
				adi = e1.getText().toString(); //Lokasyon Adı
				yazari = e2.getText().toString(); //Lokasyon Kodu
				yili = e3.getText().toString(); //Sim No
				get_spin_model = spin_model.getSelectedItem().toString();


				// make UpperCase
				adi = adi.trim();
				yazari = yazari.trim();
				yili = yili.trim();
				get_spin_model = get_spin_model.trim();

				//yili = yili.toUpperCase().trim();

				kitap_liste = db.kitaplar();//kitap listesini alıyoruz
				if(kitap_liste.size()==0){//kitap listesi boşsa
					//Toast.makeText(getApplicationContext(), "Henüz Kayıt Eklenmemiş", Toast.LENGTH_LONG).show();

				}else {
					kitap_adlari = new String[kitap_liste.size()];
					kitap_no = new String[kitap_liste.size()];
					// kitap adlarını tutucamız string arrayi olusturduk.
					kitap_idler = new int[kitap_liste.size()]; // kitap id lerini tutucamız string arrayi olusturduk.
					for (int i = 0; i < kitap_liste.size(); i++) {
						kitap_adlari[i] = kitap_liste.get(i).get("kitap_adi");
						kitap_no[i] = kitap_liste.get(i).get("yil");

						kitap_idler[i] = Integer.parseInt(kitap_liste.get(i).get("id"));
						//Yukar�daki ile ayn� tek fark� de�erleri integer a �evirdik.


					}
				}

                lokasyon_state = "kayıtlı_degil";
				kod_state = "kod_kayıtlı_değil";
				sim_state = "sim_kayıtlı_değil";

				if(adi.matches("") || yazari.matches("") || yili.matches("") || get_spin_model.matches("")   ){
				    Toast.makeText(getApplicationContext(), "Tüm Bilgileri Eksiksiz Doldurunuz", Toast.LENGTH_SHORT).show();
				}else{

					for (int i = 0; i < kitap_liste.size(); i++) {

						if(adi.equalsIgnoreCase(kitap_liste.get(i).get("kitap_adi"))){

							Toast.makeText(getApplicationContext(), "Lokasyon Adı kayıtlı zaten", Toast.LENGTH_SHORT).show();

							lokasyon_state = "kayıtlı_lokasyon";

							break;

						}

						if(yazari.equalsIgnoreCase(kitap_liste.get(i).get("yazar"))){

							Toast.makeText(getApplicationContext(), "Lokasyon Kodu kayıtlı zaten", Toast.LENGTH_SHORT).show();

							kod_state = "kayıtlı_kod";

							break;

						}

						if(yili.equalsIgnoreCase(kitap_liste.get(i).get("yil"))){

							Toast.makeText(getApplicationContext(), "Sim No kayıtlı zaten", Toast.LENGTH_SHORT).show();

							sim_state = "kayıtlı_sim";

							break;

						}

					}

                 if(!lokasyon_state.equals("kayıtlı_lokasyon") && !kod_state.equals("kayıtlı_kod") && !sim_state.equals("kayıtlı_sim")) {

					 // make LoverCase and trim()
					 adi = adi.trim();
					 yazari = yazari.trim();
					 yili = yili.trim();
					 get_spin_model = get_spin_model.trim();
					 //yili = yili.toLowerCase().trim();

					 Database db = new Database(getApplicationContext());
					 db.kitapEkle(adi, yazari, yili,get_spin_model);//kitap ekledik
					 db.close();
					 Toast.makeText(getApplicationContext(), "Kaydınız Başarıyla Eklendi.", Toast.LENGTH_SHORT).show();
					 e1.setText("");
					 e2.setText("");
					 e3.setText("");
				 }

				}
				
				
			}
		});
		
		
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
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	        return true;
	    default: return super.onOptionsItemSelected(item);  
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
				.make(findViewById(R.id.add_books), message, Snackbar.LENGTH_LONG);

		View sbView = snackbar.getView();
		TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(color);
		snackbar.show();
	}
}
