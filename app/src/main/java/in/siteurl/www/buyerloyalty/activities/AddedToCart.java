package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.adapters.CartingAdapter;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.RedeemProduct;

public class AddedToCart extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId, login_identity;
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    CartingAdapter cartingAdapter;
    ArrayList<RedeemProduct> redeemCartingList = new ArrayList<RedeemProduct>();

    ListView listView;
    TextView noItems;
    DatabaseHandler databaseHandler;
    Button proceedCart;
    RelativeLayout relativeLayout;

    String noOfProd;
    private SpotsDialog dialog;

    ArrayList<String> cartingList = new ArrayList<>();
    ArrayList<String> idcartingList = new ArrayList<>();
    String prodNames;
    String prodId;
    int prodPoints = 0;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_to_cart);
        alertDialog = new Dialog(this);

        //database handler
        databaseHandler = new DatabaseHandler(AddedToCart.this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        login_identity = sharedPreferences.getString("loginname", null);
        editor = sharedPreferences.edit();

        //UI elements to get ID
        listView = (ListView) findViewById(R.id.SearchListView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        noItems = findViewById(R.id.noItemsCart);
        proceedCart = findViewById(R.id.proceddCart);
        relativeLayout = findViewById(R.id.cartLayout);

        dialog = new SpotsDialog(AddedToCart.this, R.style.Custom);
        dialog.dismiss();

        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbarTitle.setText("Your Cart");

        checkConnection();

        //getting all products from database
        redeemCartingList = databaseHandler.getCartProducts();

        //checking cart Size to display the carting Items
        if (redeemCartingList.size() > 0) {
            cartingAdapter = new CartingAdapter(getApplicationContext(), R.layout.carting_products, redeemCartingList);
            listView.setAdapter(cartingAdapter);
        } else {
            noItems.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            proceedCart.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
        }

        listView.setTextFilterEnabled(true);

        //Enabling search filter
        SearchView searchView = (SearchView) findViewById(R.id.searchCart);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                cartingAdapter.getFilter().filter(s.toString());
                return true;
            }
        });


        proceedCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
                dialog.show();
                shopNow();
            }
        });
    }


    //this is the method to check internet connection
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
    }


    private void showDialog(boolean isConnected) {

        if (isConnected) {
            // shopNow();
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


    //this is the method for complete shopping
    public void shopNow() {

        noOfProd = String.valueOf(redeemCartingList.size());
        for (int i = 0; i < redeemCartingList.size(); i++) {
            prodPoints = Integer.parseInt(redeemCartingList.get(i).getPoints_value()) + prodPoints;

            prodNames = redeemCartingList.get(i).getProduct_name();
            cartingList.add(prodNames);

            prodId = redeemCartingList.get(i).getRedeemption_prod_id();
            idcartingList.add(prodId);

        }

        StringRequest cartData = new StringRequest(Request.Method.POST, Constants.shopNow,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dialog.dismiss();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");


                            if (Error.equals("false")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(AddedToCart.this);
                                builder.setIcon(R.mipmap.ic_launcher);
                                builder.setTitle("Loyalty Program application");
                                builder.setMessage(Message);
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        databaseHandler.deleteAll();
                                        Intent intent = new Intent(AddedToCart.this, HomePageActivity.class);
                                        startActivity(intent);

                                    }
                                });
                                builder.setCancelable(false);
                                builder.show();

                            }

                            if (Error.equals("true")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(AddedToCart.this);
                                builder.setIcon(R.mipmap.ic_launcher);
                                builder.setTitle("Loyalty Program application");
                                builder.setMessage(Message);
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                    }
                                });
                                builder.setCancelable(false);
                                builder.show();
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
                    Toast.makeText(AddedToCart.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(AddedToCart.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(AddedToCart.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(AddedToCart.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(AddedToCart.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(AddedToCart.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddedToCart.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uId);
                params.put("sid", sessionId);
                params.put("product_id", prodId);
                params.put("no_of_products", noOfProd);
                params.put("total_points_used", String.valueOf(prodPoints));
                params.put("description", prodNames);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };

        cartData.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(AddedToCart.this).addtorequestqueue(cartData);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }
}