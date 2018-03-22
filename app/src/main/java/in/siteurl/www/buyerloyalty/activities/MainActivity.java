package in.siteurl.www.buyerloyalty.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
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

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.adapters.MyCustomAdapter1;
import in.siteurl.www.buyerloyalty.adapters.RedeemAdapter;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.RedeemProduct;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar mToolbar;
    private TextView toolbarTitle;
    ListView listView;
    ArrayList<RedeemProduct> redeemProductArrayList = new ArrayList<RedeemProduct>();
    ArrayList<RedeemProduct> Books = new ArrayList<RedeemProduct>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId;
    ArrayList<JSONObject> redeemList = new ArrayList<>();
    RedeemAdapter redeemAdapter;
    String expiry_date;

    String[] products = {"Electronics", "Fashion", "Books", "Sports", "Home & Furniture",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //shared preferences to get the data
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        editor = sharedPreferences.edit();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);
        listView = (ListView) findViewById(R.id.products);

        Spinner spinner = (Spinner) findViewById(R.id.product_spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, products);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        if (item.equals("Electronics")) {

            redeemProducts();
        }

        if (item.equals("Books")) {
            redeemAdapter = new RedeemAdapter(getApplicationContext(), R.layout.redeemproduct_list, redeemProductArrayList);
            listView.setAdapter(null);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //This is the method to get all Redeem products from server
    private void redeemProducts() {

        StringRequest redeemProducts = new StringRequest(Request.Method.POST, Constants.allredeemproduct,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;

                        try {

                            jsonObject = new JSONObject(response);
                            String response_error = jsonObject.getString("Error");
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
                                    redeemAdapter = new RedeemAdapter(getApplicationContext(), R.layout.redeemproduct_list, redeemProductArrayList);
                                    listView.setAdapter(redeemAdapter);


                                    //enables filtering for the contents of the given ListView
                                    listView.setTextFilterEnabled(true);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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
}
