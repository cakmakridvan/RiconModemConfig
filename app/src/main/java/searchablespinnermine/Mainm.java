package searchablespinnermine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.mobilhanem.sqlliteexample.R;

import java.util.ArrayList;
import java.util.List;

public class Mainm extends AppCompatActivity {

    SearchableSpinner sd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity22_main);

        sd = (SearchableSpinner) findViewById(R.id.spinner);

        List<String> list = new ArrayList<String>();
        list.add("list 1");
        list.add("list 2");
        list.add("list 3");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sd.setAdapter(dataAdapter);
    }
}
