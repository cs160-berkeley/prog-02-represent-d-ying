package cs160.prog2;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class RepsList extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reps_list);

        Intent intent = getIntent();
        String loc = intent.getStringExtra("LOC");

        String[] reps = getReps(loc);
        RepAdapter adapter = new RepAdapter(this, reps);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    private String[] getReps(String loc) {
        ArrayList<String> reps = new ArrayList<String>();
        reps.add("Barbara Boxer (D)^senator@boxer.senate.gov^www.boxer.senate.gov^@SenateDems stood united at the Supreme Court today to tell @Senate_GOPs: #DoYourJob");
        reps.add("Diane Feinstien (D)^senator@feinstein.senate.gov^www.feinstein.senate.gov^Love seeing the new signage at the new Castle Mountains National Monument! #ProtectCADesert ");
        reps.add("Barbara Lee (D)^barbara.lee@mail.house.gov^lee.house.gov^The Thelma Harris Gallery is a gem in the #EastBay w/ amazing exhibits on display celebrating Af Am artists!");
        return reps.toArray(new String[reps.size()]);
    }
}
