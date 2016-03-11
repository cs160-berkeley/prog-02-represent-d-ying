package cs160.prog2;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "TWITTER_KEY";
    private static final String TWITTER_SECRET = "TWITTER_SECRET";


    private Button mCurLocButton;
    private GoogleApiClient mGoogleApiClient;
    private String mLatitudeText;
    private String mLongitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        Intent intent = getIntent();

        if (intent.getStringExtra("LOC") != null && intent.getStringExtra("LOC").equals("UPDATE")) {

            getRepData(randomZIP());
        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Button mZipButton = (Button) findViewById(R.id.zip_btn);
        mCurLocButton = (Button) findViewById(R.id.cur_loc_btn);

        mZipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText mZIP = (EditText) findViewById(R.id.zip);
                String ZIP = mZIP.getText().toString();

                if (ZIP.length() == 5) {

                    mZIP.setText("");
                    getRepData(ZIP);

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid ZIP code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String randomZIP() {
        Random random = new Random();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            digits.append(Integer.toString(random.nextInt(10)));
        }
        return digits.toString();
    }

    private String getCurrentLocation() {
        return mLatitudeText + "," + mLongitudeText;
    }

    private String getGeocodingURL(String location) {
        String loc = "address=" + location;
        if (location.length() != 5) {
            loc = "latlng=" + location;
        }

        String key = "GEO_API_KEY";
        return "https://maps.googleapis.com/maps/api/geocode/json?" + loc + "&key=" + key;
    }

    private String getVoteURL() {
        return "https://raw.githubusercontent.com/cs160-sp16/voting-data/master/election-county-2012.json";
    }

    private void getRepData(String location) {

        System.out.println("check");

        String loc = "zip=" + location;
        if (location.length() != 5) {
            String[] latLong = location.split(",");
            loc = "latitude=" + latLong[0] + "&longitude=" + latLong[1];
        }

        String APIKey = "SUNLIGHT_API_KEY";
        String sunlight = "https://congress.api.sunlightfoundation.com/legislators/locate?" + loc + "&apikey=" + APIKey;

        String countyURL = getGeocodingURL(location);
        String voteURL = getVoteURL();

        new AsyncTask<String, Void, JSONArray[]>() {

            private ProgressDialog progress;

            protected void onPreExecute() {
                progress = new ProgressDialog(MainActivity.this);
                progress.setMessage("Finding Reps...");
                progress.show();
            }

            protected JSONArray[] doInBackground(String... urls) {

                JSONArray[] repInfo = new JSONArray[4];

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
                            if (i == 2) {
                                repInfo[i] = new JSONArray(stringBuilder.toString());
                            } else {
                                repInfo[i] = new JSONObject(stringBuilder.toString()).getJSONArray("results");
                            }
                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
                return repInfo;
            }

            protected void onPostExecute(JSONArray[] response) {

                progress.dismiss();

                if (response != null) {

                    System.out.println(response[0].length());

                    if (response[0].length() == 0) {

                        getRepData(randomZIP());
                    } else {

                        String dataWear = extractDataWear(response[0], response[1], response[2]);

                        Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                        sendIntent.putExtra("REPS", dataWear);
                        startService(sendIntent);

                        String[] dataMobile = extractDataMobile(response[0]);

                        Intent intent = new Intent(getBaseContext(), RepsList.class);
                        intent.putExtra("REPS", dataMobile);
                        startActivity(intent);
                    }
                }
            }

        }.execute(sunlight, countyURL, voteURL);
    }

    private String[] extractDataMobile(JSONArray results) {
        ArrayList<String> reps = new ArrayList<String>();
        try {
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);

                String twitterID = result.getString("twitter_id");

                String rep = result.getString("first_name") + " " + result.getString("last_name") + "^";
                rep += result.getString("party") + "^";
                rep += result.getString("oc_email") + "^";
                rep += result.getString("website") + "^";

                rep += twitterID + "^";
                rep += result.getString("term_end") + "^";
                rep += result.getString("bioguide_id") + "^";
                rep += "https://twitter.com/" + twitterID + "/profile_image?size=original";
                reps.add(rep);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return reps.toArray(new String[reps.size()]);
    }

    private String extractDataWear(JSONArray results, JSONArray countyData, JSONArray voteData) {
        String reps = "";
        try {
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);

                String twitterID = result.getString("twitter_id");
                reps += result.getString("first_name") + " " + result.getString("last_name") + "~";
                reps += result.getString("party") + "~";
                reps += result.getString("term_end") + "~";
                reps += result.getString("bioguide_id") + "~";
                reps += "https://twitter.com/" + twitterID + "/profile_image?size=original^";
            }

            reps += "2012 Presidential Vote~";

            String county = "";

            JSONArray addComp = countyData.getJSONObject(0).getJSONArray("address_components");
            for (int i = 0; i < addComp.length(); i++) {
                JSONObject comp = addComp.getJSONObject(i);
                if (comp.getJSONArray("types").getString(0).equals("administrative_area_level_2")) {
                    county = comp.getString("short_name");
                }
            }

            reps += county + "~";

            String obamaPc = "";
            String romneyPc = "";

            for (int i = 0; i < voteData.length(); i++) {
                JSONObject countyVote = voteData.getJSONObject(i);
                if (county.equals(countyVote.getString("county-name") + " County")) {
                    obamaPc = countyVote.getString("obama-percentage");
                    romneyPc = countyVote.getString("romney-percentage");
                    break;
                }
            }

            reps += "Obama Vote: " + obamaPc + "~";
            reps += "Romney Vote: " + romneyPc;

            System.out.println(reps);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return reps;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
        }

        mCurLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loc = getCurrentLocation();
                System.out.println(loc);

                getRepData(loc);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mLatitudeText = String.valueOf(location.getLatitude());
            mLongitudeText = String.valueOf(location.getLongitude());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
