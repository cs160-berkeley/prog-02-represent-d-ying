package cs160.prog2;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.view.Gravity;

import java.util.List;

public class GridPagerAdapter extends FragmentGridPagerAdapter {

    private String[] data;

    public GridPagerAdapter(Context ctx, FragmentManager fm, String[] data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getFragment(int row, int column) {
        String cur = data[column];
        Bundle bundle = new Bundle();
        if (column == data.length-1) {
            bundle.putString("VOTE", cur);
            VoteFragment fragment = new VoteFragment();
            fragment.setArguments(bundle);
            fragment.setCardGravity(Gravity.CENTER);
            fragment.setExpansionEnabled(true);
            return fragment;
        } else {
            bundle.putString("REP", cur);
            RepFragment fragment = new RepFragment();
            fragment.setArguments(bundle);
            fragment.setCardGravity(Gravity.CENTER);
            fragment.setExpansionEnabled(true);
            return fragment;
        }
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int rowNum) {
        return data.length;
    }
}
