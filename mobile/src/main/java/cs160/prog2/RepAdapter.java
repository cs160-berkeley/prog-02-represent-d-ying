package cs160.prog2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.InputStream;
import java.util.List;

public class RepAdapter extends BaseAdapter {

    Context context;
    String[] data;
    private static LayoutInflater inflater = null;

    public RepAdapter(Context context, String[] data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String[] repData = data[position].split("\\^");
        final View row = convertView == null ? inflater.inflate(R.layout.rep_row, null) : convertView;

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

                TextView name = (TextView) row.findViewById(R.id.name);
                String nameText = repData[0] + " (" + repData[1] + ")";
                name.setText(nameText);
                TextView email = (TextView) row.findViewById(R.id.email);
                email.setText(repData[2]);
                TextView website = (TextView) row.findViewById(R.id.website);
                website.setText(repData[3]);

                final TextView tweetView = (TextView) row.findViewById(R.id.tweet);

                TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
                    @Override
                    public void success(Result<AppSession> appSessionResult) {
                        AppSession session = appSessionResult.data;
                        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                        twitterApiClient.getStatusesService().userTimeline(null, repData[4], 1, null, null, false, false, false, true, new Callback<List<Tweet>>() {
                            @Override
                            public void success(Result<List<Tweet>> listResult) {
                                for (Tweet tweet : listResult.data) {
                                    tweetView.setText(tweet.text);
                                }
                            }

                            @Override
                            public void failure(TwitterException e) {
                                System.out.println(e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void failure(TwitterException e) {
                        System.out.println(e.getMessage());
                    }
                });

                ImageView photo = (ImageView) row.findViewById(R.id.photo);

                String color = "#cccccc";
                if (repData[1].equals("D")) {
                    color = "#000090";
                } else if (repData[1].equals("R")) {
                    color = "#900000";
                }

                photo.setBackgroundColor(Color.parseColor(color));

                if (profileImg != null) {
                    photo.setImageBitmap(profileImg);
                }
            }

        }.execute(repData[7]);

        final String[] detailedData = new String[5];
        detailedData[0] = repData[0];
        detailedData[1] = repData[1];
        detailedData[2] = repData[5];
        detailedData[3] = repData[6];
        detailedData[4] = repData[7];

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedView.class);
                intent.putExtra("REP", detailedData);
                context.startActivity(intent);
            }
        });
        return row;
    }
}
