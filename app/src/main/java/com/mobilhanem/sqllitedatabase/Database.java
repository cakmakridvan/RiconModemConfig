package com.mobilhanem.sqllitedatabase;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "sqllite_database";//database ad˝

	private static final String TABLE_NAME = "kitap_listesi";
	private static String KITAP_ADI = "kitap_adi";
	private static String KITAP_ID = "id";
	private static String KITAP_YAZARI = "yazar";
	private static String KITAP_BASIM_YILI = "yil";


	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {  // Databesi oluşturuyoruz.Bu methodu biz çağırmıyoruz. Databese de obje oluşturduğumuzda otamatik çağırılıyor.
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KITAP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KITAP_ADI + " TEXT,"
				+ KITAP_YAZARI + " TEXT,"

				+ KITAP_BASIM_YILI + " TEXT" + ")";
		db.execSQL(CREATE_TABLE);
	}




	public void kitapSil(int id){ //id si belli olan row u silmek iÁin

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, KITAP_ID + " = ?",
				new String[] { String.valueOf(id) });
		db.close();
	}

	public void kitapEkle(String kitap_adi, String kitap_yazari,String kitap_basim_yili) {
		//kitapEkle methodu ise adı istende Databese veri eklemek için
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KITAP_ADI, kitap_adi);
		values.put(KITAP_YAZARI, kitap_yazari);
		values.put(KITAP_BASIM_YILI, kitap_basim_yili);


		db.insert(TABLE_NAME, null, values);
		db.close(); //Database Bağlantısını kapattık*/
	}


	public HashMap<String, String> kitapDetay(int id){
		//Databeseden id si belli olan rowu çekmek için.
		//Bu methodda sadece tek row değerleri alınır.

		//HashMap bir Áift boyutlu arraydir.anahtar-deer ikililerini bir arada tutmak iÁin tasarlanm˝˛t˝r.
		//mesala map.put("x","300"); mesala burda anahtar x deeri 300.

		HashMap<String,String> kitap = new HashMap<String,String>();
		String selectQuery = "SELECT * FROM " + TABLE_NAME+ " WHERE id="+id;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if(cursor.getCount() > 0){
			kitap.put(KITAP_ADI, cursor.getString(1));
			kitap.put(KITAP_YAZARI, cursor.getString(2));
			kitap.put(KITAP_BASIM_YILI, cursor.getString(3));

		}
		cursor.close();
		db.close();
		// return kitap
		return kitap;
	}

	public  ArrayList<HashMap<String, String>> kitaplar(){

		//Bu methodda ise tablodaki tüm değerleri alıyoruz
		//ArrayList ad˝ ¸st¸nde Array lerin listelendii bir Array.Burda hashmapleri listeleyeceiz
		//Herbir sat˝r˝ deer ve value ile hashmap a at˝yoruz. Her bir sat˝r 1 tane hashmap array˝ demek.
		//olusturdugumuz t¸m hashmapleri ArrayList e at˝p geri dˆn¸yoruz(return).

		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_NAME;
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<HashMap<String, String>> kitaplist = new ArrayList<HashMap<String, String>>();

		if (cursor.moveToFirst()) {
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				for(int i=0; i<cursor.getColumnCount();i++)
				{
					map.put(cursor.getColumnName(i), cursor.getString(i));
				}

				kitaplist.add(map);
			} while (cursor.moveToNext());
		}
		db.close();
		// return kitap liste
		return kitaplist;
	}

	public void kitapDuzenle(String kitap_adi, String kitap_yazari,String kitap_basim_yili,int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		//Bu methodda ise var olan veriyi güncelliyoruz(update)
		ContentValues values = new ContentValues();
		values.put(KITAP_ADI, kitap_adi);
		values.put(KITAP_YAZARI, kitap_yazari);
		values.put(KITAP_BASIM_YILI, kitap_basim_yili);


		// updating row
		db.update(TABLE_NAME, values, KITAP_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public int getRowCount() {
		// Bu method bu uygulamada kullanılmıyor ama her zaman lazım olabilir.Tablodaki row sayısını geri döner.
		//Login uygulamasında kullanacağız
		String countQuery = "SELECT  * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();
		// return row count
		return rowCount;
	}


	public void resetTables(){
		//Bunuda uygulamada kullanmıyoruz. Tüm verileri siler. tabloyu resetler.
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
