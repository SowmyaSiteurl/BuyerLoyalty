package in.siteurl.www.buyerloyalty.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.adapters.MyVendorAdapter;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.Venders_Product;


public class IndividialVenderdata extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String sessionid, uid, login_identity, vendorIndividialId, vendorhashvalue, vendorname;
    private Toolbar mToolbar;
    private TextView toolbartitle;
    TextView nameofvndr, emailofvndr, phoneofvndr, idofvndr, addressofvndr, statustv;
    String str_phn;
    LinearLayout vendor_ll;
    RelativeLayout vendorproduct_ll;
    MyVendorAdapter vendorProddataAdapter;
    ArrayList<Venders_Product> vendorProdarrayalllist = new ArrayList<Venders_Product>();
    ListView listView;
    ArrayList<JSONObject> rawvendorlist = new ArrayList<>(); //to send the individual object to next activity in the onClickListener in listView
    String gpslatitude, gpslongitude, thisaddress;
    private SwipeRefreshLayout swipeRefreshLayout;
    TextView noOffers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individial_venderdata);

        //getting data
        vendorIndividialId = getIntent().getStringExtra("individialvenderid");
        vendorhashvalue = getIntent().getStringExtra("HashValue");
        vendorname = getIntent().getStringExtra("vendorName");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //sharedPreferences to store the data
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        login_identity = loginpref.getString("loginname", null);
        editor = loginpref.edit();

        //UI elements to get the ID
        listView = (ListView) findViewById(R.id.listView2);
        vendor_ll = (LinearLayout) findViewById(R.id.detail_of_vendor_ll);
        vendorproduct_ll = (RelativeLayout) findViewById(R.id.product_ll);
        vendorproduct_ll.setVisibility(View.VISIBLE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_vendor);
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getIndividualVendorOfferData();
                                    }
                                }
        );

        checkConnection();
        getIndividualVendorOfferData();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbartitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText("Vendor Details");

        nameofvndr = (TextView) findViewById(R.id.vnder_nametv2);
        emailofvndr = (TextView) findViewById(R.id.vndr_emailtv1);
        phoneofvndr = (TextView) findViewById(R.id.vnder_contacttv6);
        idofvndr = (TextView) findViewById(R.id.vnder_idtv4);
        addressofvndr = (TextView) findViewById(R.id.vnder_addresstv5);
        statustv = (TextView) findViewById(R.id.tv11);
        noOffers = findViewById(R.id.noOffers);

    }

    //this is the method to get all the individual vendor offers
    public void getIndividualVendorOfferData() {

        StringRequest individualVendorOffers = new StringRequest(Request.Method.POST, Constants.vendoroffers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject OffersData;

                        try {
                            OffersData = new JSONObject(response);
                            String response_error = OffersData.getString("Error");
                            String data = OffersData.getString("data");
                            JSONObject offerObject = OffersData.getJSONObject("data");

                            if (response_error.equals("false")) {
                                JSONArray jsonArray = offerObject.getJSONArray("offers");

                                if (data.length() > 0) {
                                    rawvendorlist.clear();
                                    vendorProdarrayalllist.clear();

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject data_object = jsonArray.getJSONObject(i);
                                        String offer_id = data_object.getString("offer_id");
                                        String vendor_id = data_object.getString("vendor_id");
                                        String offer_name = data_object.getString("offer_name");
                                        String offer_description = data_object.getString("offer_description");
                                        String offer_image = data_object.getString("offer_image");
                                        String offer_price = data_object.getString("offer_price");
                                        String updated_at = data_object.getString("updated_at");
                                        String expiry_date = data_object.getString("expiry_date");
                                        String terms_and_condtion = data_object.getString("terms_and_condtion");
                                        String status = data_object.getString("status");

                                        rawvendorlist.add(data_object);
                                        vendorProdarrayalllist.add(new Venders_Product(offer_id, vendor_id, offer_name,
                                                offer_description, offer_image, updated_at, expiry_date, offer_price,
                                                terms_and_condtion, status));
                                    }


                                    JSONObject vendorData = offerObject.getJSONObject("vendor_details");
                                    String vendorName = vendorData.getString("name");
                                    String vendorEmail = vendorData.getString("email");
                                    String vendorAddress = vendorData.getString("address");
                                    thisaddress = vendorAddress;
                                    String vendorPhone = vendorData.getString("phone");
                                    String vendorHash = vendorData.getString("hash");
                                    String vendorGpss = vendorData.getString("gps_location");
                                    String vendorImage = vendorData.getString("store_image");
                                    String status = vendorData.getString("status");
                                    str_phn = vendorPhone;
                                    nameofvndr.setText(vendorName);
                                    emailofvndr.setText(vendorEmail);
                                    phoneofvndr.setText(vendorPhone);
                                    addressofvndr.setText(vendorAddress);
                                    statustv.setText(status);

                                    if (vendorData.has("gps_location")) {
                                        String vendorGps = vendorData.getString("gps_location");
                                        if (!vendorGps.equals("") | !vendorGps.isEmpty()) {
                                            String animals = vendorGps;
                                            String[] animalsArray = animals.split(",");
                                            String latitude = (animalsArray[0]);
                                            gpslatitude = (latitude);
                                            String longitude = (animalsArray[1]);
                                            gpslongitude = (longitude);
                                        } else {
                                            gpslatitude = "12.2958";
                                            gpslongitude = "76.6394";
                                            thisaddress = "Showing Mysore";
                                            //  Toast.makeText(IndividialVenderdata.this, "Location not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    // Adding items to listview

                                    if (vendorProdarrayalllist.size() > 0) {
                                        vendorProddataAdapter = new MyVendorAdapter(getApplicationContext(), R.layout.allvendorproduct_list, vendorProdarrayalllist);
                                        listView.setAdapter(vendorProddataAdapter);
                                    } else {
                                        noOffers.setVisibility(View.VISIBLE);
                                        listView.setVisibility(View.GONE);

                                    }

                                    //enables filtering for the contents of the given ListView
                                    listView.setTextFilterEnabled(true);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        }
                                    });

                                    // Enabling Search Filter
                                    SearchView searchView = (SearchView) findViewById(R.id.search);
                                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String s) {
                                            vendorProddataAdapter.getFilter().filter(s.toString());
                                            return true;
                                        }
                                    });

                                } else {

                                }
                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                String responseMessage = OffersData.getString("Message");
                                Toast.makeText(IndividialVenderdata.this, responseMessage, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    Toast.makeText(IndividialVenderdata.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    Toast.makeText(IndividialVenderdata.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(IndividialVenderdata.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(IndividialVenderdata.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(IndividialVenderdata.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(IndividialVenderdata.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(IndividialVenderdata.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("vendor_id", vendorIndividialId);
                params.put("sid", sessionid);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };

        individualVendorOffers.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(individualVendorOffers);
    }

    private void callToVendor() {
        if (!str_phn.isEmpty()) {
            Intent intent = new Intent(IndividialVenderdata.this, CalltoVendor.class);
            intent.putExtra("phonenumber", str_phn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent);
        }
    }

    public void lookMap(View view) {

        startActivity(new Intent(IndividialVenderdata.this, VendorMapActivity.class).
                putExtra("laty", gpslatitude).putExtra("longy", gpslongitude).
                putExtra("address", String.valueOf(thisaddress)).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.call_vendor) {
            callToVendor();
        }
        if (id == R.id.QR_content) {

            startActivity(new Intent(IndividialVenderdata.this, ShopVendorsProduct.class).
                    putExtra("venderhashdetails", vendorhashvalue).
                    putExtra("vendorName", vendorname).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_vendor_offer, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //this is the method to check internet connection
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        //  showSnack(isConnected);
    }

    // snack bar
   /* private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }
        Snackbar snackbar = Snackbar.make(findViewById(R.id.listView2), message, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }*/

    @Override
    public void onRefresh() {
        getIndividualVendorOfferData();
    }

}
