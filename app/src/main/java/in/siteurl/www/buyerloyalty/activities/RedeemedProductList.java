package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.adapters.RedeemAdapter;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.RedeemProduct;


public class RedeemedProductList extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId;
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    RedeemAdapter redeemAdapter;
    ArrayList<RedeemProduct> redeemProductArrayList = new ArrayList<RedeemProduct>();
    ListView listView;
    ArrayList<JSONObject> redeemList = new ArrayList<>();
    EditText myFilter;
    int totalPoints = 0;
    String expiry_date;
    Dialog alertDialog;
    TextView noItems;
    RelativeLayout relativeLayout;
    private SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeemed_product_list);
        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //shared preferences to get the data
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        editor = sharedPreferences.edit();

        //UI elements
        listView = (ListView) findViewById(R.id.RedeemListView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new SpotsDialog(RedeemedProductList.this, R.style.Custom);
        dialog.dismiss();

        myFilter = (EditText) findViewById(R.id.redeemFilter);

        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbarTitle.setText("Redeem Products");

        // noItems = findViewById(R.id.noItemsRedeem);
        relativeLayout = findViewById(R.id.redeemLayout);

        checkConnection();
        pointsDetails();
    }


    //this is the method to get points details from server
    public void pointsDetails() {

        StringRequest pointsDetails = new StringRequest(Request.Method.POST,
                Constants.allseen,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String data = jsonObject.getString("data");

                            JSONArray jsonArray = new JSONArray(data);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject pointsData = jsonArray.getJSONObject(i);

                                String points_earned = pointsData.getString("points_earned");
                                totalPoints = Integer.parseInt(points_earned) + totalPoints;
                                Log.d("totalPoints", String.valueOf(totalPoints));

                            }

                            editor.putString("TotalPoints", String.valueOf(totalPoints));
                            editor.commit();

                            dialog.show();
                            redeemProducts();

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
                    // Toast.makeText(RedeemedProductList.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    //  Toast.makeText(RedeemedProductList.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(RedeemedProductList.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(RedeemedProductList.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(RedeemedProductList.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(RedeemedProductList.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RedeemedProductList.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        pointsDetails.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(pointsDetails);

    }

    //This is the method to get all Redeem products from server
    private void redeemProducts() {

        StringRequest redeemProducts = new StringRequest(Request.Method.POST, Constants.allredeemproduct,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;

                        dialog.dismiss();

                        try {

                            jsonObject = new JSONObject(response);
                            String response_error = jsonObject.getString("Error");
                            String responseMessage = jsonObject.getString("Message");

                            if (response_error.equals("true")){
                                Toast.makeText(RedeemedProductList.this, responseMessage, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RedeemedProductList.this, LoginActivity.class);
                                startActivity(intent);
                            }

                            if (response_error.equals("false")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                if (data.length() > 0) {
                                    redeemProductArrayList.clear();
                                    redeemList.clear();

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject data_object = data.getJSONObject(i);
                                        String redeemption_prod_id = data_object.getString("redeemption_prod_id");
                                        String product_name = data_object.getString("product_name");
                                        String user_id = data_object.getString("user_id");
                                        String points_value = data_object.getString("points_value");
                                        String product_img = data_object.getString("product_img");
                                        String updated_at = data_object.getString("updated_at");
                                        expiry_date = data_object.getString("expiry_date");
                                        String prod_description = data_object.getString("prod_description");
                                        String terms_and_condition = data_object.getString("terms_and_condition");
                                        String status = data_object.getString("status");

                                        redeemList.add(data_object);

                                        try {
                                            Calendar c = Calendar.getInstance();
                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                            String formatDate = df.format(c.getTime());

                                            Date newDateStr = df.parse(formatDate);
                                            Date userEnteredDate = df.parse(expiry_date);

                                            long NoOfDays = userEnteredDate.getTime() - newDateStr.getTime();
                                            NoOfDays = TimeUnit.DAYS.convert(NoOfDays, TimeUnit.MILLISECONDS);
                                            Log.d("NoOfDays", String.valueOf(NoOfDays));

                                            if (NoOfDays >= 0) {
                                                redeemProductArrayList.add(new RedeemProduct(redeemption_prod_id, product_name, user_id,
                                                        points_value, product_img, updated_at, expiry_date, prod_description,
                                                        terms_and_condition, status));

                                            } else {

                                            }

                                        } catch (java.text.ParseException e) {
                                            e.printStackTrace();

                                        }
                                    }

                                    //setting adapter
                                    if (redeemProductArrayList.size() > 0) {
                                        redeemAdapter = new RedeemAdapter(getApplicationContext(), R.layout.redeemproduct_list, redeemProductArrayList);
                                        listView.setAdapter(redeemAdapter);
                                    } else {
                                        // noItems.setVisibility(View.VISIBLE);

                                        RelativeLayout relativeLayout1 = findViewById(R.id.redeem_relative);
                                        TextView tv = new TextView(getApplicationContext());
                                        tv.setText("No Redeem Products");
                                        tv.setTextColor(Color.BLACK);
                                        tv.setTextSize(18);
                                        relativeLayout1.setGravity(Gravity.CENTER);
                                        relativeLayout1.addView(tv);

                                        listView.setVisibility(View.GONE);
                                        relativeLayout.setVisibility(View.GONE);
                                    }


                                    //enables filtering for the contents of the given ListView
                                    listView.setTextFilterEnabled(true);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        }
                                    });

                                    //Enabling search filter
                                    SearchView searchView = (SearchView) findViewById(R.id.searchRedeem);
                                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                        @Override
                                        public boolean onQueryTextSubmit(String query) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onQueryTextChange(String s) {
                                            redeemAdapter.getFilter().filter(s.toString());
                                            return true;
                                        }
                                    });

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

                dialog.dismiss();

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(RedeemedProductList.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    //  Toast.makeText(RedeemedProductList.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(RedeemedProductList.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(RedeemedProductList.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(RedeemedProductList.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(RedeemedProductList.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RedeemedProductList.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<>();
                params.put("user_id", uId);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        redeemProducts.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(redeemProducts);
    }


    //menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.addToCart) {

            startActivity(new Intent(RedeemedProductList.this, AddedToCart.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_redeem, menu);

        return super.onCreateOptionsMenu(menu);
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
