package cs160.prog2;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.InputStream;
import java.util.ArrayList;

public class RepsList extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reps_list);

        Intent intent = getIntent();
        String[] reps = intent.getStringArrayExtra("REPS");

        RepAdapter adapter = new RepAdapter(this, reps);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}
