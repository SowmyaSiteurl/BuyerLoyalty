package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import in.siteurl.www.buyerloyalty.Helper.updateHelper;
import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.adapters.MyCustomAdapter1;
import in.siteurl.www.buyerloyalty.adapters.viewPagerAdapter;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.OtherProduct;
import in.siteurl.www.buyerloyalty.core.PurchaseDetails;
import in.siteurl.www.buyerloyalty.core.Vendors;


public class HomePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, updateHelper.onUpdateCheckListener,
        ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uid, login_identity;

    String vnderuser_id, hash, response_error, response_message, name;
    private Toolbar mToolbar;
    private TextView toolbartitle;
    private DrawerLayout mMainDrawer;
    private ActionBarDrawerToggle mToogle;
    private NavigationView mNavigationView;
    ArrayList<Vendors> venderarrayalllist = new ArrayList<Vendors>();
    ArrayList<String> stringsvender = new ArrayList<>();
    ArrayList<String> hashofvender = new ArrayList<>();
    ArrayList<JSONObject> rawvendorlist = new ArrayList<>();
    Context context;

    private LinearLayout llSearch;
    String storeQRData;
    Dialog alertDialog;


    ViewPager pager;
    viewPagerAdapter viewPagerAdapter;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 2500;
    final long PERIOD_MS = 2500;
    ArrayList<OtherProduct> vendorotherProdarrayalllist = new ArrayList<OtherProduct>();
    ArrayList<JSONObject> rawvendorlist1 = new ArrayList<>();
    String vendorName, thisaddress, str_phn;
    Random r;

    private RecyclerView recyclerView1;
    MyCustomAdapter1 myCustomAdapter1;
    private SearchView searchView;
    TextView totalPoints, Ptss;
    JSONArray historyArray;
    ArrayList<JSONObject> historyList = new ArrayList<>();
    private ArrayList<PurchaseDetails> purchaseDetails = new ArrayList<>();
    int Points;
    String closing_balance;
    String walletamount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        alertDialog = new Dialog(this);
        DemoSyncJob.scheduleJob();

        this.context = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //shared preferences to save the data
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uid = sharedPreferences.getString("User-id", null);
        login_identity = sharedPreferences.getString("loginname", null);
        editor = sharedPreferences.edit();

        //UI elements to get the ID
        totalPoints = findViewById(R.id.Points);
        Ptss = findViewById(R.id.Pointss);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        totalPoints.setVisibility(View.VISIBLE);
        Ptss.setVisibility(View.VISIBLE);

        toolbartitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbartitle.setText("Trendz Loyalty");

        //navigation drawer
        mMainDrawer = (DrawerLayout) findViewById(R.id.loyaltydrawer);
        mToogle = new ActionBarDrawerToggle(this, mMainDrawer, R.string.open, R.string.close);
        mMainDrawer.addDrawerListener(mToogle);
        mToogle.syncState();

        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerviewIndivisualVendorOffer);
        recyclerView1.setHasFixedSize(true);

        //setting orientation for recyclerView gridlayout
        int columns = 2;
        recyclerView1.setLayoutManager(new GridLayoutManager(HomePageActivity.this, columns));

        // if orientation change the number of columns will change in grid layout with RecyclerView
        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView1.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView1.setLayoutManager(new GridLayoutManager(this, 3));
        }

        mNavigationView = (NavigationView) findViewById(R.id.navigationview);
        mNavigationView.setNavigationItemSelectedListener(this);

        checkConnection();

        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        pager = (ViewPager) findViewById(R.id.offerpager);

        updateHelper.with(this).onUpdateCheck(this).check();

    }


    //this is the method to display all the vendor details
    private void displayListView() {

        StringRequest vendorDetails = new StringRequest(Request.Method.POST, Constants.allvendors, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(response);
                    String response_error = jsonObject.getString("Error");
                    String response_message = jsonObject.getString("Message");

                    if (response_error.equals("true")) {
                        Toast.makeText(HomePageActivity.this, response_message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    if (response_error.equals("false")) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data.length() > 0) {

                            rawvendorlist.clear();
                            venderarrayalllist.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject data_object = data.getJSONObject(i);
                                vnderuser_id = data_object.getString("user_id");
                                name = data_object.getString("name");
                                String email = data_object.getString("email");
                                String phone = data_object.getString("phone");
                                String address = data_object.getString("address");
                                hash = data_object.getString("hash");
                                String store_image = data_object.getString("store_image");
                                stringsvender.add(vnderuser_id);
                                hashofvender.add(hash);

                                rawvendorlist.add(data_object);
                                venderarrayalllist.add(new Vendors(store_image, vnderuser_id, name, email, phone, address, hash));
                            }

                            OtherOffers();

                        } else {

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //setting adapter
                myCustomAdapter1 = new MyCustomAdapter1(HomePageActivity.this, venderarrayalllist);
                recyclerView1.setAdapter(myCustomAdapter1);

                //search function
                searchView = findViewById(R.id.searchHome);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        myCustomAdapter1.getFilter().filter(s.toString());
                        return true;
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(HomePageActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    //  Toast.makeText(HomePageActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(HomePageActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(HomePageActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(HomePageActivity.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(HomePageActivity.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomePageActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }

        };
        vendorDetails.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(vendorDetails);
    }

    //this is the method to check internet connection
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
    }


    private void showDialog(boolean isConnected) {

        if (isConnected) {
            displayListView();
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

    //menuItem
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();


        if (id == R.id.home_logout) {
            logout();
        }

        if (id == R.id.home) {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
        }

        if (id == R.id.scan_button) {

            llSearch.setVisibility(View.GONE);
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            // integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES); for qr code
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(true);
            integrator.setPrompt("Scan a QR code");
            integrator.setOrientationLocked(false);
            integrator.initiateScan();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //Navigation drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_Vendors) {
            mMainDrawer.closeDrawers();
        }
        if (id == R.id.nav_Points) {
            startActivity(new Intent(HomePageActivity.this, PointsHistory.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            mMainDrawer.closeDrawers();
        }
        if (id == R.id.nav_Cart) {
            startActivity(new Intent(HomePageActivity.this, AddedToCart.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            mMainDrawer.closeDrawers();
        }
        if (id == R.id.nav_purchasehistory) {
            startActivity(new Intent(HomePageActivity.this, PurchaseHistory.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            mMainDrawer.closeDrawers();
        }
        if (id == R.id.nav_Redeemed) {
            startActivity(new Intent(HomePageActivity.this, RedeemedProductList.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            mMainDrawer.closeDrawers();
        }
        if (id == R.id.nav_profile) {
            startActivity(new Intent(HomePageActivity.this, EditProfile.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            mMainDrawer.closeDrawers();
        }
        if (id == R.id.nav_changepassword) {
            startActivity(new Intent(HomePageActivity.this, ChangePasswordActivity.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            mMainDrawer.closeDrawers();
        }
        if (id == R.id.nav_logout) {
            logout();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);
    }


    // this is method for BackButton
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Loyalty Program application");
        builder.setMessage("You want to Exit Loyalty program ?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                System.exit(0);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
        //super.onBackPressed();
    }

    //logout method
    public void logout() {

        StringRequest continue_register = new StringRequest(Request.Method.POST, Constants.logout, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject objectlogout = new JSONObject(response);
                    response_error = objectlogout.getString("Error");
                    if (response_error.equals("false")) {
                        response_message = objectlogout.getString("Message");
                        Toast.makeText(HomePageActivity.this, response_message, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomePageActivity.this, LoginActivity.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                        editor.clear();
                        editor.commit();
                    } else {
                        response_message = objectlogout.getString("Message");
                        Toast.makeText(HomePageActivity.this, response_message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(HomePageActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(HomePageActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(HomePageActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(HomePageActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(HomePageActivity.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(HomePageActivity.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomePageActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);

                return params;
            }
        };
        continue_register.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(continue_register);
    }

    @Override
    public void onClick(View v) {

        llSearch.setVisibility(View.GONE);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a barcode or QRcode");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();

    }

    //this is the method to display offers of all vendors
    public void OtherOffers() {

        StringRequest otherOffers = new StringRequest(Request.Method.POST, Constants.vendoroffers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObjectothers = new JSONObject(response);
                            String data = jsonObjectothers.getString("data");
                            String error = jsonObjectothers.getString("Error");
                            String responseMessage = jsonObjectothers.getString("Message");
                            JSONObject offerObject = jsonObjectothers.getJSONObject("data");

                            if (error.equals("true")) {
                                Toast.makeText(HomePageActivity.this, responseMessage, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }

                            if (error.equals("false")) {

                                if (data.length() > 0) {
                                    rawvendorlist1.clear();
                                    vendorotherProdarrayalllist.clear();

                                    JSONObject vendorData = offerObject.getJSONObject("vendor_details");
                                    vendorName = vendorData.getString("name");
                                    String vendorEmail = vendorData.getString("email");
                                    String vendorAddress = vendorData.getString("address");
                                    thisaddress = vendorAddress;
                                    String vendorPhone = vendorData.getString("phone");
                                    String vendorHash = vendorData.getString("hash");
                                    String vendorGpss = vendorData.getString("gps_location");
                                    String vendorImage = vendorData.getString("store_image");
                                    String status = vendorData.getString("status");
                                    str_phn = vendorPhone;

                                    JSONArray offersArray = offerObject.getJSONArray("other_offers");
                                    for (int i = 0; i < offersArray.length(); i++) {

                                        JSONObject offer_object = offersArray.getJSONObject(i);

                                        String vendorId = offer_object.getString("vendor_id");
                                        String otherName = offer_object.getString("offer_name");
                                        String otherImage = offer_object.getString("offer_image");
                                        String otherOffer = offer_object.getString("offer_price");

                                        rawvendorlist1.add(offer_object);
                                        vendorotherProdarrayalllist.add(new OtherProduct(otherName, otherImage,
                                                otherOffer, vendorId));
                                    }

                                    //setting adater
                                    viewPagerAdapter = new viewPagerAdapter(HomePageActivity.this, vendorotherProdarrayalllist);
                                    pager.setAdapter(viewPagerAdapter);

                                    //view pager
                                    final Handler handler = new Handler();
                                    final Runnable Update = new Runnable() {
                                        public void run() {
                                            if (currentPage == vendorotherProdarrayalllist.size()) {
                                                currentPage = 0;
                                            }
                                            pager.setCurrentItem(currentPage++, true);
                                        }
                                    };

                                    timer = new Timer(); // This will create a new Thread
                                    timer.schedule(new TimerTask() { // task to be scheduled

                                        @Override
                                        public void run() {
                                            handler.post(Update);
                                        }
                                    }, DELAY_MS, PERIOD_MS);

                                    purchaseHistory();

                                } else {

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(HomePageActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(HomePageActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(HomePageActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(HomePageActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(HomePageActivity.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(HomePageActivity.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomePageActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                r = new Random();
                int no = r.nextInt(stringsvender.size());

                if (no > 4)
                    no = no - 2;

                params.put("user_id", uid);
                params.put("vendor_id", stringsvender.get(no));
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        otherOffers.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(otherOffers);
    }


    //this is the method to get all the Individual vendor offers
    public void getIndividualVendorOfferData() {

        StringRequest IndividualVendorOffers = new StringRequest(Request.Method.POST, Constants.vendoroffers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject pointsData;

                        //  Log.d("qrdata", storeQRData + vendorName);

                        startActivity(new Intent(HomePageActivity.this, ShopVendorsProduct.class).
                                putExtra("venderhashdetails", storeQRData).
                                putExtra("vendorName", vendorName).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("vendor_id", storeQRData);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        // swipeRefreshLayout.setRefreshing(false);
        IndividualVendorOffers.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(IndividualVendorOffers);
    }

    //claim request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            if (result.getContents() == null) {
                llSearch.setVisibility(View.GONE);
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {

                llSearch.setVisibility(View.VISIBLE);
                // tvScanContent.setText(result.getContents());
                storeQRData = result.getContents();
                // tvScanFormat.setText(result.getFormatName());

                getIndividualVendorOfferData();

            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onUpdateCheckListener(String urlApp) {

        //  Toast.makeText(HomePageActivity.this, "update is available", Toast.LENGTH_SHORT).show();

        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this)
                .setTitle("New Version Available")
                .setMessage("Please Update to New Version to Continue Use")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        Toast.makeText(HomePageActivity.this, "Update", Toast.LENGTH_SHORT).show();

                        String appPackageName = getPackageName();

                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                    }
                }).setNegativeButton("Remind Me Later!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(HomePageActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }).create();

        alertDialog.show();

    }

    //this is the method to display total remaining points in homePage for Buyer
    public void purchaseHistory() {
        StringRequest purchaseHistory = new StringRequest(Request.Method.POST,
                Constants.usertransaction,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String data = jsonObject.getString("Data");

                            JSONObject purchaseObject = new JSONObject(data);
                            String transaction = purchaseObject.getString("transaction");

                            historyArray = new JSONArray(transaction);
                            for (int i = 0; i < historyArray.length(); i++) {
                                JSONObject historyObject = historyArray.getJSONObject(i);
                                String statement_id = historyObject.getString("statement_id");
                                String buyer_id = historyObject.getString("buyer_id");
                                String description = historyObject.getString("description");
                                String points_id = historyObject.getString("points_id");
                                String redemption_id = historyObject.getString("redemption_id");
                                String opening_balance = historyObject.getString("opening_balance");
                                String credit_points = historyObject.getString("credit_points");

                                //  Points = Integer.parseInt(credit_points) + Points;

                                String debit_points = historyObject.getString("debit_points");
                                closing_balance = historyObject.getString("closing_balance");
                                String status = historyObject.getString("status");
                                String created_at = historyObject.getString("created_at");
                                String updated_at = historyObject.getString("updated_at");

                                String user_details = purchaseObject.getString("user_details");

                                JSONObject vendorData = new JSONObject(user_details);
                                vendorName = vendorData.getString("name");
                                String vendorEmail = vendorData.getString("email");
                                String vendorAddress = vendorData.getString("address");
                                String vendorPhone = vendorData.getString("phone");


                                if (uid.equals(buyer_id)) {

                                    historyList.add(historyObject);

                                    purchaseDetails.add(new PurchaseDetails(statement_id, buyer_id, points_id
                                            , description, redemption_id, opening_balance, credit_points, debit_points, closing_balance
                                            , status, created_at, updated_at, vendorName, vendorEmail, vendorAddress, vendorPhone));

                                    walletamount = String.valueOf(historyArray.getJSONObject(0).getString("closing_balance"));
                                }
                            }

                            //  totalPoints.setText(String.valueOf(closing_balance));
                            totalPoints.setText(walletamount);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    //Toast.makeText(HomePageActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(HomePageActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(HomePageActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(HomePageActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(HomePageActivity.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(HomePageActivity.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomePageActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        purchaseHistory.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(purchaseHistory);
    }

}
