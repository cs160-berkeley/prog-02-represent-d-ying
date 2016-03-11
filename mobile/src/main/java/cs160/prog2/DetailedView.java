package cs160.prog2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class DetailedView extends AppCompatActivity {

    TextView committees;
    TextView bills;
    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        committees = (TextView) findViewById(R.id.committees);
        bills = (TextView) findViewById(R.id.bills);
        photo = (ImageView) findViewById(R.id.photo);

        Intent intent = getIntent();
        String[] rep = intent.getStringArrayExtra("REP");
        String[] repData = getRepData(rep);

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(repData[0]);
        TextView party = (TextView) findViewById(R.id.party);
        party.setText(repData[1]);
        TextView termEnd = (TextView) findViewById(R.id.term_end);
        termEnd.setText(repData[2]);
    }

    private String[] getRepData(String[] repInfo) {

        String bioGuideID = repInfo[3];
        String party = "Independent";
        String color = "#cccccc";
        if (repInfo[1].equals("D")) {
            party = "Democrat";
            color = "#000090";
        } else if (repInfo[1].equals("R")) {
            party = "Republican";
            color = "#900000";
        }

        photo.setBackgroundColor(Color.parseColor(color));


        ArrayList<String> rep = new ArrayList<String>();
        rep.add(repInfo[0]);
        rep.add(party);
        rep.add("Term ends: " + repInfo[2]);

        String APIKey = "SUNLIGHT_API_KEY";
        String committeesURL = "https://congress.api.sunlightfoundation.com/committees?member_ids=" + bioGuideID + "&apikey=" + APIKey;
        String billsURL = "https://congress.api.sunlightfoundation.com/bills?sponsor_id=" + bioGuideID + "&apikey=" + APIKey;

        new AsyncTask<String, Void, JSONObject[]>() {

            private ProgressDialog progress;

            protected void onPreExecute() {
                progress = new ProgressDialog(DetailedView.this);
                progress.setMessage("Getting details...");
                progress.show();
            }

            protected JSONObject[] doInBackground(String... urls) {

                JSONObject[] detailedInfo = new JSONObject[2];

                for (int i = 0; i < urls.length; i++) {
                    try {
                        URL url = new URL(urls[i]);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line).append("\n");
                            }
                            bufferedReader.close();
                            detailedInfo[i] = new JSONObject(stringBuilder.toString());
                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }

                return detailedInfo;
            }

            protected void onPostExecute(JSONObject[] response) {

                progress.dismiss();

                if (response != null) {
                    try {
                        JSONArray committeeResults = response[0].getJSONArray("results");
                        String committeesString = "";
                        for (int i = 0; i < committeeResults.length(); i++) {
                            JSONObject committee = committeeResults.getJSONObject(i);
                            committeesString += committee.getString("name") + "\n";
                        }
                        committees.setText(committeesString);

                        JSONArray billsResults = response[1].getJSONArray("results");
                        String billsString = "";
                        int numBills = Math.min(committeeResults.length(), 3);
                        for (int i = 0; i < numBills; i++) {
                            JSONObject bill = billsResults.getJSONObject(i);
                            billsString += bill.getString("introduced_on") + "\n" + bill.getString("short_title") + "\n";
                        }
                        bills.setText(billsString);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }

        }.execute(committeesURL, billsURL);

        new AsyncTask<String, Void, Bitmap>() {

            protected Bitmap doInBackground(String... urls) {
                String url = urls[0];
                Bitmap img;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    img = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
                return img;
            }

            protected void onPostExecute(Bitmap profileImg) {
                if (profileImg != null) {
                    photo.setImageBitmap(profileImg);
                }
            }

        }.execute(repInfo[4]);

        return rep.toArray(new String[rep.size()]);
    }
}
