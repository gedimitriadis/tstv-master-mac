package com.partner.android.valauroPartner;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.partner.android.valauroPartner.MainActivity.pairOfRingsItems;

public class CompareActivity extends AppCompatActivity implements java.io.Serializable{


    ArrayList<pairOfRingsItem> ringComparelist;
    /** Adapter for the list of rings */
    private pairOfRingsItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);
        populateRingsList();
    }

    private void populateRingsList() {
        // Construct the data source
        final ArrayList<pairOfRingsItem> ringComparelist;
        // gets the data from main activity through intent, pairOfRingsItems into ringComparelist
        ringComparelist = (ArrayList<pairOfRingsItem>)getIntent().getSerializableExtra("FILES_TO_SEND");

        // Create the adapter to convert the array to views
        adapter = new pairOfRingsItemAdapter(this, ringComparelist);

        // Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Delete pair of rings with dialog box
                AlertDialog.Builder adb=new AlertDialog.Builder(CompareActivity.this);
                adb.setTitle(R.string.deletePair);
                adb.setMessage(R.string.deletePairQuestion);
                final int positionToRemove = position;
                adb.setNegativeButton(R.string.Cancel, null);
                adb.setPositiveButton(R.string.Delete, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ringComparelist.remove(positionToRemove);
                        adapter.notifyDataSetChanged();
                        //delete from tha data source too because adapter doesn't refresh properly
                        pairOfRingsItems.remove(positionToRemove);
                    }});
                adb.show();
            }
        });

    }


    }











