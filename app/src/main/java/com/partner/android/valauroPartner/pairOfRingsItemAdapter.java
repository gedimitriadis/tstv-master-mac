package com.partner.android.valauroPartner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.partner.android.valauroPartner.MainActivity.imageTable;

public class pairOfRingsItemAdapter extends ArrayAdapter<pairOfRingsItem>{
    //Constructs a new pairOfRingsItemAdapter.
    // pairOfRingsItems is the list of rings, which is the data source of the adapter
    public pairOfRingsItemAdapter(Context context, ArrayList<pairOfRingsItem> pairOfRings) {
        super(context,0, pairOfRings);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new ring list item layout.
        View pairOfRingsView = convertView;
        if (pairOfRingsView == null) {
            pairOfRingsView = LayoutInflater.from(getContext()).inflate(
                    R.layout.compare_list_item, parent, false);
        }

        // Find the pair of rings at the given position in the list of pairs
        pairOfRingsItem currentPairOfRingsItem = getItem(position);

        //get the views

        ImageView WomanImageRing = (ImageView) pairOfRingsView.findViewById(R.id.Image_Ring_Woman);
        ImageView ManImageRing = (ImageView) pairOfRingsView.findViewById(R.id.Image_Ring_Man);

        TextView designWoman = (TextView) pairOfRingsView.findViewById(R.id.Design_Woman_TextView);
        TextView designMan = (TextView) pairOfRingsView.findViewById(R.id.Design_Man_TextView);

        TextView caratsWoman = (TextView) pairOfRingsView.findViewById(R.id.Carats_Woman_TextView);
        TextView caratsMan = (TextView) pairOfRingsView.findViewById(R.id.Carats_Man_TextView);

        TextView profileWoman = (TextView) pairOfRingsView.findViewById(R.id.Profile_Woman_TextView);
        TextView profileMan = (TextView) pairOfRingsView.findViewById(R.id.Profile_Man_TextView);

        TextView stonesWoman = (TextView) pairOfRingsView.findViewById(R.id.Stones_Woman_TextView);
        TextView stonesMan = (TextView) pairOfRingsView.findViewById(R.id.Stones_Man_TextView);

        TextView pairCost = (TextView) pairOfRingsView.findViewById(R.id.Pair_Price_Data_TextView);

        //set the content of the views

        WomanImageRing.setImageResource(currentPairOfRingsItem.getRingImageWoman());
        ManImageRing.setImageResource(currentPairOfRingsItem.getRingImageMan());

        designWoman.setText(currentPairOfRingsItem.getDesignWoman());
        designMan.setText(currentPairOfRingsItem.getDesignMan());

        caratsWoman.setText(currentPairOfRingsItem.getCaratsWoman());
        caratsMan.setText(currentPairOfRingsItem.getCaratsMan());

        profileWoman.setText(currentPairOfRingsItem.getProfileWoman());
        profileMan.setText(currentPairOfRingsItem.getProfileMan());

        stonesWoman.setText(""+ currentPairOfRingsItem.getStonesWoman());
        stonesMan.setText("" + currentPairOfRingsItem.getStonesMan());

        pairCost.setText(currentPairOfRingsItem.getPairCost());

        // Return the list item view that is now showing the appropriate data
        return pairOfRingsView;

    }

}
