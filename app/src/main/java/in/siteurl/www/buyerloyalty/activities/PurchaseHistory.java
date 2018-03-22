package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import dmax.dialog.SpotsDialog;
import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.adapters.AdapterPurchase;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.PurchaseDetails;

public class PurchaseHistory extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId, loginIdentity;
    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private ArrayList<PurchaseDetails> purchaseDetails = new ArrayList<>();
    ArrayList<JSONObject> historyList = new ArrayList<>();
    JSONArray historyArray;
    TextView tv_balance;
    int totalPoints;
    String closing_balance;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    String vendorName;
    private Toolbar toolbar;
    RelativeLayout transcationPoints;
    Dialog alertDialog;
    String walletamount;
    private SpotsDialog dialog;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_transcation);
        alertDialog = new Dialog(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //shared preferences to save the data
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        loginIdentity = sharedPreferences.getString("loginname", null);
        editor = sharedPreferences.edit();

        //UI elements to get the ID
        mToolbar = (Toolbar) findViewById(R.id.toolbarPurchase);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new SpotsDialog(PurchaseHistory.this, R.style.Custom);
        dialog.dismiss();

        tv_balance = findViewById(R.id.tv_balance);
        transcationPoints = findViewById(R.id.totalPoints);
        recyclerView = (RecyclerView) findViewById(R.id.rv_transactions);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        checkConnection();
        dialog.show();
        purchaseHistory();

        //collapsing toolbar
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar1);
        toolbar = (Toolbar) findViewById(R.id.toolbarPurchase);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.transactionappbarlayout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                    transcationPoints.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Transaction History");
                    isShow = true;
                    transcationPoints.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);

                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                    transcationPoints.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //this is the method to get transaction details of buyer
    public void purchaseHistory() {
        StringRequest purchaseHistory = new StringRequest(Request.Method.POST,
                Constants.usertransaction,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

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

                                //  totalPoints = Integer.parseInt(credit_points) + totalPoints;

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

                                if (uId.equals(buyer_id)) {
                                    historyList.add(historyObject);
                                    purchaseDetails.add(new PurchaseDetails(statement_id, buyer_id, points_id
                                            , description, redemption_id, opening_balance, credit_points, debit_points, closing_balance
                                            , status, created_at, updated_at, vendorName, vendorEmail, vendorAddress, vendorPhone));
                                }

                                walletamount = String.valueOf(historyArray.getJSONObject(0).getString("closing_balance"));
                            }

                            tv_balance.setText(walletamount);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        AdapterPurchase adapterPurchase = new AdapterPurchase(PurchaseHistory.this, purchaseDetails);
                        recyclerView.setAdapter(adapterPurchase);

                        searchView = findViewById(R.id.purchaseSearch);
                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String query) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String s) {
                                adapterPurchase.getFilter().filter(s.toString());
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
                    // Toast.makeText(PurchaseHistory.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    //  Toast.makeText(PurchaseHistory.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(PurchaseHistory.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(PurchaseHistory.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(PurchaseHistory.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(PurchaseHistory.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PurchaseHistory.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uId);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        purchaseHistory.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(purchaseHistory);
    }

    //this is the method to check internet connection
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
    }


    private void showDialog(boolean isConnected) {

        if (isConnected) {

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
    public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);
    }
}
