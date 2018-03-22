package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import in.siteurl.www.buyerloyalty.adapters.PointsAdapter;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.Points;


public class PointsHistory extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId, loginIdentity;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    PointsAdapter pointsAdapter;
    ArrayList<Points> pointsList = new ArrayList<Points>();
    ListView listView;
    ArrayList<JSONObject> pointsHistoryList = new ArrayList<>();
    EditText myFilter;
    int totalPoints = 0;
    Dialog alertDialog;
    private SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_history);
        alertDialog = new Dialog(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //sharedPreferences to save the values
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        loginIdentity = sharedPreferences.getString("loginname", null);
        editor = sharedPreferences.edit();

        //UI elements to get the Id
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new SpotsDialog(PointsHistory.this, R.style.Custom);
        dialog.dismiss();

        toolbarTitle = toolbar.findViewById(R.id.toolbartitle);
        toolbarTitle.setText("Points History");

        listView = (ListView) findViewById(R.id.PointsListView);
        myFilter = (EditText) findViewById(R.id.pointsFilter);

        checkConnection();
        dialog.show();
        pointsDetails();
       // getPointsDetails();
    }

    private void getPointsDetails() {

        StringRequest pointsDetails = new StringRequest(Request.Method.POST,
                Constants.allseen, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                dialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String Error = jsonObject.getString("Error");
                    String data = jsonObject.getString("data");

                    JSONArray jsonArray = new JSONArray(data);

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject pointsData = jsonArray.getJSONObject(i);

                        String user_id = pointsData.getString("user_id");
                        String purchase_amount = pointsData.getString("purchase_amount");
                        String points_earned = pointsData.getString("points_earned");
                        String approval_date = pointsData.getString("approval_date");
                        String status = pointsData.getString("status");

                        totalPoints = Integer.parseInt(points_earned) + totalPoints;

                        String user_details = pointsData.getString("user_details");
                        JSONObject userData = new JSONObject(user_details);

                        String vendor_details = pointsData.getString("vendor_details");
                        JSONObject vendorData = new JSONObject(vendor_details);
                        String vendorName = vendorData.getString("name");

                        if (uId.equals(user_id)) {
                            if (status.equals("Completed")) {

                                pointsHistoryList.add(pointsData);
                                pointsList.add(new Points(purchase_amount, points_earned, approval_date
                                        , status, vendorName, user_id));
                            }
                        }
                    }

                    //setting adapter
                    pointsAdapter = new PointsAdapter(getApplicationContext(), R.layout.points_layout, pointsList);
                    listView.setAdapter(pointsAdapter);

                    //enables filtering for the contents of the given ListView
                    listView.setTextFilterEnabled(true);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        }
                    });

                    //Enabling search filter
                    SearchView searchView = (SearchView) findViewById(R.id.search);
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            pointsAdapter.getFilter().filter(s.toString());
                            return true;
                        }
                    });

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(PointsHistory.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    //  Toast.makeText(PointsHistory.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(PointsHistory.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(PointsHistory.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(PointsHistory.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(PointsHistory.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PointsHistory.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    //this is the method do display all the points history from the server
    public void pointsDetails() {

        StringRequest pointsDetails = new StringRequest(Request.Method.POST,
                Constants.allseen,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dialog.dismiss();


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String data = jsonObject.getString("data");

                            JSONArray jsonArray = new JSONArray(data);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject pointsData = jsonArray.getJSONObject(i);

                                String user_id = pointsData.getString("user_id");
                                String purchase_amount = pointsData.getString("purchase_amount");
                                String points_earned = pointsData.getString("points_earned");
                                String approval_date = pointsData.getString("approval_date");
                                String status = pointsData.getString("status");

                                totalPoints = Integer.parseInt(points_earned) + totalPoints;

                                String user_details = pointsData.getString("user_details");
                                JSONObject userData = new JSONObject(user_details);

                                String vendor_details = pointsData.getString("vendor_details");
                                JSONObject vendorData = new JSONObject(vendor_details);
                                String vendorName = vendorData.getString("name");

                                if (uId.equals(user_id)) {
                                    if (status.equals("Completed")) {

                                        pointsHistoryList.add(pointsData);
                                        pointsList.add(new Points(purchase_amount, points_earned, approval_date
                                                , status, vendorName, user_id));
                                    }
                                }
                            }

                            //setting adapter
                            pointsAdapter = new PointsAdapter(getApplicationContext(), R.layout.points_layout, pointsList);
                            listView.setAdapter(pointsAdapter);

                            //enables filtering for the contents of the given ListView
                            listView.setTextFilterEnabled(true);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                }
                            });

                            //Enabling search filter
                            SearchView searchView = (SearchView) findViewById(R.id.search);
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String s) {
                                    pointsAdapter.getFilter().filter(s.toString());
                                    return true;
                                }
                            });

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                if (error instanceof NetworkError) {
                    Log.d("NetworkError", "Please check your internet connection");
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    // Toast.makeText(PointsHistory.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    //  Toast.makeText(PointsHistory.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(PointsHistory.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(PointsHistory.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(PointsHistory.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(PointsHistory.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PointsHistory.this, "Something went wrong", Toast.LENGTH_LONG).show();
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