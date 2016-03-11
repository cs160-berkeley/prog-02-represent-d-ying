package cs160.prog2;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RepFragment extends CardFragment {

    String rep;
    TextView name, party;

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rep = this.getArguments().getString("REP");
        View root = inflater.inflate(R.layout.fragment_rep, container, false);
        if (rep != null) {
            String[] repData = rep.split("~");
            String partyString = "Independent";
            if (repData[1].equals("D")) {
                partyString = "Democrat";
            } else if (repData[1].equals("R")) {
                partyString = "Republican";
            }

            name = (TextView) root.findViewById(R.id.rep_name);
            name.setText(repData[0]);
            party = (TextView) root.findViewById(R.id.rep_party);
            party.setText(partyString);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent(getActivity(), WatchToPhoneService.class);
                    sendIntent.putExtra("REP", rep);
                    getActivity().startService(sendIntent);
                }
            });
        }
        return root;
    }
}
