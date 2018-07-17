package com.database;

import java.util.ArrayList;
import java.util.HashMap;

import com.database.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class KitapDuzenle extends Activity {
	Button b1;
	EditText e1,e2,e3,e4;
	int id;
	String get_startup_location_name,get_startup_location_code,get_startup_sim_no;
	String lokasyon_state_duzenle,kod_state_duzenle,sim_state_duzenle;

	// DataBase connection variable
	ArrayList<HashMap<String, String>> kitap_liste;
	String kitap_adlari[];
	String kitap_no[];
	String get_cep_no;
	int kitap_idler[];
	String mobil_no;
	ArrayAdapter<String> dataAdapter;
	Database db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_kitapduzenle);
/*
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Kitap Detay");
		*/

		// DataBase Connection
		db = new Database(getApplicationContext()); // Db bağlantısı oluşturuyoruz. ilk seferde database oluşturulur.

		b1 = (Button)findViewById(R.id.button1);
		e1 = (EditText)findViewById(R.id.editText1);
		e2 = (EditText)findViewById(R.id.editText2);
		e3 = (EditText)findViewById(R.id.editText3);
		//e4 = (EditText)findViewById(R.id.editText4);



		Intent intent=getIntent();
		id = intent.getIntExtra("id", 0);

		//Database db = new Database(getApplicationContext());
		HashMap<String, String> map = db.kitapDetay(id);

		e1.setText(map.get("kitap_adi"));
		e2.setText(map.get("yazar").toString());
		e3.setText(map.get("yil").toString());
		//e4.setText(map.get("fiyat").toString());

		get_startup_location_name = e1.getText().toString();
		get_startup_location_code = e2.getText().toString();
		get_startup_sim_no = e3.getText().toString();

		// make UpperCase and trim()
		get_startup_location_name = get_startup_location_name.toUpperCase().trim();
		get_startup_location_code = get_startup_location_code.toUpperCase().trim();
		get_startup_sim_no = get_startup_sim_no.toUpperCase().trim();

		b1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String adi,yazari,yili,fiyati;
				adi = e1.getText().toString();
				yazari = e2.getText().toString();
				yili = e3.getText().toString();
				//fiyati = e4.getText().toString();

				// make UpperCase and trim()
				adi = adi.trim();
				yazari = yazari.trim();
				yili = yili.trim();
				//yili = yili.toUpperCase().trim();

				lokasyon_state_duzenle = "kayıtlı_degil";
				kod_state_duzenle = "kod_kayıtlı_değil";
				sim_state_duzenle = "sim_kayıtlı_değil";

				kitap_liste = db.kitaplar();//kitap listesini alıyoruz
				if(kitap_liste.size()==0){//kitap listesi boşsa
					//Toast.makeText(getApplicationContext(), "Henüz Kayıt Eklenmemiş", Toast.LENGTH_LONG).show();

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


					}
				}

				if(adi.matches("") || yazari.matches("") || yili.matches("")){
					Toast.makeText(getApplicationContext(), "Tüm Bilgileri Eksiksiz Doldurunuz", Toast.LENGTH_SHORT).show();
				}

				else if(get_startup_location_name.equals(adi) && get_startup_location_code.equals(yazari) && get_startup_sim_no.equals(yili)){

					// make LoverCase and trim()
					adi = adi.toLowerCase().trim();
					yazari = yazari.toLowerCase().trim();
					yili = yili.toLowerCase().trim();

		//			db = new Database(getApplicationContext());
					db.kitapDuzenle(adi, yazari, yili,id);//gönderdiğimiz id li kitabın değerlerini güncelledik.
					db.close();

					Toast.makeText(getApplicationContext(), "Kaydınız Başarıyla Düzenlendi.", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();

				}

				else{


					for (int i = 0; i < kitap_liste.size(); i++) {

						if(!get_startup_location_name.equals(adi)) {

							if (adi.equalsIgnoreCase(kitap_liste.get(i).get("kitap_adi"))) {

								Toast.makeText(getApplicationContext(), "Lokasyon Adı kayıtlı zaten", Toast.LENGTH_SHORT).show();

								lokasyon_state_duzenle = "kayıtlı_lokasyon";

								break;

							}
						}


					     if(!get_startup_location_code.equals(yazari)) {
						  if (yazari.equalsIgnoreCase(kitap_liste.get(i).get("yazar"))) {

							Toast.makeText(getApplicationContext(), "Lokasyon Kodu kayıtlı zaten", Toast.LENGTH_SHORT).show();

							kod_state_duzenle = "kayıtlı_kod";

							break;

						  }
				        	}


                         if(!get_startup_sim_no.equals(yili)) {
						   if (yili.equalsIgnoreCase(kitap_liste.get(i).get("yil"))) {

							   Toast.makeText(getApplicationContext(), "Sim No kayıtlı zaten", Toast.LENGTH_SHORT).show();

							   sim_state_duzenle = "kayıtlı_sim";

							   break;

						   }
					   }
					}

					if(!lokasyon_state_duzenle.equals("kayıtlı_lokasyon") && !kod_state_duzenle.equals("kayıtlı_kod") && !sim_state_duzenle.equals("kayıtlı_sim")) {

						// make LoverCase and trim()
						adi = adi.trim();
						yazari = yazari.trim();
						yili = yili.trim();
						//yili = yili.toLowerCase().trim();

						Database db = new Database(getApplicationContext());
						db.kitapDuzenle(adi, yazari, yili,id);//gˆnderdiimiz id li kitab˝n deperlerini g¸ncelledik.
						db.close();

						Toast.makeText(getApplicationContext(), "Kaydınız Başarıyla Düzenlendi.", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}


				}


			}
		});


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


}
