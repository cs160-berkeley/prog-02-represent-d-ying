package cs160.prog2;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VoteFragment extends CardFragment{

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        String votes = this.getArguments().getString("VOTE");
        View root = inflater.inflate(R.layout.fragment_vote, container, false);
        if (votes != null) {
            String[] data = votes.split("_");
            TextView title = (TextView) root.findViewById(R.id.title);
            title.setText(data[0]);
            TextView county = (TextView) root.findViewById(R.id.county);
            county.setText(data[1]);
            TextView obama = (TextView) root.findViewById(R.id.obama);
            obama.setText(data[2]);
            TextView romney = (TextView) root.findViewById(R.id.romney);
            romney.setText(data[3]);
        }
        return root;
    }
}
