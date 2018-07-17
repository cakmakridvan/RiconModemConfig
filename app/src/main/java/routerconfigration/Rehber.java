package routerconfigration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.database.KitapEkle;
import com.database.MainActivity;
import com.database.R;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by kaya on 21/08/17.
 */

public class Rehber extends Activity implements View.OnClickListener{

    Button yenikayit,duzenle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.rehber);

        yenikayit = (Button) findViewById(R.id.btn_yenikayit);
        duzenle = (Button) findViewById(R.id.btn_duzenle);

        yenikayit.setOnClickListener(this);
        duzenle.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_yenikayit:


                Intent i = new Intent(Rehber.this, KitapEkle.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                break;

            case R.id.btn_duzenle:

                Intent go_main = new Intent(Rehber.this,MainActivity.class);
                go_main.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(go_main);
                break;
        }

    }
}
