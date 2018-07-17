	package com.mobilhanem.sqllitedatabase;
	
	import java.util.ArrayList;
	import java.util.HashMap;
	import com.mobilhanem.sqlliteexample.R;
	import android.os.Bundle;
	import android.app.ActionBar;
	import android.app.Activity;
	import android.content.Intent;
	import android.text.Editable;
	import android.text.InputFilter;
	import android.text.Spanned;
	import android.text.TextWatcher;
	import android.view.Menu;
	import android.view.MenuItem;
	import android.view.View;
	import android.widget.AdapterView;
	import android.widget.ArrayAdapter;
	import android.widget.EditText;
	import android.widget.ListView;
	import android.widget.Toast;

	import com.crashlytics.android.Crashlytics;
	import io.fabric.sdk.android.Fabric;
	
	public class MainActivity extends Activity {
	
		ListView lv;
		ArrayAdapter<String> adapter;	
		ArrayList<HashMap<String, String>> kitap_liste;
		String kitap_adlari[];
		int kitap_idler[];
		EditText search_text;
		String data;
		int clicked_item;
		Database db;

		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Fabric.with(this, new Crashlytics());
			setContentView(R.layout.activity_main);
		/*
			 ActionBar actionBar = getActionBar();
		     actionBar.setDisplayHomeAsUpEnabled(false);
		     */
		search_text = (EditText) findViewById(R.id.edt_search);



		}
		
		public void onResume()
	    {   //neden onResume metodu kullandığımı ders içinde anlattım.
	    super.onResume();
	    db = new Database(getApplicationContext()); // Db bağlantısı oluşturuyoruz. ilk seferde database oluşturulur.
	    kitap_liste = db.kitaplar();//kitap listesini al�yoruz
	    if(kitap_liste.size()==0){//kitap listesi bo�sa
	   	 Toast.makeText(getApplicationContext(), "Henüz Kayıt Eklenmemiş", Toast.LENGTH_SHORT).show();
	    }else{
		     kitap_adlari = new String[kitap_liste.size()]; // kitap adlarını tutucamız string arrayi olusturduk.
		     kitap_idler = new int[kitap_liste.size()]; // kitap id lerini tutucamız string arrayi olusturduk.
		     for(int i=0;i<kitap_liste.size();i++){
		    	 kitap_adlari[i] = kitap_liste.get(i).get("kitap_adi");
		    	 //kitap_liste.get(0) bize arraylist içindeki ilk hashmap arrayini döner. Yani tablomuzdaki ilk satır değerlerini
		    	 //kitap_liste.get(0).get("kitap_adi") //bize arraylist içindeki ilk hashmap arrayin anahtarı kitap_adi olan value döner
		    	 
		    	 kitap_idler[i] = Integer.parseInt(kitap_liste.get(i).get("id"));
		    	//Yukarıdaki ile aynı tek farkı değerleri integer a çevirdik.
		     }
		     //Kitapları Listeliyoruz ve bu listeye listener atıyoruz
		     lv = (ListView) findViewById(R.id.list_view);
			    
		     adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.kitap_adi, kitap_adlari);
		     lv.setAdapter(adapter);

			 search_text.addTextChangedListener(new TextWatcher() {
				 @Override
				 public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				 }

				 @Override
				 public void onTextChanged(CharSequence s, int start, int before, int count) {


					 MainActivity.this.adapter.getFilter().filter(s);

				 }

				 @Override
				 public void afterTextChanged(Editable s) {

                   //String get_txt = search_text.getText().toString();

				 }
			 });



		     lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		 		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		 				long arg3) {

					data = (String)arg0.getItemAtPosition(arg2);

		 			//Listedeki her hangibir yere tıklandıgında tıklanan satırın sırasını alıyoruz.
		 			//Bu sıra id arraydeki sırayla aynı oldugundan tıklanan satırda bulunan kitapın id sini alıyor ve kitap detaya gönderiyoruz.


					kitap_liste = db.kitaplar();//kitap listesini alıyoruz
/*

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

*/


						for (int i = 0; i < kitap_liste.size(); i++) {

							if(data.equals(kitap_liste.get(i).get("kitap_adi"))){

								//kitap_idler[i] = Integer.parseInt(kitap_liste.get(i).get("id"));

								clicked_item = i;

								break;

							}

						}



		 			 Intent intent = new Intent(getApplicationContext(), KitapDetay.class);
		 			 intent.putExtra("id", (int)kitap_idler[clicked_item]);
					 intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		             startActivity(intent);
		 			
		 		}
		     });
	    }
	    
	    }
	
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			
			return super.onCreateOptionsMenu(menu);
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle presses on the action bar items
		    switch (item.getItemId()) {
		        case R.id.ekle:
		        	KitapEkle();
		            return true;
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
	
		 private void KitapEkle() {
		        Intent i = new Intent(MainActivity.this, KitapEkle.class);
			    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		        startActivity(i);
		    }
	}
