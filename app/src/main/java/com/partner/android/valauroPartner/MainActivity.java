package com.partner.android.valauroPartner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.PendingIntent.getActivity;
import static android.support.v7.preference.PreferenceManager.*;



public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    // Will show the string "data" that holds the results

    int totalShippingCost;
    TextView date_TextView;
    TextView price_TextView;
    Button buttonCalculate;
    Button buttonAddToCompare;
    Button buttonCompare;
    ArrayList<Double> weights;
    static ArrayList<pairOfRingsItem> pairOfRingsItems = new ArrayList<pairOfRingsItem>();
    ArrayList profiles;
    int posW;
    int posM;
    String wds;
    String mds;
    double goldPricePerGrammar;
    double womanRingPriceNoVAT;
    double womanRingPriceWithVAT;
    double manRingPriceNoVAT;
    double manRingPriceWithVAT;
    double totalCost_noVAT;
    double totalCost_withVAT;
    double woman_multiplier_carats;
    double man_multiplier_carats;
    double weightCaratsMultiplierWoman;
    double weightCaratsMultiplierMan;
    double woman_multiplier_profile;
    double man_multiplier_profile;
    Integer labourCostW;
    int labourCostM;
    String FPA_percentage;
    String Profit_percentage;
    double NumFPA;
    double NumProfit;
    //TextView testPosition;
    //TextView testcaratsmultiplier;
    //TextView testprofilemultiplier;
    //TextView testlabourCost;
    // TextView woman_ring_price_no_vat;
    // TextView woman_ring_price_with_vat;
    // TextView man_ring_price_no_vat;
    // TextView man_ring_price_with_vat;
    //TextView totalCostNoVat;
    TextView totalCostWithVat;
    TextView femaleColor;
    TextView maleColor;
    ImageView ringImageWoman;
    ImageView ringImageMan;
    Spinner spinner_woman_design;
    Spinner spinner_woman_carats;
    Spinner spinner_woman_profiles;
    Spinner spinner_woman_stones;
    Spinner spinner_man_design;
    Spinner spinner_man_carats;
    Spinner spinner_man_profiles;
    Spinner spinner_man_stones;
    TextView internetSettings;
    TextView resetApp;

    final long delayMillis=1000;
    Handler h=null;
    Runnable r;


    // URL of object to be parsed
    String JsonURL = "https://www.quandl.com/api/v3/datasets/LBMA/GOLD.json?column_index=6&exclude_column_names=true&rows=2&order=asc&api_key=yu5Cz1dz6Vs4nPXu9TmL";
    // This string will hold the results
    String data = "";
    // Defining the Volley request queue that handles the URL request concurrently
    RequestQueue requestQueue;

    //format date of gold price taken
    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e(LOG_TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        if (isFirstTime()) {
            Toast.makeText(MainActivity.this, R.string.App_Updated, Toast.LENGTH_LONG).show();
        }


        // gets screen size for calling the right spinner layout
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);



        // restart app every morning at 7.00 to get the latest gold price
        h=new Handler(Looper.getMainLooper());
        r = new Runnable() {

            public void run() {

                //current time
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min=c.get(Calendar.MINUTE);
                int sec=c.get(Calendar.SECOND);
                String currenttime= String.valueOf(hour)+" : "+String.valueOf(min)+" : "+String.valueOf(sec);


                //comparing current time with 12:00pm
                if(currenttime.equals("7 : 0 : 0")){

                    //restarting the activity
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                }


                h.postDelayed(this, delayMillis);

            }
        };

        h.post(r);

        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Casts results into the TextView found within the main layout XML with id jsonData
        date_TextView = (TextView) findViewById(R.id.date);
        price_TextView = (TextView) findViewById(R.id.goldprice);
        buttonCalculate = (Button) findViewById(R.id.btnCalculate);
        buttonAddToCompare = (Button) findViewById(R.id.btnAddToCompare);
        // testPosition = (TextView) findViewById(R.id.test_position);
        // testcaratsmultiplier = (TextView)findViewById(R.id.test_multiplier_carats);
        // testprofilemultiplier = (TextView) findViewById(R.id.test_multiplier_profile);
        // testlabourCost = (TextView) findViewById(R.id.test_labour_cost);
        spinner_woman_design = (Spinner) findViewById(R.id.woman_design);
        spinner_woman_carats = (Spinner) findViewById(R.id.woman_carats);
        spinner_woman_profiles = (Spinner) findViewById(R.id.woman_profile);
        spinner_woman_stones = (Spinner) findViewById(R.id.woman_stones);
        spinner_man_design = (Spinner) findViewById(R.id.man_design);
        spinner_man_carats = (Spinner) findViewById(R.id.man_carats);
        spinner_man_profiles = (Spinner) findViewById(R.id.man_profile);
        spinner_man_stones = (Spinner) findViewById(R.id.man_stones);
        internetSettings = (TextView) findViewById(R.id.internetSettings);
        resetApp = (TextView) findViewById(R.id.resetApp);
        ringImageWoman = (ImageView) findViewById(R.id.female_ring_image);
        ringImageMan = (ImageView) findViewById(R.id.male_ring_image);
        femaleColor = (TextView) findViewById(R.id.female_color);
        maleColor = (TextView) findViewById(R.id.male_color);
        // woman_ring_price_no_vat = (TextView) findViewById(R.id.woman_ring_price_no_VAT);
        // woman_ring_price_with_vat = (TextView) findViewById(R.id.woman_ring_price_with_VAT);
        // man_ring_price_no_vat = (TextView) findViewById(R.id.man_ring_price_no_VAT);
        // man_ring_price_with_vat = (TextView) findViewById(R.id.man_ring_price_with_VAT);
        // totalCostNoVat = (TextView) findViewById(R.id.total_cost_noVAT);
        totalCostWithVat = (TextView) findViewById(R.id.total_cost_withVAT);
        buttonCalculate = (Button) findViewById(R.id.btnCalculate);
        buttonCompare = (Button) findViewById(R.id.Compare);



        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        buttonCalculate.setEnabled(true);
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Creating the JsonObjectRequest class called obreq, passing required parameters:
            //GET is used to fetch data from the server, JsonURL is the URL to be fetched from.
            JsonObjectRequest obreq = new JsonObjectRequest(Method.GET, JsonURL,
                    // The third parameter Listener overrides the method onResponse() and passes
                    //JSONObject as a parameter
                    new Response.Listener<JSONObject>() {

                        // Takes the response from the JSON request
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = response.getJSONObject("dataset");
                                JSONArray dataArray = obj.getJSONArray("data");
                                JSONArray innerData = dataArray.getJSONArray(0);

                                String date = innerData.getString(0);
                                String NewStringDate = formateDateFromstring("yyyy-MM-dd", "dd-MM-yyyy", date);
                                date_TextView.setText(NewStringDate);

                                String goldPrice = innerData.getString(1);
                                double d = Double.parseDouble(goldPrice);
                                double priceInKilo = d * 32.150;
                                price_TextView.setText(String.format(Locale.US, "%.2f", priceInKilo));

                            }
                            // Try and catch are included to handle any errors due to JSON
                            catch (JSONException e) {
                                // If an error occurs, this prints the error to the log
                                e.printStackTrace();
                            }
                        }
                    },
                    // The final parameter overrides the method onErrorResponse() and passes VolleyError
                    //as a parameter
                    new Response.ErrorListener() {
                        @Override
                        // Handles errors that occur due to Volley
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley", "Error");
                        }
                    }
            );
            // Adds the JSON object request "obreq" to the request queue
            requestQueue.add(obreq);
        } else {
            //inform user for internet state
            Toast toast = Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            buttonCalculate.setText(R.string.no_internet);
            buttonCalculate.setEnabled(false);
            buttonAddToCompare.setEnabled(false);
        }


        // create spinners for designs, carats, ring profile,stones
        CreateSpinnerDesign();
        CreateSpinnerCarats();
        CreateWomanSpinnerProfile();
        CreateManSpinnerProfile();
        CreateSpinnerStones();
        weightTable();
        labourTable();
        // gold price and date textviews are invisible for now
        price_TextView.setVisibility(View.GONE);
        date_TextView.setVisibility(View.GONE);
        //button compare is invisible until add to compare is pressed
        buttonCompare.setVisibility(View.GONE);


        // Set OnClickItemListener on spinner for woman ring
        spinner_woman_design.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_woman_profiles.setEnabled(true);
                posW = parent.getSelectedItemPosition();
                //for relative woman and man designs - automatic man design selection
                if (posW==53 || posW ==58  || posW ==60 || posW ==62 || posW ==64|| posW ==68|| posW ==71|| posW ==73|| posW ==77|| posW ==86|| posW ==89|| posW ==92|| posW ==94|| posW ==98|| posW ==100|| posW ==102|| posW ==104|| posW ==107|| posW ==109|| posW ==111|| posW ==113|| posW ==115|| posW ==117|| posW ==119|| posW ==121|| posW ==124|| posW ==128|| posW ==135|| posW ==138|| posW ==140|| posW ==142|| posW ==148|| posW ==155|| posW ==159|| posW ==165|| posW ==170|| posW ==183|| posW ==185|| posW ==187|| posW ==189|| posW ==191|| posW ==193 || posW ==195 || posW ==197 || posW ==199 || posW ==201 || posW ==203 || posW ==205 || posW ==227 || posW ==238 || posW ==240 || posW ==242 || posW ==244  || posW ==278 || posW ==280 || posW ==282 || posW ==284 || posW ==286 || posW ==288 || posW ==290 || posW ==292 || posW ==294 || posW ==296 || posW ==298 || posW ==300  || posW ==302 || posW ==304 || posW ==306 || posW ==308 || posW ==310 || posW ==312 || posW ==314 || posW ==327|| posW ==334 || posW ==335 || posW ==337 || posW ==339 || posW ==341 || posW ==343 || posW ==345 || posW ==347 || posW ==349 || posW ==351 || posW ==353 || posW ==355 || posW ==357 || posW ==359 || posW ==361|| posW ==371   ) {
                    spinner_man_design.setSelection(posW + 1);
                }else{
                    spinner_man_design.setSelection(posW);
                }

                // testPosition.setText(String.valueOf(posW));
                // testPosition.setText(weightTable().get(posW).toString());
                labourCostW = labourTable().get(posW);
                ringImageWoman.setImageResource(imageTable().get(posW));
                //Log.v(LOG_TAG, "imageTable().get(posW) =  " + imageTable().get(posW) );
                // available color for woman design
                femaleColor.setText("" + colorTable().get(posW));
                // testlabourCost.setText("" + labourCostW);
                // for designs with normal profile
                wds = parent.getSelectedItem().toString();
                CreateWomanSpinnerProfile();
                if (wds == "31Α - 4.5mm" || wds == "44Α - 5mm" || wds == "51Α - 4mm" || wds == "52Α - 3.5mm" || wds == "57Α - 4mm" || wds == "60Α - 4mm" || wds == "61Α - 3.5mm" || wds == "71Α - 4.5mm" || wds == "73Α - 4.5mm" || wds == "74Α - 4.5mm" || wds == "75Α - 4.5mm" || wds == "76Α - 4.5mm" || wds == "79Α - 4.5mm" || wds == "80Α - 4.5mm" || wds == "95Α - 5mm" || wds == "121Β - 4.5mm" || wds == "132Α - 4.8mm" || wds == "145Α - 5mm" || wds == "147Α - 4.5mm" || wds == "149Α - 4.5mm" || wds == "160Α - 5mm" || wds == "164Α - 5mm" || wds == "166Α - 4.5mm" || wds == "174Α - 4.3mm" || wds == "191Α - 5.5mm" || wds == "191Α - Α - 5.5mm" || wds == "210Α - 4mm" || wds == "224Α - 5.5mm"|| wds == "226Α - 5mm"|| wds == "226Α - Α - 5mm") {
                    Toast.makeText(MainActivity.this, R.string.just_normal_profile, Toast.LENGTH_SHORT).show();
                    spinner_woman_profiles.setSelection(1);
                    spinner_woman_profiles.setEnabled(false);
                }else if (wds =="247Γ - 5mm" || wds == "274Γ - 5mm" || wds =="247Γ - Α - 5mm" || wds == "274Γ - Α - 5mm"){
                    Toast.makeText(MainActivity.this, R.string.just_high_profile, Toast.LENGTH_SHORT).show();
                    spinner_woman_profiles.setSelection(2);
                    spinner_woman_profiles.setEnabled(false);
                } else if (wds == "234Α - 5mm"  || wds == "246Α - 5.5mm" || wds =="305Γ - 3mm" ) {
                    Toast.makeText(MainActivity.this, R.string.no_low_profile, Toast.LENGTH_SHORT).show();
                    spinner_woman_profiles.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //resultNumber = getString(R.string.spinner_default_value);
            }
        });




        // Set OnClickItemListener on spinner for man ring
        spinner_man_design.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_man_profiles.setEnabled(true);
                posM = parent.getSelectedItemPosition();
                labourCostM = labourTable().get(posM);
                // image man ring
                ringImageMan.setImageResource(imageTable().get(posM));
                // set color for man ring
                maleColor.setText("" + colorTable().get(posM));
                // testPosition.setText(String.valueOf(posM));
                // testPosition.setText(weightTable().get(posM).toString());
                // testlabourCost.setText(labourTable().get(posM).toString());
                // for designs with normal profile
                mds = parent.getSelectedItem().toString();
                CreateManSpinnerProfile();
                if (mds == "31Α - 4.5mm" || mds == "44Α - 5mm" || mds == "51Α - 4mm" || mds == "52Α - 3.5mm" || mds == "57Α - 4mm" || mds == "60Α - 4mm" || mds == "61Α - 3.5mm" || mds == "71Α - 4.5mm" || mds == "73Α - 4.5mm" || mds == "74Α - 4.5mm" || mds == "75Α - 4.5mm" || mds == "76Α - 4.5mm" || mds == "79Α - 4.5mm" || mds == "80Α - 4.5mm" || mds == "95Α - 5mm" || mds == "121Β - 4.5mm" || mds == "132Α - 4.8mm" || mds == "145Α - 5mm" || mds == "147Α - 4.5mm" || mds == "149Α - 4.5mm" || mds == "160Α - 5mm" || mds == "164Α - 5mm" || mds == "166Α - 4.5mm" || mds == "174Α - 4.3mm" || mds == "191Α - 5.5mm" || mds == "191Α - Α - 5.5mm" || mds == "210Α - 4mm" || mds == "224Α - 5.5mm"|| mds == "226Α - 5mm"|| mds == "226Α - Α - 5mm") {
                    Toast.makeText(MainActivity.this, R.string.just_normal_profile, Toast.LENGTH_SHORT).show();
                    spinner_man_profiles.setSelection(1);
                    spinner_man_profiles.setEnabled(false);
                }else if (mds =="247Γ - Α - 5mm" || mds == "274Γ - Α - 5mm"){
                    Toast.makeText(MainActivity.this, R.string.just_high_profile, Toast.LENGTH_SHORT).show();
                    spinner_man_profiles.setSelection(2);
                    spinner_man_profiles.setEnabled(false);
                } else if (mds == "234Α - Α - 5mm" || mds == "246Α - Α - 5.5mm") {
                    Toast.makeText(MainActivity.this, R.string.no_low_profile, Toast.LENGTH_SHORT).show();
                    spinner_man_profiles.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //resultNumber = getString(R.string.spinner_default_value);
            }
        });


        // Set OnClickItemListener on spinner for woman carats
        spinner_woman_carats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String woman_carats_selection = (String) parent.getSelectedItem();
                int woman_carats_selection = parent.getSelectedItemPosition();
                switch (woman_carats_selection) {
                    case 0:
                        woman_multiplier_carats = 0.366;
                        labourCostW = labourTable().get(posW);
                        weightCaratsMultiplierWoman = 0.84;
                        break;
                    case 1:
                        woman_multiplier_carats = 0.644;
                        labourCostW = labourTable().get(posW);
                        weightCaratsMultiplierWoman = 1;
                        break;
                    case 2:
                        woman_multiplier_carats = 0.825;
                        // testlabourCost.setText(""+(labourTable().get(pos)+ 5));
                        labourCostW = labourTable().get(posW) + 5;
                        weightCaratsMultiplierWoman = 1.16;
                        break;
                }
                // testcaratsmultiplier.setText(""+ woman_multiplier_carats);
                // testlabourCost.setText(""+labourCostW);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //resultNumber = getString(R.string.spinner_default_value);
                parent.setSelection(1);
            }
        });

        // Set OnClickItemListener on spinner for man carats
        spinner_man_carats.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int man_carats_selection = parent.getSelectedItemPosition();
                switch (man_carats_selection) {
                    case 0:
                        man_multiplier_carats = 0.366;
                        labourCostM = labourTable().get(posM);
                        weightCaratsMultiplierMan = 0.84;
                        break;
                    case 1:
                        man_multiplier_carats = 0.644;
                        labourCostM = labourTable().get(posM);
                        weightCaratsMultiplierMan = 1;
                        break;
                    case 2:
                        man_multiplier_carats = 0.825;
                        labourCostM = labourTable().get(posM) + 5;
                        weightCaratsMultiplierMan = 1.16;

                        break;
                }
                // testcaratsmultiplier.setText(""+ man_multiplier_carats);
                // testlabourCost.setText(""+labourCostM);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //resultNumber = getString(R.string.spinner_default_value);
                parent.setSelection(1);

            }
        });

        // Set OnClickItemListener on spinner for woman ring profile
        spinner_woman_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int woman_profile_selection = parent.getSelectedItemPosition();
                if (spinner_woman_profiles.getAdapter().getCount() > 2) {
                    switch (woman_profile_selection) {
                        case 0:
                            woman_multiplier_profile = 0.82;
                            break;
                        case 1:
                            woman_multiplier_profile = 1;
                            break;
                        case 2:
                            woman_multiplier_profile = 1.19;
                            break;
                    }
                    // testprofilemultiplier.setText(""+ woman_multiplier_profile);
                }else {
                    switch (woman_profile_selection) {
                        case 0:
                            woman_multiplier_profile = 1;
                            break;
                        case 1:
                            woman_multiplier_profile = 1.19;
                            break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //resultNumber = getString(R.string.spinner_default_value);
                parent.setSelection(1);
            }
        });

        // Set OnClickItemListener on spinner for man ring profile
        spinner_man_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int man_profile_selection = parent.getSelectedItemPosition();
                if (spinner_man_profiles.getAdapter().getCount() > 2) {
                    switch (man_profile_selection) {
                        case 0:
                            man_multiplier_profile = 0.82;
                            break;
                        case 1:
                            man_multiplier_profile = 1;
                            break;
                        case 2:
                            man_multiplier_profile = 1.19;
                            break;
                    }
                    // testprofilemultiplier.setText(""+ woman_multiplier_profile);
                }else {
                    switch (man_profile_selection) {
                        case 0:
                            man_multiplier_profile = 1;
                            break;
                        case 1:
                            man_multiplier_profile = 1.19;
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //resultNumber = getString(R.string.spinner_default_value);
                parent.setSelection(1);
            }
        });

        //calculate order
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goldPricePerGrammar = (Double.parseDouble(price_TextView.getText().toString()) / 1000);



                Log.v(LOG_TAG, "gold price: " + goldPricePerGrammar + "\n" + "woman multiplier carats:" + woman_multiplier_carats + "\n" + "weightcarats multiplier woman:" + weightCaratsMultiplierWoman + "\n" + "woman multiplier profile:" + woman_multiplier_profile + "\n" + "weight:" + weightTable().get(posW) + "\n" + "labour cost:" + labourCostW + "\n"+ "positionWOMAN:"+ posW + "\n"+ "fpa_percentage: " + FPA_percentage + "\n"+ "NumFPA:" + NumFPA);
                Log.v(LOG_TAG, "gold price: " + goldPricePerGrammar + "\n" + "man multiplier carats:" + man_multiplier_carats + "\n" + "weightcarats multiplier man:" + weightCaratsMultiplierMan + "\n" + "man multiplier profile:" + man_multiplier_profile + "\n" + "weight:" + weightTable().get(posM) + "\n" + "labour cost:" + labourCostM + "\n"+ "positionMAN:"+ posM);
                double womanStoneValue;
                double manStoneValue;
                double extraWeightMan;
                // no design is selected for stone value

                if (posW == 0) {
                    womanStoneValue = 0;
                    womanRingPriceWithVAT = 0;
                } else {
                    womanStoneValue = 12 * 2.5 * (Integer.parseInt(spinner_woman_stones.getSelectedItem().toString()));
                    womanRingPriceNoVAT = (goldPricePerGrammar * woman_multiplier_carats) * (weightCaratsMultiplierWoman * woman_multiplier_profile * weightTable().get(posW)) + labourCostW;
                    //NumFPA = Integer.parseInt(FPA_percentage.toString());
                    womanRingPriceWithVAT = womanRingPriceNoVAT *  (1 + (NumFPA/100));
                }


                // woman_ring_price_no_vat.setText(getString(R.string.price_no_vat) + " " + String.format("%.0f", womanRingPriceNoVAT) + " €");
                // woman_ring_price_with_vat.setText(getString(R.string.price_with_vat) + " " + String.format("%.0f", womanRingPriceWithVAT) + " €");

                //Log.v(LOG_TAG, "gold price: " + goldPricePerGrammar + "\n" + "man multiplier carats:" + man_multiplier_carats + "\n" + "weightcarats multiplier man:" + weightCaratsMultiplierMan + "\n" + "man multiplier profile:" + man_multiplier_profile + "\n" + "weight:" + (weightTable().get(posM) + 0.5) + "\n" + "labour cost:" + labourCostM + "\n"+ "positionMAN:"+ posM);
                // no man design is selected
                if (posM == 0) {
                    extraWeightMan = 0;
                    manStoneValue =0;
                    manRingPriceWithVAT = 0;

                } else {
                    extraWeightMan = 0.5;
                    manStoneValue = 12 * 2.5 * (Integer.parseInt(spinner_man_stones.getSelectedItem().toString()));
                    manRingPriceNoVAT = ((goldPricePerGrammar * man_multiplier_carats) * ((weightCaratsMultiplierMan * man_multiplier_profile * (weightTable().get(posM)) + extraWeightMan)))  + labourCostM;
                    manRingPriceWithVAT = manRingPriceNoVAT * (1 + (NumFPA/100));
                }

                //  man_ring_price_no_vat.setText(getString(R.string.price_no_vat) + " " + String.format("%.0f", manRingPriceNoVAT) + " €");
                //  man_ring_price_with_vat.setText(getString(R.string.price_with_vat) + " " + String.format("%.0f", manRingPriceWithVAT) + " €");

                //  totalCost_noVAT = (womanRingPriceNoVAT + manRingPriceNoVAT) * 2;
                //  totalCostNoVat.setText(String.format("%.0f", totalCost_noVAT) + " €");
                if (posM != 0 && posW !=0){
                    totalShippingCost = 15;
                }else if (posM == 0 && posW == 0){
                    totalShippingCost = 0;
                }else{
                    totalShippingCost = 10;
                }
                //totalCost_withVAT = (womanRingPriceWithVAT + manRingPriceWithVAT) + ((womanRingPriceWithVAT + manRingPriceWithVAT)* Integer.valueOf(Profit_percentage)/100) + womanStoneValue + manStoneValue + totalShippingCost  ;
                totalCost_withVAT = (womanRingPriceWithVAT + manRingPriceWithVAT) + ((womanRingPriceWithVAT + manRingPriceWithVAT) * (NumProfit/100)) + womanStoneValue + manStoneValue + totalShippingCost  ;



                totalCostWithVat.setText(String.format("%.0f", totalCost_withVAT ) + " €");

            }
        });

        // add to compare list
        buttonAddToCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int WomanImageResourceID = imageTable().get(posW);
                int ManImageResourceID = imageTable().get(posM);
                String designWoman = spinner_woman_design.getSelectedItem().toString();
                String designMan = spinner_man_design.getSelectedItem().toString();
                String caratsWoman = spinner_woman_carats.getSelectedItem().toString();
                String caratsMan = spinner_man_carats.getSelectedItem().toString();
                String profileWoman = spinner_woman_profiles.getSelectedItem().toString();
                String profileMan = spinner_man_profiles.getSelectedItem().toString();
                int stonesWoman = (int) spinner_woman_stones.getSelectedItem();
                int stonesMan = (int) spinner_man_stones.getSelectedItem();
                // calculates order value if user forgets to press calculate button
                buttonCalculate.performClick();
                String pairCost = (String) totalCostWithVat.getText();
                buttonCompare.setVisibility(View.VISIBLE);

                //adds ring item to the arraylist which is the data source for the arrayadapter
                pairOfRingsItem pairOfRingsItem = new pairOfRingsItem(WomanImageResourceID, ManImageResourceID, designWoman, designMan, caratsWoman, caratsMan, profileWoman, profileMan,stonesWoman,stonesMan, pairCost);

                Toast toast = Toast.makeText(MainActivity.this, R.string.add_to_compare_complete, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //Log.v("pairOfRingsItem", "womanImageID " + WomanImageResourceID + " ManImageID " + ManImageResourceID + " design woman " + designWoman + " design man " +designMan + " carats woman " + caratsWoman + " carats man " + caratsMan + " profile woman " + profileWoman + " profile man " + profileMan + " stones woman " + stonesWoman +" stones man " +  stonesMan + " pairCost " + pairCost);
                pairOfRingsItems.add(pairOfRingsItem);

            }
        });


        buttonCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sends data to compare rings activity through intent  pairOfRingsItems to ringComparelist
                Intent myIntent = new Intent(MainActivity.this, CompareActivity.class);
                myIntent.putExtra("FILES_TO_SEND", pairOfRingsItems);
                MainActivity.this.startActivity(myIntent);
            }
        });





        // opens internet settings
        internetSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });

        //restart application
        resetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = getActivity(MainActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);

            }
        });

        android.support.v7.preference.PreferenceManager
                .setDefaultValues(this,
                        R.xml.settings_pref, true);

        // Read the settings from the shared preferences, put them into the
        // SettingsActivity, and display a toast.
        SharedPreferences sharedPref =
                android.support.v7.preference.PreferenceManager
                        .getDefaultSharedPreferences(this);
        FPA_percentage = sharedPref.getString(SettingsActivity.KEY_PREF_FPA_VALUE,"24");
        Profit_percentage = sharedPref.getString(SettingsActivity.KEY_PREF_PROFIT_VALUE,"100");
        try {
            NumFPA= Integer.parseInt(FPA_percentage);
            Log.i("",FPA_percentage +" is a number");
        } catch (NumberFormatException e) {
            Log.i("",FPA_percentage + " is not a number");
            Toast.makeText(this, R.string.FPAnotNumber, Toast.LENGTH_SHORT).show();
        }
        //NumFPA= Integer.parseInt(FPA_percentage);
        try {
            NumProfit= Integer.parseInt(Profit_percentage);
            Log.i("",Profit_percentage +" is a number");
        } catch (NumberFormatException e) {
            Log.i("",Profit_percentage + " is not a number");
            Toast.makeText(this, R.string.ProfitNotNumber, Toast.LENGTH_SHORT).show();
        }

        //NumProfit=Integer.parseInt(Profit_percentage);
        //show on MainActivity FPA and Profit percentage when leave from settings
        //Toast.makeText(this, FPA_percentage.toString(), Toast.LENGTH_SHORT).show();
       // Toast.makeText(this, Profit_percentage.toString(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Inflates the options menu and adds items to the menu.
     *
     * @param menu Options menu
     * @return True if menu is inflated.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar
        // if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    /**
     * Handles option menu selections and automatically handles clicks
     * on the Up button in the app bar.
     *
     * @param item Item in options menu
     * @return True if Settings is selected in the options menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // If option menu item is Settings, return true.
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,
                    SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public void CreateSpinnerDesign() {

        ArrayList<String> designs = new ArrayList<String>();
        designs.add(getString(R.string.chooseDesign));
        designs.add("1Γ - 2mm");
        designs.add("2Γ - 2.5mm");
        designs.add("3Γ - 3mm");
        designs.add("4Γ - 3.5mm");
        designs.add("5Γ - 4mm");
        designs.add("6Γ - 4.5mm");
        designs.add("7Γ - 5mm");
        designs.add("8Γ - 6mm");
        designs.add("9Γ - 7mm");
        designs.add("10Γ - 3mm");
        designs.add("11Γ - 3.7mm");
        designs.add("12Γ - 4.5mm");
        designs.add("14Γ - 3mm");
        designs.add("15Γ - 3.7mm");
        designs.add("16Γ - 4.5mm");
        designs.add("23Γ - 3.7mm");
        designs.add("26Γ - 4mm");
        designs.add("27Γ - 4mm");
        designs.add("31Α - 4.5mm");
        designs.add("33Γ - 4mm");
        designs.add("34Γ - 3mm");
        designs.add("41Γ - 4mm");
        designs.add("44Α - 5mm");
        designs.add("46Γ - 3mm");
        designs.add("48Γ - 4mm");
        designs.add("51Α - 4mm");
        designs.add("52Α - 3.5mm");
        designs.add("53Γ - 4.5mm");
        designs.add("57Α - 4mm");
        designs.add("60Α - 4mm");
        designs.add("61Α - 3.5mm");
        designs.add("63Γ - 4mm");
        designs.add("71Α - 4.5mm");
        designs.add("73Α - 4.5mm");
        designs.add("74Α - 4.5mm");
        designs.add("75Α - 4.5mm");
        designs.add("76Α - 4.5mm");
        designs.add("79Α - 4.5mm");
        designs.add("80Α - 4.5mm");
        designs.add("94Γ - 4mm");
        designs.add("95Α - 5mm");
        designs.add("97Α - 4.5mm");
        designs.add("111Γ - 5mm");
        designs.add("117Γ - 3.5mm");
        designs.add("121Β - 4.5mm");
        designs.add("130Α - 4.5mm");
        designs.add("132Α - 4.8mm");
        designs.add("134Α - 4mm");
        designs.add("140Α - 4.5mm");
        designs.add("145Α - 5mm");
        designs.add("147Α - 4.5mm");
        designs.add("149Α - 4.5mm");
        designs.add("153Γ - 4.5mm");
        designs.add("153Γ - Α - 4.5mm");
        designs.add("160Α - 5mm");
        designs.add("164Α - 5mm");
        designs.add("166Α - 4.5mm");
        designs.add("170Γ - 4.5mm");
        designs.add("170Γ - Α - 4.5mm");
        designs.add("171Γ - 4mm");
        designs.add("171Γ - Α - 4mm");
        designs.add("172Γ - 4mm");
        designs.add("172Γ - Α - 4mm");
        designs.add("173Α - 4mm");
        designs.add("173Α - Α - 4mm");
        designs.add("174Α - 4.3mm");
        designs.add("179Α - 4.5mm");
        designs.add("180Α - 4.5mm");
        designs.add("180Α - Α - 4.5mm");
        designs.add("182Γ - 5mm");
        designs.add("184Γ - 3.5mm");
        designs.add("184Γ - Α - 3.5mm");
        designs.add("185Γ - 3.5mm");
        designs.add("185Γ - Α - 3.5mm");
        designs.add("188Γ - 4.5mm");
        designs.add("189Γ - 4.5mm");
        designs.add("191Α - 5.5mm");
        designs.add("191Α - Α - 5.5mm");
        designs.add("198Γ - 4.5mm");
        designs.add("200Α - 5mm");
        designs.add("201Α - 5mm");
        designs.add("203Γ - 5mm");
        designs.add("208Α - 5mm");
        designs.add("210Α - 4mm");
        designs.add("211Γ - 4.5mm");
        designs.add("213Α - 5mm");
        designs.add("213Α - Α - 5mm");
        designs.add("214Α - 5mm");
        designs.add("215Α - 4mm");
        designs.add("215Α -Α - 4mm");
        designs.add("224Α - 5.5mm");
        designs.add("225Α - 5mm");
        designs.add("225Α - Α - 5mm");
        designs.add("226Α - 5mm");
        designs.add("226Α - Α - 5mm");
        designs.add("227Γ - 4.5mm");
        designs.add("228Α - 5mm");
        designs.add("233Α - 5.5mm");
        designs.add("233Α - Α - 5.5mm");
        designs.add("234Α - 5mm");
        designs.add("234Α - Α - 5mm");
        designs.add("237Γ - 6.5mm");
        designs.add("237Γ - Α - 6.5mm");
        designs.add("238Γ - 4.5mm");
        designs.add("238Γ - Α - 4.5mm");
        designs.add("239Α - 7mm");
        designs.add("240Α - 5mm");
        designs.add("240Α - Α - 5mm");
        designs.add("241Γ - 5mm");
        designs.add("241Γ - Α - 5mm");
        designs.add("242Δ - 5mm");
        designs.add("242Δ - Α - 5mm");
        designs.add("243Δ - 4mm");
        designs.add("243Δ - Α - 4mm");
        designs.add("245Γ - 4.5mm");
        designs.add("245Γ - Α - 4.5mm");
        designs.add("246Α - 5.5mm");
        designs.add("246Α - Α - 5.5mm");
        designs.add("247Γ - 5mm");
        designs.add("247Γ - Α - 5mm");
        designs.add("248Δ - 6.5mm");
        designs.add("248Δ - Α - 6.5mm");
        designs.add("249Γ - 3mm");
        designs.add("251Α - 7mm");
        designs.add("251Α - Α - 7mm");
        designs.add("252Α - 5.2mm");
        designs.add("253Α - 5mm");
        designs.add("254Α - 4.5mm");
        designs.add("254Α - Α - 4.5mm");
        designs.add("264Α - 4mm");
        designs.add("265Γ - 5mm");
        designs.add("268Α - 4mm");
        designs.add("269Α - 4mm");
        designs.add("271Γ - 4mm");
        designs.add("274Γ - 5mm");
        designs.add("274Γ - Α - 5mm");
        designs.add("283Γ - 4mm");
        designs.add("291Α - 5mm");
        designs.add("291Α - Α - 5mm");
        designs.add("292Α - 5mm");
        designs.add("292Α - Α - 5mm");
        designs.add("293Γ - 4.5mm");
        designs.add("293Γ - Α - 4.5mm");
        designs.add("295Α - 4mm");
        designs.add("298Α - 4mm");
        designs.add("302Γ - 3.5mm");
        designs.add("303Γ - 3.5mm");
        designs.add("304Γ - 3mm");
        designs.add("304Γ - Α - 3mm");
        designs.add("305Γ - 3mm");
        designs.add("306Γ - 3.5mm");
        designs.add("307Γ - 4mm");
        designs.add("308Α - 3.5mm");
        designs.add("309Δ - 3mm");
        designs.add("311Α - 4.5mm");
        designs.add("311Α - Α - 4.5mm");
        designs.add("312Γ - 3.5mm");
        designs.add("313Γ - 3.5mm");
        designs.add("314Γ - 4.5mm");
        designs.add("314Γ - Α - 4.5mm");
        designs.add("315Γ - 3.5mm");
        designs.add("316Α - 3.5mm");
        designs.add("317Α - 4mm");
        designs.add("318Γ - 4mm");
        designs.add("319Γ - 4mm");
        designs.add("319Γ - Α - 4mm");
        designs.add("320Γ - 4mm");
        designs.add("321Α - 3.5mm");
        designs.add("322Α - 4mm");
        designs.add("323Γ - 4mm");
        designs.add("323Γ - Α - 4mm");
        designs.add("324Γ - 4mm");
        designs.add("325Γ - 4mm");
        designs.add("326Γ - 3.5mm");
        designs.add("327Γ - 4mm");
        designs.add("328Γ - 4mm");
        designs.add("329Γ - 3.5mm");
        designs.add("330Γ - 4.5mm");
        designs.add("331Γ - 4mm");
        designs.add("332Γ - 4.5mm");
        designs.add("333Α - 4.5mm");
        designs.add("334Γ - 4mm");
        designs.add("335Γ - 3.5mm");
        designs.add("335Γ - Α - 3.5mm");
        designs.add("336Γ - 4mm");
        designs.add("336Γ - Α - 4mm");
        designs.add("337Γ - 3.5mm");
        designs.add("337Γ - Α - 3.5mm");
        designs.add("338Γ - 3.mm");
        designs.add("338Γ - A - 3.mm");
        designs.add("339Γ - 3.mm");
        designs.add("339Γ - Α - 3mm");
        designs.add("340Γ - 3mm");
        designs.add("340Γ - Α - 3mm");
        designs.add("341Α - 3.5mm");
        designs.add("341Α - Α - 3.5mm");
        designs.add("342Γ - 3.5mm");
        designs.add("342Γ - Α - 3.5mm");
        designs.add("343Α - 3.5mm");
        designs.add("343Α - Α - 3.5mm");
        designs.add("344Α - 3.5mm");
        designs.add("344Α - Α - 3.5mm");
        designs.add("345Γ - 4mm");
        designs.add("345 - Α - 4mm");
        designs.add("346Γ - 3.5mm");
        designs.add("346Γ - A - 3.5mm");
        designs.add("347Α - 4mm");
        designs.add("348Γ - 3.5mm");
        designs.add("349Γ - 3.5mm");
        designs.add("350Γ - 3.5mm");
        designs.add("351Γ - 3.5mm");
        designs.add("352Α - 4mm");
        designs.add("353Γ - 3.5mm");
        designs.add("354Γ - 3.5mm");
        designs.add("355Γ - 3mm");
        designs.add("356Γ - 3.5mm");
        designs.add("357Γ - 3mm");
        designs.add("358Α - 3mm");
        designs.add("359Γ - 3mm");
        designs.add("360Γ - 3mm");
        designs.add("361Α - 4mm");
        designs.add("362Α - 3.5mm");
        designs.add("363Γ - 3.5mm");
        designs.add("364Α - 3.5mm");
        designs.add("365Α - 3.5mm");
        designs.add("366Γ - 3.5mm");
        designs.add("367Γ - 3.5mm");
        designs.add("367Γ - Α - 3.5mm");
        designs.add("368Γ - 3.5mm");
        designs.add("369Γ - 3.5mm");
        designs.add("370Γ - 3.5mm");
        designs.add("371Α - 3mm");
        designs.add("372Α - 3.5mm");
        designs.add("373Γ - 3.5mm");
        designs.add("374Α - 3.5mm");
        designs.add("375Γ - 2.5mm");
        designs.add("376Γ - 3mm");
        designs.add("377Γ - 3mm");
        designs.add("377Γ - Α - 3mm");
        designs.add("378Γ - 3mm");
        designs.add("378Γ - Α - 3mm");
        designs.add("379Γ - 3mm");
        designs.add("379Γ - Α - 3mm");
        designs.add("380Γ - 3.5mm");
        designs.add("380Γ - 3.5mm");
        designs.add("381Α - 3.5mm");
        designs.add("382Γ - 3mm");
        designs.add("383Γ - 3mm");
        designs.add("384Γ - 3.5mm");
        designs.add("385Γ - 3mm");
        designs.add("386Α - 3.5mm");
        designs.add("387Γ - 3.5mm");
        designs.add("388Γ - 3.5mm");
        designs.add("389Γ - 3.5mm");
        designs.add("390Γ - 3.5mm");
        designs.add("391Γ - 3.5mm");
        designs.add("392Γ - 3mm");
        designs.add("393Γ - 2.5mm");
        designs.add("394Γ - 3mm");
        designs.add("395Γ - 3mm");
        designs.add("396Γ - 3mm");
        designs.add("397Γ - 3mm");
        designs.add("398Γ - 3mm");
        designs.add("399Γ - 3mm");
        designs.add("400Γ - 3mm");
        designs.add("401Γ - 3.5mm");
        designs.add("402Γ - 3mm");
        designs.add("403Α - 1 - 3.5mm");
        designs.add("403Α - 2 - 3.5mm");
        designs.add("404Γ - 2.5mm");
        designs.add("405Α - 3mm");
        designs.add("406Γ - 3mm");
        designs.add("407Γ - 2.5mm");
        designs.add("408Α - 3mm");
        designs.add("409Γ - 2.5mm");
        designs.add("410Α - 3mm");
        designs.add("411Γ - 3mm");
        designs.add("412Γ - 3.5mm");
        designs.add("412Γ - Α - 3.5mm");
        designs.add("413Γ - 2.5mm");
        designs.add("413Γ - Α - 2.5mm");
        designs.add("414Γ - 3.5mm");
        designs.add("414Γ - Α - 3.5mm");
        designs.add("415Γ - 3mm");
        designs.add("415Γ - Α - 3mm");
        designs.add("416Γ - 2.5mm");
        designs.add("416Γ - Α - 2.5mm");
        designs.add("417Γ - 2.5mm");
        designs.add("417Γ - Α - 2.5mm");
        designs.add("418Γ - 3mm");
        designs.add("418Γ - Α - 3mm");
        designs.add("419Γ - 3.5mm");
        designs.add("419Γ - A - 3.5mm");
        designs.add("420Γ - 3.5mm");
        designs.add("420Γ - A - 3.5mm");
        designs.add("421Γ - 4mm");
        designs.add("421Γ - A - 4mm");
        designs.add("422Γ - 3mm");
        designs.add("422Γ - A - 3mm");
        designs.add("423Γ - 4mm");
        designs.add("423Γ - A - 4mm");
        designs.add("424Γ - 4mm");
        designs.add("424Γ - A - 4mm");
        designs.add("425Α - 3.5mm");
        designs.add("425Α - A - 3.5mm");
        designs.add("426Γ - 4mm");
        designs.add("426Γ - A - 4mm");
        designs.add("427Γ - 3.5mm");
        designs.add("427Γ -A - 3.5mm");
        designs.add("428Α - 3.5mm");
        designs.add("428Α - A - 3.5mm");
        designs.add("429Γ - 3.5mm");
        designs.add("429Γ - A - 3.5mm");
        designs.add("430Γ - 3.5mm");
        designs.add("430Γ - A - 3.5mm");
        designs.add("431Α - 3.5mm");
        designs.add("432Γ - 3mm");
        designs.add("433Γ - 3.5mm");
        designs.add("434Α - 3.5mm");
        designs.add("435Α - 3.5mm");
        designs.add("436Α - 3.5mm");
        designs.add("437Α - 3.5mm");
        designs.add("438Α - 3mm");
        designs.add("439Α - 3mm");
        designs.add("440Α - 4mm");
        designs.add("441Α - 4mm");
        designs.add("442Γ - 3.5mm");
        designs.add("442Γ - Α - 3.5mm");
        designs.add("443Α - 3.5mm");
        designs.add("444Α - 3.5mm");
        designs.add("445Γ - 3.5mm");
        designs.add("446Γ - 3mm");
        designs.add("447Γ - 3.5mm");
        designs.add("448Γ - 3.5mm");
        designs.add("449Γ - 3.5mm");
        designs.add("449Γ - Α - 3.5mm");
        designs.add("450Α - 4mm");
        designs.add("450Α - Α - 4mm");
        designs.add("451Α - 3.5mm");
        designs.add("451Α - Α - 3.5mm");
        designs.add("452Γ - 4mm");
        designs.add("452Γ - Α - 4mm");
        designs.add("453Γ - 4mm");
        designs.add("453Γ - Α - 4mm");
        designs.add("454Γ - 4mm");
        designs.add("454Γ - Α - 4mm");
        // New designs 2020
        designs.add("455Γ - 2mm");
        designs.add("455Γ - Α - 2mm");
        designs.add("456Γ - 2mm");
        designs.add("456Γ - Α - 2mm");
        designs.add("457Γ - 2mm");
        designs.add("457Γ - Α - 2mm");
        designs.add("458Γ - 2mm");
        designs.add("458Γ - Α - 2mm");
        designs.add("459Γ - 2mm");
        designs.add("459Γ - Α - 2mm");
        designs.add("460Γ - 2mm");
        designs.add("460Γ - Α - 2mm");
        designs.add("461Γ - 2mm");
        designs.add("461Γ - Α - 2mm");
        designs.add("462Γ - 2.5mm");
        designs.add("462Γ - Α - 2.5mm");
        designs.add("463Γ - 2mm");
        designs.add("464Γ - 2.5mm");
        designs.add("465Γ - 2mm");
        designs.add("466Γ - 3mm");
        designs.add("467Γ - 2.5mm");
        designs.add("468Γ - 2.5mm");
        designs.add("469Γ - 2.5mm");
        designs.add("470Γ - 2.5mm");
        designs.add("471Γ - 2mm");
        designs.add("471Γ - Α - 2mm");
        designs.add("472Γ - 2.5mm");
        designs.add("473Γ - 2mm");
        designs.add("474Γ - 2.5mm");
        designs.add("475Γ - 2mm");
        designs.add("476Γ - 2.5mm");
        designs.add("477Γ - 3mm");
        designs.add("478Γ - 2mm");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ArrayAdapter adapter;
       if (metrics.density < 2) {

            adapter = new ArrayAdapter(this, R.layout.spinner_layout, designs);
            adapter.setDropDownViewResource(R.layout.spinner_layout);


        }else {

           adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, designs);
           adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

       }

        spinner_woman_design.setAdapter(adapter);
        spinner_woman_design.setSelection(0);
        posW = 0;
        spinner_man_design.setAdapter(adapter);
        spinner_man_design.setSelection(0);
        posM = 0;


    }

    public void CreateSpinnerCarats() {

        ArrayList<String> carats = new ArrayList<String>();

        carats.add(getString(R.string.carats8K));
        carats.add(getString(R.string.carats14K));
        carats.add(getString(R.string.carats18K));

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ArrayAdapter adapter;
        if (metrics.density < 2) {

            adapter = new ArrayAdapter(this, R.layout.spinner_layout, carats);
            adapter.setDropDownViewResource(R.layout.spinner_layout);


        }else {

            adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, carats);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        }


        spinner_woman_carats.setAdapter(adapter);
        spinner_woman_carats.setSelection(1);
        spinner_man_carats.setAdapter(adapter);
        spinner_man_carats.setSelection(1);


    }

    public void CreateWomanSpinnerProfile() {

        ArrayList<String> profiles = new ArrayList<String>();

        if (wds != "234Α - 5mm" && wds!= "246Α - 5.5mm" && wds!="246Α - Α - 5.5mm" && wds!= "305Γ - 3mm" ){
            profiles.add(getString(R.string.profileLow));
            profiles.add(getString(R.string.profileNormal));
            profiles.add(getString(R.string.profileHigh));
        }else{
            profiles.add(getString(R.string.profileNormal));
            profiles.add(getString(R.string.profileHigh));
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ArrayAdapter adapter;
        if (metrics.density < 2) {

            adapter = new ArrayAdapter(this, R.layout.spinner_layout, profiles);
            adapter.setDropDownViewResource(R.layout.spinner_layout);


        }else {

            adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, profiles);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        }

        spinner_woman_profiles.setAdapter(adapter);
        spinner_woman_profiles.setSelection(1);


    }


    public void CreateManSpinnerProfile() {

        ArrayList<String> profiles = new ArrayList<String>();

        if (mds != "234Α - Α - 5mm" && mds!="246Α - Α - 5.5mm" && mds != "305Γ - 3mm" ){
            profiles.add(getString(R.string.profileLow));
            profiles.add(getString(R.string.profileNormal));
            profiles.add(getString(R.string.profileHigh));
        }else{
            profiles.add(getString(R.string.profileNormal));
            profiles.add(getString(R.string.profileHigh));
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ArrayAdapter adapter;
        if (metrics.density < 2) {

            adapter = new ArrayAdapter(this, R.layout.spinner_layout, profiles);
            adapter.setDropDownViewResource(R.layout.spinner_layout);


        }else {

            adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, profiles);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        }

        spinner_man_profiles.setAdapter(adapter);
        spinner_man_profiles.setSelection(1);


    }


    public void CreateSpinnerStones() {

        ArrayList<Integer> numberOfStones = new ArrayList<Integer>();

        numberOfStones.add(0);
        numberOfStones.add(1);
        numberOfStones.add(2);
        numberOfStones.add(3);
        numberOfStones.add(4);
        numberOfStones.add(5);
        numberOfStones.add(6);
        numberOfStones.add(7);
        numberOfStones.add(8);
        numberOfStones.add(9);
        numberOfStones.add(10);
        numberOfStones.add(11);
        numberOfStones.add(12);
        numberOfStones.add(13);
        numberOfStones.add(14);
        numberOfStones.add(15);
        numberOfStones.add(16);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ArrayAdapter adapter;
        if (metrics.density < 2) {

            adapter = new ArrayAdapter(this, R.layout.spinner_layout, numberOfStones);
            adapter.setDropDownViewResource(R.layout.spinner_layout);


        }else {

            adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, numberOfStones);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        }

        spinner_woman_stones.setAdapter(adapter);
        spinner_woman_stones.setSelection(0);
        spinner_man_stones.setAdapter(adapter);
        spinner_man_stones.setSelection(0);




    }

    public ArrayList<Double> weightTable() {
        ArrayList<Double> weights = new ArrayList<Double>();
        weights.add(0.00);
        weights.add(2.00);
        weights.add(2.50);
        weights.add(3.00);
        weights.add(3.50);
        weights.add(4.10);
        weights.add(4.50);
        weights.add(5.00);
        weights.add(6.00);
        weights.add(7.00);
        weights.add(3.30);
        weights.add(4.10);
        weights.add(5.00);
        weights.add(3.00);
        weights.add(3.70);
        weights.add(4.50);
        weights.add(3.60);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(5.60);
        weights.add(4.30);
        weights.add(3.10);
        weights.add(3.80);
        weights.add(5.60);
        weights.add(3.30);
        weights.add(4.40);
        weights.add(4.70);
        weights.add(4.00);
        weights.add(4.50);
        weights.add(4.80);
        weights.add(4.50);
        weights.add(4.20);
        weights.add(4.00);
        weights.add(5.10);
        weights.add(5.40);
        weights.add(5.40);
        weights.add(5.40);
        weights.add(5.40);
        weights.add(5.40);
        weights.add(5.50);
        weights.add(4.00);
        weights.add(6.00);
        weights.add(4.40);
        weights.add(5.50);
        weights.add(3.60);
        weights.add(5.40);
        weights.add(4.40);
        weights.add(5.70);
        weights.add(4.20);
        weights.add(4.90);
        weights.add(5.90);
        weights.add(5.30);
        weights.add(5.10);
        weights.add(4.70);
        weights.add(4.70);
        weights.add(5.40);
        weights.add(5.60);
        weights.add(5.70);
        weights.add(4.90);
        weights.add(4.90);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(5.20);
        weights.add(4.40);
        weights.add(4.70);
        weights.add(4.70);
        weights.add(5.30);
        weights.add(3.50);
        weights.add(3.50);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(4.60);
        weights.add(4.60);
        weights.add(6.40);
        weights.add(6.40);
        weights.add(4.90);
        weights.add(5.50);
        weights.add(5.50);
        weights.add(5.30);
        weights.add(5.50);
        weights.add(5.10);
        weights.add(4.60);
        weights.add(5.50);
        weights.add(5.50);
        weights.add(5.50);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(6.80);
        weights.add(5.20);
        weights.add(5.20);
        weights.add(5.50);
        weights.add(5.50);
        weights.add(4.90);
        weights.add(5.50);
        weights.add(5.80);
        weights.add(5.80);
        weights.add(5.20);
        weights.add(5.20);
        weights.add(8.40);
        weights.add(8.40);
        weights.add(4.90);
        weights.add(4.90);
        weights.add(7.70);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(5.60);
        weights.add(5.60);
        weights.add(5.50);
        weights.add(5.50);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(4.70);
        weights.add(4.70);
        weights.add(5.90);
        weights.add(5.90);
        weights.add(9.70);
        weights.add(9.70);
        weights.add(7.50);
        weights.add(7.50);
        weights.add(3.30);
        weights.add(7.40);
        weights.add(7.40);
        weights.add(5.20);
        weights.add(5.70);
        weights.add(4.90);
        weights.add(4.90);
        weights.add(3.90);
        weights.add(5.50);
        weights.add(4.50);
        weights.add(4.50);
        weights.add(4.30);
        weights.add(10.00);
        weights.add(10.00);
        weights.add(4.00);
        weights.add(5.50);
        weights.add(5.50);
        weights.add(5.50);
        weights.add(5.50);
        weights.add(4.90);
        weights.add(4.90);
        weights.add(3.80);
        weights.add(4.70);
        weights.add(3.80);
        weights.add(3.70);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(2.70);
        weights.add(3.70);
        weights.add(4.00);
        weights.add(3.70);
        weights.add(2.90);
        weights.add(4.90);
        weights.add(4.90);
        weights.add(4.10);
        weights.add(4.20);
        weights.add(4.90);
        weights.add(4.90);
        weights.add(3.70);
        weights.add(3.60);
        weights.add(4.20);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(4.30);
        weights.add(3.70);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(4.40);
        weights.add(3.70);
        weights.add(4.40);
        weights.add(4.40);
        weights.add(3.70);
        weights.add(4.80);
        weights.add(4.40);
        weights.add(4.80);
        weights.add(4.80);
        weights.add(4.30);
        weights.add(3.70);
        weights.add(3.700);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(3.70);
        weights.add(3.70);
        weights.add(3.20);
        weights.add(3.20);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(4.30);
        weights.add(4.20);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(4.30);
        weights.add(3.90);
        weights.add(3.90);
        weights.add(3.70);
        weights.add(3.80);
        weights.add(4.40);
        weights.add(3.60);
        weights.add(3.80);
        weights.add(3.20);
        weights.add(3.70);
        weights.add(3.20);
        weights.add(3.30);
        weights.add(3.20);
        weights.add(3.20);
        weights.add(4.30);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.50);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.70);
        weights.add(3.70);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.20);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.70);
        weights.add(2.70);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.50);
        weights.add(2.90);
        weights.add(2.90);
        weights.add(3.70);
        weights.add(3.00);
        weights.add(3.70);
        weights.add(3.40);
        weights.add(3.80);
        weights.add(3.70);
        weights.add(3.80);
        weights.add(3.70);
        weights.add(3.20);
        weights.add(2.70);
        weights.add(3.20);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.80);
        weights.add(3.30);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(2.70);
        weights.add(3.30);
        weights.add(3.20);
        weights.add(2.70);
        weights.add(3.30);
        weights.add(2.70);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(3.70);
        weights.add(3.70);
        weights.add(2.70);
        weights.add(2.70);
        weights.add(3.70);
        weights.add(3.70);
        weights.add(3.20);
        weights.add(3.20);
        weights.add(2.70);
        weights.add(2.70);
        weights.add(2.60);
        weights.add(2.60);
        weights.add(3.00);
        weights.add(3.00);
        weights.add(3.70);
        weights.add(3.70);
        weights.add(3.70);
        weights.add(3.70);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(3.20);
        weights.add(3.20);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(3.90);
        weights.add(3.90);
        weights.add(3.50);
        weights.add(3.50);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(3.60);
        weights.add(3.60);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.20);
        weights.add(3.70);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.60);
        weights.add(3.80);
        weights.add(3.30);
        weights.add(3.30);
        weights.add(4.40);
        weights.add(4.30);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.10);
        weights.add(3.50);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(3.80);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(3.70);
        weights.add(3.70);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(4.30);
        weights.add(4.40);
        weights.add(4.40);
        //New Designs 2020
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.00);
        weights.add(1.90);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.60);
        weights.add(2.50);
        weights.add(2.10);
        weights.add(2.60);
        weights.add(2.10);
        weights.add(2.60);
        weights.add(2.50);
        weights.add(2.60);
        weights.add(2.60);
        weights.add(2.60);
        weights.add(2.10);
        weights.add(2.10);
        weights.add(2.60);
        weights.add(2.10);
        weights.add(2.60);
        weights.add(2.10);
        weights.add(2.60);
        weights.add(3.00);
        weights.add(2.00);


        return weights;
    }

    public ArrayList<Integer> labourTable() {
        ArrayList<Integer> labourCosts = new ArrayList<Integer>();
        labourCosts.add(0);
        labourCosts.add(15);
        labourCosts.add(15);
        labourCosts.add(20);
        labourCosts.add(20);
        labourCosts.add(25);
        labourCosts.add(25);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(20);
        labourCosts.add(25);
        labourCosts.add(30);
        labourCosts.add(20);
        labourCosts.add(25);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(45);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(45);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(45);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(30);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(40);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(50);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(60);
        labourCosts.add(60);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(60);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(60);
        labourCosts.add(60);
        labourCosts.add(60);
        labourCosts.add(60);
        labourCosts.add(30);
        labourCosts.add(60);
        labourCosts.add(60);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(100);
        labourCosts.add(100);
        labourCosts.add(35);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(50);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(30);
        labourCosts.add(35);
        labourCosts.add(35);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(40);
        labourCosts.add(40);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
        labourCosts.add(45);
//New designs 2020
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);
        labourCosts.add(30);

        return labourCosts;
    }


    public static ArrayList<Integer> imageTable() {
        ArrayList<Integer> ringImages = new ArrayList<Integer>();
        ringImages.add(R.drawable.valauro_square250);
        ringImages.add(R.drawable.v001c);
        ringImages.add(R.drawable.v002c);
        ringImages.add(R.drawable.v003c);
        ringImages.add(R.drawable.v004c);
        ringImages.add(R.drawable.v005c);
        ringImages.add(R.drawable.v006c);
        ringImages.add(R.drawable.v007c);
        ringImages.add(R.drawable.v008c);
        ringImages.add(R.drawable.v009c);
        ringImages.add(R.drawable.v010c);
        ringImages.add(R.drawable.v011c);
        ringImages.add(R.drawable.v012c);
        ringImages.add(R.drawable.v014c);
        ringImages.add(R.drawable.v015c);
        ringImages.add(R.drawable.v016c);
        ringImages.add(R.drawable.v023c);
        ringImages.add(R.drawable.v026c);
        ringImages.add(R.drawable.v027c);
        ringImages.add(R.drawable.v031a);
        ringImages.add(R.drawable.v033c);
        ringImages.add(R.drawable.v034c);
        ringImages.add(R.drawable.v041c);
        ringImages.add(R.drawable.v044a);
        ringImages.add(R.drawable.v046c);
        ringImages.add(R.drawable.v048c);
        ringImages.add(R.drawable.v051a);
        ringImages.add(R.drawable.v052a);
        ringImages.add(R.drawable.v053c);
        ringImages.add(R.drawable.v057a);
        ringImages.add(R.drawable.v060a);
        ringImages.add(R.drawable.v061a);
        ringImages.add(R.drawable.v063c);
        ringImages.add(R.drawable.v071a);
        ringImages.add(R.drawable.v073a);
        ringImages.add(R.drawable.v074a);
        ringImages.add(R.drawable.v075a);
        ringImages.add(R.drawable.v076a);
        ringImages.add(R.drawable.v079a);
        ringImages.add(R.drawable.v080a);
        ringImages.add(R.drawable.v094c);
        ringImages.add(R.drawable.v095a);
        ringImages.add(R.drawable.v097a);
        ringImages.add(R.drawable.v111c);
        ringImages.add(R.drawable.v117c);
        ringImages.add(R.drawable.v121b);
        ringImages.add(R.drawable.v130a);
        ringImages.add(R.drawable.v132a);
        ringImages.add(R.drawable.v134a);
        ringImages.add(R.drawable.v140a);
        ringImages.add(R.drawable.v145a);
        ringImages.add(R.drawable.v147a);
        ringImages.add(R.drawable.v149a);
        ringImages.add(R.drawable.v153c);
        ringImages.add(R.drawable.v153c_a);
        ringImages.add(R.drawable.v160a);
        ringImages.add(R.drawable.v164a);
        ringImages.add(R.drawable.v166a);
        ringImages.add(R.drawable.v170c);
        ringImages.add(R.drawable.v170c_a);
        ringImages.add(R.drawable.v171c);
        ringImages.add(R.drawable.v171c_a);
        ringImages.add(R.drawable.v172c);
        ringImages.add(R.drawable.v172c_a);
        ringImages.add(R.drawable.v173a);
        ringImages.add(R.drawable.v173a_a);
        ringImages.add(R.drawable.v174a);
        ringImages.add(R.drawable.v179a);
        ringImages.add(R.drawable.v180a);
        ringImages.add(R.drawable.v180a_a);
        ringImages.add(R.drawable.v182c);
        ringImages.add(R.drawable.v184c);
        ringImages.add(R.drawable.v184c_a);
        ringImages.add(R.drawable.v185c);
        ringImages.add(R.drawable.v185c_a);
        ringImages.add(R.drawable.v188c);
        ringImages.add(R.drawable.v189c);
        ringImages.add(R.drawable.v191a);
        ringImages.add(R.drawable.v191a_a);
        ringImages.add(R.drawable.v198c);
        ringImages.add(R.drawable.v200a);
        ringImages.add(R.drawable.v201a);
        ringImages.add(R.drawable.v203c);
        ringImages.add(R.drawable.v208a);
        ringImages.add(R.drawable.v210a);
        ringImages.add(R.drawable.v211c);
        ringImages.add(R.drawable.v213a);
        ringImages.add(R.drawable.v213a_a);
        ringImages.add(R.drawable.v214a);
        ringImages.add(R.drawable.v215a);
        ringImages.add(R.drawable.v215a_a);
        ringImages.add(R.drawable.v224a);
        ringImages.add(R.drawable.v225a);
        ringImages.add(R.drawable.v225a_a);
        ringImages.add(R.drawable.v226a);
        ringImages.add(R.drawable.v226a_a);
        ringImages.add(R.drawable.v227c);
        ringImages.add(R.drawable.v228a);
        ringImages.add(R.drawable.v233a);
        ringImages.add(R.drawable.v233a_a);
        ringImages.add(R.drawable.v234a);
        ringImages.add(R.drawable.v234a_a);
        ringImages.add(R.drawable.v237c);
        ringImages.add(R.drawable.v237c_a);
        ringImages.add(R.drawable.v238c);
        ringImages.add(R.drawable.v238c_a);
        ringImages.add(R.drawable.v239d);
        ringImages.add(R.drawable.v240a);
        ringImages.add(R.drawable.v240a_a);
        ringImages.add(R.drawable.v241c);
        ringImages.add(R.drawable.v241c_a);
        ringImages.add(R.drawable.v242d);
        ringImages.add(R.drawable.v242d_a);
        ringImages.add(R.drawable.v243d);
        ringImages.add(R.drawable.v243d_a);
        ringImages.add(R.drawable.v245c);
        ringImages.add(R.drawable.v245c_a);
        ringImages.add(R.drawable.v246a);
        ringImages.add(R.drawable.v246a_a);
        ringImages.add(R.drawable.v247c);
        ringImages.add(R.drawable.v247c_a);
        ringImages.add(R.drawable.v248d);
        ringImages.add(R.drawable.v248d_a);
        ringImages.add(R.drawable.v249c);
        ringImages.add(R.drawable.v251a);
        ringImages.add(R.drawable.v251a_a);
        ringImages.add(R.drawable.v252a);
        ringImages.add(R.drawable.v253a);
        ringImages.add(R.drawable.v254a);
        ringImages.add(R.drawable.v254a_a);
        ringImages.add(R.drawable.v264a);
        ringImages.add(R.drawable.v265c);
        ringImages.add(R.drawable.v268a);
        ringImages.add(R.drawable.v269a);
        ringImages.add(R.drawable.v271c);
        ringImages.add(R.drawable.v274c);
        ringImages.add(R.drawable.v274c_a);
        ringImages.add(R.drawable.v283c);
        ringImages.add(R.drawable.v291a);
        ringImages.add(R.drawable.v291a_a);
        ringImages.add(R.drawable.v292a);
        ringImages.add(R.drawable.v292a_a);
        ringImages.add(R.drawable.v293c);
        ringImages.add(R.drawable.v293c_a);
        ringImages.add(R.drawable.v295d);
        ringImages.add(R.drawable.v298a);
        ringImages.add(R.drawable.v302c);
        ringImages.add(R.drawable.v303c);
        ringImages.add(R.drawable.v304c);
        ringImages.add(R.drawable.v304c_a);
        ringImages.add(R.drawable.v305c);
        ringImages.add(R.drawable.v306c);
        ringImages.add(R.drawable.v307c);
        ringImages.add(R.drawable.v308a);
        ringImages.add(R.drawable.v309d);
        ringImages.add(R.drawable.v311a);
        ringImages.add(R.drawable.v311a_a);
        ringImages.add(R.drawable.v312c);
        ringImages.add(R.drawable.v313c);
        ringImages.add(R.drawable.v314c);
        ringImages.add(R.drawable.v314c_a);
        ringImages.add(R.drawable.v315c);
        ringImages.add(R.drawable.v316a);
        ringImages.add(R.drawable.v317a);
        ringImages.add(R.drawable.v318c);
        ringImages.add(R.drawable.v319c);
        ringImages.add(R.drawable.v319c_a);
        ringImages.add(R.drawable.v320c);
        ringImages.add(R.drawable.v321a);
        ringImages.add(R.drawable.v322a);
        ringImages.add(R.drawable.v323c);
        ringImages.add(R.drawable.v323c_a);
        ringImages.add(R.drawable.v324c);
        ringImages.add(R.drawable.v325c);
        ringImages.add(R.drawable.v326c);
        ringImages.add(R.drawable.v327c);
        ringImages.add(R.drawable.v328c);
        ringImages.add(R.drawable.v329c);
        ringImages.add(R.drawable.v330c);
        ringImages.add(R.drawable.v331c);
        ringImages.add(R.drawable.v332c);
        ringImages.add(R.drawable.v333a);
        ringImages.add(R.drawable.v334c);
        ringImages.add(R.drawable.v335c);
        ringImages.add(R.drawable.v335c_a);
        ringImages.add(R.drawable.v336c);
        ringImages.add(R.drawable.v336c_a);
        ringImages.add(R.drawable.v337c);
        ringImages.add(R.drawable.v337c_a);
        ringImages.add(R.drawable.v338c);
        ringImages.add(R.drawable.v338c_a);
        ringImages.add(R.drawable.v339c);
        ringImages.add(R.drawable.v339c_a);
        ringImages.add(R.drawable.v340c);
        ringImages.add(R.drawable.v340c_a);
        ringImages.add(R.drawable.v341a);
        ringImages.add(R.drawable.v341a_a);
        ringImages.add(R.drawable.v342c);
        ringImages.add(R.drawable.v342c_a);
        ringImages.add(R.drawable.v343a);
        ringImages.add(R.drawable.v343a_a);
        ringImages.add(R.drawable.v344a);
        ringImages.add(R.drawable.v344a_a);
        ringImages.add(R.drawable.v345c);
        ringImages.add(R.drawable.v345c_a);
        ringImages.add(R.drawable.v346c);
        ringImages.add(R.drawable.v346c_a);
        ringImages.add(R.drawable.v347a);
        ringImages.add(R.drawable.v348c);
        ringImages.add(R.drawable.v349c);
        ringImages.add(R.drawable.v350c);
        ringImages.add(R.drawable.v351c);
        ringImages.add(R.drawable.v352a);
        ringImages.add(R.drawable.v353c);
        ringImages.add(R.drawable.v354c);
        ringImages.add(R.drawable.v355c);
        ringImages.add(R.drawable.v356c);
        ringImages.add(R.drawable.v357c);
        ringImages.add(R.drawable.v358a);
        ringImages.add(R.drawable.v359c);
        ringImages.add(R.drawable.v360c);
        ringImages.add(R.drawable.v361a);
        ringImages.add(R.drawable.v362a);
        ringImages.add(R.drawable.v363c);
        ringImages.add(R.drawable.v364a);
        ringImages.add(R.drawable.v365a);
        ringImages.add(R.drawable.v366c);
        ringImages.add(R.drawable.v367c);
        ringImages.add(R.drawable.v367c_a);
        ringImages.add(R.drawable.v368c);
        ringImages.add(R.drawable.v369c);
        ringImages.add(R.drawable.v370c);
        ringImages.add(R.drawable.v371a);
        ringImages.add(R.drawable.v372a);
        ringImages.add(R.drawable.v373c);
        ringImages.add(R.drawable.v374a);
        ringImages.add(R.drawable.v375c);
        ringImages.add(R.drawable.v376c);
        ringImages.add(R.drawable.v377c);
        ringImages.add(R.drawable.v377c_a);
        ringImages.add(R.drawable.v378c);
        ringImages.add(R.drawable.v378c_a);
        ringImages.add(R.drawable.v379c);
        ringImages.add(R.drawable.v379c_a);
        ringImages.add(R.drawable.v380c);
        ringImages.add(R.drawable.v380c_a);
        ringImages.add(R.drawable.v381a);
        ringImages.add(R.drawable.v382c);
        ringImages.add(R.drawable.v383c);
        ringImages.add(R.drawable.v384c);
        ringImages.add(R.drawable.v385c);
        ringImages.add(R.drawable.v386a);
        ringImages.add(R.drawable.v387c);
        ringImages.add(R.drawable.v388c);
        ringImages.add(R.drawable.v389c);
        ringImages.add(R.drawable.v390c);
        ringImages.add(R.drawable.v391c);
        ringImages.add(R.drawable.v392c);
        ringImages.add(R.drawable.v393c);
        ringImages.add(R.drawable.v394c);
        ringImages.add(R.drawable.v395c);
        ringImages.add(R.drawable.v396c);
        ringImages.add(R.drawable.v397c);
        ringImages.add(R.drawable.v398c);
        ringImages.add(R.drawable.v399c);
        ringImages.add(R.drawable.v400c);
        ringImages.add(R.drawable.v401c);
        ringImages.add(R.drawable.v402c);
        ringImages.add(R.drawable.v403);
        ringImages.add(R.drawable.v403);
        ringImages.add(R.drawable.v404c);
        ringImages.add(R.drawable.v405a);
        ringImages.add(R.drawable.v406c);
        ringImages.add(R.drawable.v407c);
        ringImages.add(R.drawable.v408a);
        ringImages.add(R.drawable.v409c);
        ringImages.add(R.drawable.v410a);
        ringImages.add(R.drawable.v411c);
        ringImages.add(R.drawable.v412c);
        ringImages.add(R.drawable.v412c_a);
        ringImages.add(R.drawable.v413c);
        ringImages.add(R.drawable.v413c_a);
        ringImages.add(R.drawable.v414c);
        ringImages.add(R.drawable.v414c_a);
        ringImages.add(R.drawable.v415c);
        ringImages.add(R.drawable.v415c_a);
        ringImages.add(R.drawable.v416c);
        ringImages.add(R.drawable.v416c_a);
        ringImages.add(R.drawable.v417c);
        ringImages.add(R.drawable.v417c_a);
        ringImages.add(R.drawable.v418c);
        ringImages.add(R.drawable.v418c_a);
        ringImages.add(R.drawable.v419c);
        ringImages.add(R.drawable.v419c_a);
        ringImages.add(R.drawable.v420c);
        ringImages.add(R.drawable.v420c_a);
        ringImages.add(R.drawable.v421c);
        ringImages.add(R.drawable.v421c_a);
        ringImages.add(R.drawable.v422c);
        ringImages.add(R.drawable.v422c_a);
        ringImages.add(R.drawable.v423c);
        ringImages.add(R.drawable.v423c_a);
        ringImages.add(R.drawable.v424c);
        ringImages.add(R.drawable.v424c_a);
        ringImages.add(R.drawable.v425a);
        ringImages.add(R.drawable.v425a_a);
        ringImages.add(R.drawable.v426c);
        ringImages.add(R.drawable.v426c_a);
        ringImages.add(R.drawable.v427c);
        ringImages.add(R.drawable.v427c_a);
        ringImages.add(R.drawable.v428a);
        ringImages.add(R.drawable.v428a_a);
        ringImages.add(R.drawable.v429c);
        ringImages.add(R.drawable.v429c_a);
        ringImages.add(R.drawable.v430c);
        ringImages.add(R.drawable.v430c_a);
        ringImages.add(R.drawable.v431a);
        ringImages.add(R.drawable.v432c);
        ringImages.add(R.drawable.v433c);
        ringImages.add(R.drawable.v434a);
        ringImages.add(R.drawable.v435);
        ringImages.add(R.drawable.v436a);
        ringImages.add(R.drawable.v437a);
        ringImages.add(R.drawable.v438a);
        ringImages.add(R.drawable.v439a);
        ringImages.add(R.drawable.v440a);
        ringImages.add(R.drawable.v441a);
        ringImages.add(R.drawable.v442c);
        ringImages.add(R.drawable.v442c_a);
        ringImages.add(R.drawable.v443a);
        ringImages.add(R.drawable.v444a);
        ringImages.add(R.drawable.v445c);
        ringImages.add(R.drawable.v446c);
        ringImages.add(R.drawable.v447c);
        ringImages.add(R.drawable.v448c);
        ringImages.add(R.drawable.v449c);
        ringImages.add(R.drawable.v449c_a);
        ringImages.add(R.drawable.v450a);
        ringImages.add(R.drawable.v450a_a);
        ringImages.add(R.drawable.v451a);
        ringImages.add(R.drawable.v451a_a);
        ringImages.add(R.drawable.v452c);
        ringImages.add(R.drawable.v452c_a);
        ringImages.add(R.drawable.v453c);
        ringImages.add(R.drawable.v453b_a);
        ringImages.add(R.drawable.v454c);
        ringImages.add(R.drawable.v454c_a);
        //New designs 2020
        ringImages.add(R.drawable.v455c);
        ringImages.add(R.drawable.v455c_a);
        ringImages.add(R.drawable.v456c);
        ringImages.add(R.drawable.v456c_a);
        ringImages.add(R.drawable.v457c);
        ringImages.add(R.drawable.v457c_a);
        ringImages.add(R.drawable.v458c);
        ringImages.add(R.drawable.v458c_a);
        ringImages.add(R.drawable.v459c);
        ringImages.add(R.drawable.v459c_a);
        ringImages.add(R.drawable.v460c);
        ringImages.add(R.drawable.v460c_a);
        ringImages.add(R.drawable.v461c);
        ringImages.add(R.drawable.v461c_a);
        ringImages.add(R.drawable.v462c);
        ringImages.add(R.drawable.v462c_a);
        ringImages.add(R.drawable.v463c);
        ringImages.add(R.drawable.v464c);
        ringImages.add(R.drawable.v465c);
        ringImages.add(R.drawable.v466c);
        ringImages.add(R.drawable.v467c);
        ringImages.add(R.drawable.v468c);
        ringImages.add(R.drawable.v469c);
        ringImages.add(R.drawable.v470c);
        ringImages.add(R.drawable.v471c);
        ringImages.add(R.drawable.v471c_a);
        ringImages.add(R.drawable.v472c);
        ringImages.add(R.drawable.v473c);
        ringImages.add(R.drawable.v474c);
        ringImages.add(R.drawable.v475c);
        ringImages.add(R.drawable.v476c);
        ringImages.add(R.drawable.v477c);
        ringImages.add(R.drawable.v478c);


        return ringImages;
    }

    public ArrayList<String> colorTable() {
        ArrayList<String> ringColors = new ArrayList<String>();
        ringColors.add(getString(R.string.nocolor));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.all_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.color_combination));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        //New designs 2020
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));
        ringColors.add(getString(R.string.three_colors));


        

        return ringColors;
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // resets every selection after 10 minutes of idle

        new CountDownTimer(600000, 1000){
            public void onTick(long millisUntilFinished){

            }
            public  void onFinish(){
                spinner_woman_design.setSelection(0);
                spinner_man_design.setSelection(0);
                spinner_man_carats.setSelection(1);
                spinner_woman_carats.setSelection(1);
                spinner_woman_profiles.setSelection(1);
                spinner_man_profiles.setSelection(1);
                spinner_woman_stones.setSelection(0);
                spinner_man_stones.setSelection(0);
                totalCostWithVat.setText("0");
            }
        }.start();

    }




    //Save current values when rotating screen
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putDouble("gold_price_per_grammar", goldPricePerGrammar);
        savedInstanceState.putDouble("woman_ring_price_no_vat", womanRingPriceNoVAT);
        savedInstanceState.putDouble("woman_ring_price_with_vat", womanRingPriceWithVAT);
        savedInstanceState.putDouble("man_ring_price_no_vat", manRingPriceNoVAT);
        savedInstanceState.putDouble("man_ring_price_with_vat", manRingPriceWithVAT);
        savedInstanceState.putDouble("total_cost_no_vat", totalCost_noVAT);
        savedInstanceState.putDouble("total_cost_with_vat", totalCost_withVAT);
        savedInstanceState.putInt("posW", posW);
        savedInstanceState.putDouble("posM", posM);



    }


    //Restore current values when rotating screen
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state from saved instance
        goldPricePerGrammar = savedInstanceState.getDouble("gold_price_per_grammar");
        womanRingPriceNoVAT = savedInstanceState.getDouble("woman_ring_price_no_vat");
        womanRingPriceWithVAT = savedInstanceState.getDouble("woman_ring_price_with_vat");
        manRingPriceNoVAT = savedInstanceState.getDouble("man_ring_price_no_vat");
        manRingPriceWithVAT = savedInstanceState.getDouble("man_ring_price_with_vat");
        //totalCost_noVAT = savedInstanceState.getDouble("total_cost_no_vat");
        totalCost_withVAT = savedInstanceState.getDouble("total_cost_with_vat");

        //  woman_ring_price_no_vat.setText(getString(R.string.price_no_vat) + " " + String.format("%.0f", womanRingPriceNoVAT) + " €");
        //  woman_ring_price_with_vat.setText(getString(R.string.price_with_vat) + " " + String.format("%.0f", womanRingPriceWithVAT) + " €");
        //  man_ring_price_no_vat.setText(getString(R.string.price_no_vat) + " " + String.format("%.0f", manRingPriceNoVAT) + " €");
        //  man_ring_price_with_vat.setText(getString(R.string.price_with_vat) + " " + String.format("%.0f", manRingPriceWithVAT) + " €");
        //  totalCostNoVat.setText(String.format("%.0f", totalCost_noVAT) + " €");
        totalCostWithVat.setText(String.format("%.0f", totalCost_withVAT) + " €");



    }

    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }
}
