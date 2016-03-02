package cs160.prog2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DetailedView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        Intent intent = getIntent();
        String rep = intent.getStringExtra("REP");
        String[] repData = getRepData(rep);

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(repData[0]);
        TextView party = (TextView) findViewById(R.id.party);
        party.setText(repData[1]);
        TextView termEnd = (TextView) findViewById(R.id.term_end);
        termEnd.setText(repData[2]);
        TextView committees = (TextView) findViewById(R.id.committees);
        committees.setText(repData[3]);
        TextView bills = (TextView) findViewById(R.id.bills);
        bills.setText(repData[4]);
    }

    private String[] getRepData(String repName) {
        ArrayList<String> rep = new ArrayList<String>();
        rep.add("Diane Feinstien");
        rep.add("Democrat");
        rep.add("Term ends: January 3, 2019");
        rep.add("Senate Select Committee on Intelligence\nSenate Committee on Appropriations\nSenate Committee on Judiciary\nSenate Committee on Rules and Administration");
        rep.add("02/12/2015\nComprehensive Addiction and Recovery Act of 2015\n\n02/11/2016\nA bill to amend section 875(c) of title 18, United States Code, to include an intent requirement");
        return rep.toArray(new String[rep.size()]);
    }
}
