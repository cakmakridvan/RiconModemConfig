package com.database;

import java.util.HashMap;

import com.database.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

// Kayıt Silme işlemi Burda yapılır

public class KitapDetay extends Activity {
	Button b1,b2;
	TextView t1,t2,t3;
	int id;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_kitapdetay);
	/*
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Kayıt Listesi");
	*/
		b1 = (Button)findViewById(R.id.button1);
		b2 = (Button)findViewById(R.id.button2);
		
		t1 = (TextView)findViewById(R.id.adi);
		t2 = (TextView)findViewById(R.id.yazari);
		t3 = (TextView)findViewById(R.id.yili);


		Intent intent=getIntent();
		id = intent.getIntExtra("id", 0);//id değerini integer olarak aldık. Burdaki 0 eğer değer alınmazsa default olrak verilecek değer
		
		Database db = new Database(getApplicationContext());
		HashMap<String, String> map = db.kitapDetay(id);//Bu id li row un değerini hashmap e aldık
		
		t1.setText(map.get("kitap_adi"));
		t2.setText(map.get("yazar").toString());
		t3.setText(map.get("yil").toString());

		
		
		b1.setOnClickListener(new View.OnClickListener() {//Kitap düzenle butonuna tıklandıgında tekrardan kitabın id sini gönderdik
			
			public void onClick(View v) {
				 Intent intent = new Intent(getApplicationContext(), KitapDuzenle.class);
				 intent.putExtra("id", (int)id);
				 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                 startActivity(intent);
			}
		});
		
		b2.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(KitapDetay.this);
    	        alertDialog.setTitle("Uyarı");
    	        alertDialog.setMessage("Kayıt Silinsin mi?");
    	        alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog,int which) {
    	            	Database db = new Database(getApplicationContext());
    	            	db.kitapSil(id);
    	            	Toast.makeText(getApplicationContext(), "Kayıt Başarıyla Silindi", Toast.LENGTH_SHORT).show();
    	            	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	                startActivity(intent);//bu id li kitabı sildik ve Anasayfaya döndük
    	                finish();
    	                
    	            }
    	        });
    	        alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog,int which) {
    	            	
    	            }
    	        });
    	        alertDialog.show();     
				
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


}
