package cs160.prog2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.rep_row, null);
        }
        String[] repData = data[position].split("\\^");
        TextView name = (TextView) row.findViewById(R.id.name);
        name.setText(repData[0]);
        TextView email = (TextView) row.findViewById(R.id.email);
        email.setText(repData[1]);
        TextView website = (TextView) row.findViewById(R.id.website);
        website.setText(repData[2]);
        TextView tweet = (TextView) row.findViewById(R.id.tweet);
        tweet.setText(repData[3]);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedView.class);
                intent.putExtra("REP", data[position]);
                context.startActivity(intent);
            }
        });
        return row;
    }
}
