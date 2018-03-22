package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.apis.Constants;


public class EditProfile extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    TextInputLayout emailTextinputlayout, nameTextinputlayout, phoneTextinputlayout, addressTextinputlayout;
    EditText emailEdittext, nameEdittext, phoneEdittext, addressEdittext;
    String name, email, phone, address;
    Button save;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId, loginIdentity;
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private SpotsDialog dialog;
    String name1, email1, phone1, address1;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        alertDialog = new Dialog(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //sharedPreferences to save the data
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        loginIdentity = sharedPreferences.getString("loginname", null);
        editor = sharedPreferences.edit();


        dialog = new SpotsDialog(EditProfile.this, R.style.Custom);
        dialog.show();

        //UI elements to get the ID
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbarTitle.setText("Edit Profile");

        nameEdittext = findViewById(R.id.nameEdittext);
        emailEdittext = findViewById(R.id.emailEdittext);
        phoneEdittext = findViewById(R.id.phoneEdittext);
        addressEdittext = findViewById(R.id.addressEdittext);
        nameTextinputlayout = findViewById(R.id.nameTextinputlayout);
        emailTextinputlayout = findViewById(R.id.emailTextinputlayout);
        phoneTextinputlayout = findViewById(R.id.phoneTextinputlayout);
        addressTextinputlayout = findViewById(R.id.addressTextinputlayout);
        save = findViewById(R.id.saveEDT);

        checkConnection();
        editProfile();

        //Validations for profile details
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = nameEdittext.getText().toString().trim();
                email = emailEdittext.getText().toString().trim();
                phone = phoneEdittext.getText().toString().trim();
                address = addressEdittext.getText().toString().trim();

                if (name.matches(name1) && email.matches(email1) && phone.matches(phone1) && address.matches(address1)) {

                    AlertDialog.Builder errorbuilder = new AlertDialog.Builder(EditProfile.this);
                    errorbuilder.setMessage("No Changes Found");
                    errorbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    errorbuilder.setCancelable(false);
                    errorbuilder.show();

                    return;
                }

                // we can create object instead of matches

                if (name.equals("") || (name.equals(null))) {
                    nameTextinputlayout.setError("Enter a valid name");
                    dialog.dismiss();
                    return;
                } else {
                    nameTextinputlayout.setError(null);
                    dialog.dismiss();

                }

                if (email.equals("") || (email.equals(null) || !(email.matches(EMAIL_PATTERN)))) {
                    emailTextinputlayout.setError("Enter a valid email address");
                    dialog.dismiss();
                    return;
                } else {
                    emailTextinputlayout.setError(null);
                    dialog.dismiss();
                }

                if (phone.equals("") || (phone.equals(null)) || (phone.length() != 10)) {

                    phoneTextinputlayout.setError("enter a valid phone number");
                    dialog.dismiss();
                    return;
                } else {
                    phoneTextinputlayout.setError(null);
                    dialog.dismiss();
                }


                if (address.equals("") || (address.equals(null))) {

                    addressTextinputlayout.setError("enter a valid address");
                    dialog.dismiss();
                    return;
                } else {
                    addressTextinputlayout.setError(null);
                    dialog.dismiss();
                }

                {
                    AlertDialog.Builder errorbuilder = new AlertDialog.Builder(EditProfile.this);
                    errorbuilder.setIcon(R.mipmap.ic_launcher);
                    errorbuilder.setTitle("Loyalty Program application");
                    errorbuilder.setMessage("Do you want to save the changes ?");
                    errorbuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            dialog.show();
                            saveEditToConsole();
                        }
                    });
                    errorbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    errorbuilder.setCancelable(false);
                    errorbuilder.show();
                }

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

            alertDialog.dismiss();

        } else {
            alertDialog.setContentView(R.layout.check_internet);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            Button button = alertDialog.findViewById(R.id.tryAgain);
            Button exit  =alertDialog.findViewById(R.id.exit);
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

    //this is the method to edit BuyerProfile
    private void editProfile() {

        StringRequest editProfile = new StringRequest(Request.Method.POST, Constants.individialBuyerdetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String responseError = jsonObject.getString("Error");
                            String responseMessage = jsonObject.getString("Message");
                            String responsedata = jsonObject.getString("data");

                            JSONObject alldataobj = new JSONObject(responsedata);
                            String ui = alldataobj.getString("user_id");
                            String name = alldataobj.getString("name");
                            String email = alldataobj.getString("email");
                            String phone = alldataobj.getString("phone");
                            String address = alldataobj.getString("address");
                            name1 = name;
                            email1 = email;
                            phone1 = phone;
                            address1 = address;

                            if (responsedata.contains("user_id")) {
                                nameEdittext.setText(name);
                                nameEdittext.setEnabled(false); //not possible to edit name
                                emailEdittext.setText(email);
                                phoneEdittext.setText(phone);
                                addressEdittext.setText(address);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

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

        editProfile.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(editProfile);
    }


    //sending edit Buyer profile information to server
    private void saveEditToConsole() {

        StringRequest saveEditDetails = new StringRequest(Request.Method.POST, Constants.editIndividialBuyer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String responseError = jsonObject.getString("Error");
                            String responseMessage = jsonObject.getString("Message");


                            if (responseError.equals("true")){
                                Toast.makeText(EditProfile.this, responseMessage, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(EditProfile.this, LoginActivity.class);
                                startActivity(intent);
                            }


                            if (responseError.equals("false")) {
                                String responsemessage = jsonObject.getString("Message");
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                                    builder.setIcon(R.mipmap.ic_launcher);
                                    builder.setTitle("Loyalty Program application");
                                    builder.setMessage(responsemessage);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                        }
                                    });

                                    builder.setCancelable(false);
                                    builder.show();
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
                    //Toast.makeText(EditProfile.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(EditProfile.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(EditProfile.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(EditProfile.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(EditProfile.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(EditProfile.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(EditProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uId);
                params.put("name", name);
                params.put("email", email);
                params.put("phone", phone);
                params.put("address", address);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };

        saveEditDetails.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(saveEditDetails);
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
