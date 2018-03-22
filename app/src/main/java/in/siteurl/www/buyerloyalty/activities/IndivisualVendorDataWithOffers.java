package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.adapters.VendorOffersAdapter;
import in.siteurl.www.buyerloyalty.adapters.viewPagerAdapter;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.VendorProduct;


public class IndivisualVendorDataWithOffers extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String sessionid, uid, login_identity, vendorIndividialId, vendorhashvalue, vendorname;
    TextView nameofvndr, emailofvndr, phoneofvndr, idofvndr, addressofvndr, statustv;
    String str_phn;
    RelativeLayout vendorproduct_ll, vendordetails;
    ArrayList<VendorProduct> vendorProdarrayalllist = new ArrayList<VendorProduct>();
    ArrayList<JSONObject> rawvendorlist = new ArrayList<>(); //to send the individual object to next activity in the onClickListener in listView
    String gpslatitude, gpslongitude, thisaddress;
    TextView noOffers;
    VendorOffersAdapter vendorOffersAdapter;

    private RecyclerView recyclerView1;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    LinearLayout topLinearLayout;
    String vendorName;
    private SearchView searchView;
    VendorProduct vendorProduct;
    GridLayoutManager mGridLayoutManager;
    Dialog alertDialog;

    private SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indivisual_vendor_data_with_offers);
        alertDialog = new Dialog(this);

        //getting data from other activity uding getExtra and hasExtra
        if (getIntent().hasExtra("individialvenderid")) {
            vendorIndividialId = getIntent().getStringExtra("individialvenderid");

        } else if (getIntent().hasExtra("VendorId")) {
            vendorIndividialId = getIntent().getStringExtra("VendorId");
        }

        vendorhashvalue = getIntent().getStringExtra("HashValue");
        vendorname = getIntent().getStringExtra("vendorName");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //sharedPreferences to store the data
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        login_identity = loginpref.getString("loginname", null);
        editor = loginpref.edit();

        dialog = new SpotsDialog(IndivisualVendorDataWithOffers.this, R.style.Custom);
        dialog.dismiss();

        //setting recyclerView to gridlayout
        recyclerView1 = (RecyclerView) findViewById(R.id.vendor_offers);
        recyclerView1.setHasFixedSize(true);

        int columns = 2;
        recyclerView1.setLayoutManager(new GridLayoutManager(IndivisualVendorDataWithOffers.this, columns));

        // if orientation change the number of columns will change in grid layout with RecyclerView
        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView1.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView1.setLayoutManager(new GridLayoutManager(this, 3));
        }

       /* LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(mLayoutManager);*/

        topLinearLayout = findViewById(R.id.scroll);

        checkConnection();
        // getIndividualVendorOfferData();

        nameofvndr = (TextView) findViewById(R.id.svnder_nametv2);
        emailofvndr = (TextView) findViewById(R.id.svndr_emailtv1);
        phoneofvndr = (TextView) findViewById(R.id.vnder_contacttv61);
        idofvndr = (TextView) findViewById(R.id.vnder_idtv41);
        addressofvndr = (TextView) findViewById(R.id.svnder_addresstv5);
        statustv = (TextView) findViewById(R.id.stv11);
        noOffers = findViewById(R.id.noOffers1);

        vendorproduct_ll = (RelativeLayout) findViewById(R.id.product_ll1);
        vendorproduct_ll.setVisibility(View.VISIBLE);
        vendordetails = findViewById(R.id.vendorDetails);

        //collapsingToolbar
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        //adding off set change listener to hide and show the balance
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                    vendordetails.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(vendorName);
                    isShow = true;
                    vendordetails.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);

                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                    vendordetails.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });


    }


    //this is the method to get all the individual vendor offers
    public void getIndividualVendorOfferData() {

        StringRequest individualVendorOffers = new StringRequest(Request.Method.POST, Constants.vendoroffers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject offersData;

                        dialog.dismiss();

                        try {
                            offersData = new JSONObject(response);
                            String response_message = offersData.getString("Message");
                            String response_error = offersData.getString("Error");
                            String data = offersData.getString("data");
                            JSONObject offerObject = offersData.getJSONObject("data");

                            if (response_error.equals("true")) {
                                Toast.makeText(IndivisualVendorDataWithOffers.this, response_message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(IndivisualVendorDataWithOffers.this, LoginActivity.class);
                                startActivity(intent);
                            }

                            if (response_error.equals("false")) {
                                JSONArray jsonArray = offerObject.getJSONArray("offers");

                                if (data.length() > 0) {
                                    rawvendorlist.clear();
                                    vendorProdarrayalllist.clear();

                                    JSONObject vendorData = offerObject.getJSONObject("vendor_details");
                                    vendorName = vendorData.getString("name");
                                    String vendorEmail = vendorData.getString("email");
                                    String vendorAddress = vendorData.getString("address");
                                    thisaddress = vendorAddress;
                                    String vendorPhone = vendorData.getString("phone");
                                    String vendorHash = vendorData.getString("hash");
                                    String vendorGpss = vendorData.getString("gps_location");
                                    String vendorImage = vendorData.getString("store_image");
                                    String vendorStatus = vendorData.getString("status");
                                    str_phn = vendorPhone;
                                    nameofvndr.setText(vendorName);
                                    emailofvndr.setText(vendorEmail);
                                    phoneofvndr.setText(vendorPhone);
                                    addressofvndr.setText(vendorAddress);
                                    statustv.setText(vendorStatus);

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
                                        vendorProduct = new VendorProduct(offer_name,
                                                offer_description, offer_image, expiry_date, offer_price,
                                                terms_and_condtion, status, vendorName, vendorEmail, vendorAddress);
                                        vendorProdarrayalllist.add(vendorProduct);
                                    }


                                    JSONArray offersArray = offerObject.getJSONArray("other_offers");
                                    for (int i = 0; i < offersArray.length(); i++) {

                                        JSONObject offer_object = offersArray.getJSONObject(i);

                                        String offerId = offer_object.getString("offer_id");
                                        String vendorId = offer_object.getString("vendor_id");
                                        String offerName = offer_object.getString("offer_name");
                                        String offerDesc = offer_object.getString("offer_description");
                                        String offerImage = offer_object.getString("offer_image");
                                        String offerPrice = offer_object.getString("offer_price");
                                        String offerExpiryDate = offer_object.getString("expiry_date");
                                        String offerTerms = offer_object.getString("terms_and_condtion");
                                        String offerStatus = offer_object.getString("status");

                                        //  Horizontal scrollview

                                        /*LinearLayout li = new LinearLayout(getApplicationContext());
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        li.setOrientation(LinearLayout.VERTICAL);
                                        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                        LinearLayout.LayoutParams paramsnew = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


                                        params1.setMargins(30, 20, 30, 0);

                                        OfferName = new TextView(getApplicationContext());
                                        OfferName.setText("" + offerName);
                                        OfferName.setId(5);
                                        OfferName.setLayoutParams(paramsnew);
                                        OfferName.setTextColor(Color.parseColor("#000000"));
                                        OfferName.setGravity(Gravity.CENTER);

                                        OfferImage = new ImageView(getApplicationContext());
                                        int width = 300;
                                        int height = 150;
                                        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
                                        OfferImage.setLayoutParams(parms);

                                        RequestOptions requestOptions = new RequestOptions();
                                        requestOptions.placeholder(R.mipmap.ic_launcher);
                                        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                                        requestOptions.fitCenter();

                                        Glide.with(getApplicationContext()).load(offerImage)
                                                .thumbnail(0.5f)
                                                .apply(requestOptions)
                                                .into(OfferImage);

                                        OfferPrice = new TextView(getApplicationContext());
                                        OfferPrice.setText("" + offerPrice + " Rs/-");
                                        OfferPrice.setId(5);
                                        OfferPrice.setTextColor(Color.parseColor("#000000"));
                                        OfferPrice.setLayoutParams(paramsnew);
                                        OfferPrice.setGravity(Gravity.CENTER);

                                        li.addView(OfferName);
                                        li.addView(OfferImage);
                                        li.addView(OfferPrice);

                                        li.setLayoutParams(params1);
                                        topLinearLayout.addView(li);*/

                                    }

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
                                        }
                                    }

                                } else {

                                }

                            } else {
                                String responseMessage = offersData.getString("Message");
                                Toast.makeText(IndivisualVendorDataWithOffers.this, responseMessage, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (vendorProdarrayalllist.size() > 0) {
                            vendorOffersAdapter = new VendorOffersAdapter(IndivisualVendorDataWithOffers.this, vendorProdarrayalllist);
                            recyclerView1.setAdapter(vendorOffersAdapter);

                        } else {
                            noOffers.setVisibility(View.VISIBLE);
                            recyclerView1.setVisibility(View.GONE);
                            vendorproduct_ll.setVisibility(View.GONE);

                        }

                        //  vendorOffersAdapter.notifyDataSetChanged();

                        searchView = findViewById(R.id.searchVendorswithOffers);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String s) {
                                vendorOffersAdapter.getFilter().filter(s.toString());
                                return true;
                            }
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    Toast.makeText(IndivisualVendorDataWithOffers.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(IndivisualVendorDataWithOffers.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(IndivisualVendorDataWithOffers.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(IndivisualVendorDataWithOffers.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(IndivisualVendorDataWithOffers.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(IndivisualVendorDataWithOffers.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(IndivisualVendorDataWithOffers.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    //this is the method to call to vendor
    private void callToVendor() {
        if (!str_phn.isEmpty()) {
            Intent intent = new Intent(IndivisualVendorDataWithOffers.this, CalltoVendor.class);
            intent.putExtra("phonenumber", str_phn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent);
        }
    }


    //this is the method to get location of vendor
    public void lookMap(View view) {

        startActivity(new Intent(IndivisualVendorDataWithOffers.this, VendorMapActivity.class).
                putExtra("laty", gpslatitude).putExtra("longy", gpslongitude).
                putExtra("address", String.valueOf(thisaddress)).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }

    //menu Items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.call_vendor) {
            callToVendor();
        }
        if (id == R.id.QR_content) {

            startActivity(new Intent(IndivisualVendorDataWithOffers.this, ShopVendorsProduct.class).
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
        showDialog(isConnected);
    }


    private void showDialog(boolean isConnected) {

        if (isConnected) {
            dialog.show();
            getIndividualVendorOfferData();
            alertDialog.dismiss();

        } else {
            alertDialog.setContentView(R.layout.check_internet);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            Button button = alertDialog.findViewById(R.id.tryAgain);
            Button exit = alertDialog.findViewById(R.id.exit);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.exit(0);

                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    checkConnection();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onRefresh() {
        checkConnection();
        getIndividualVendorOfferData();

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        showDialog(isConnected);

    }
}
