package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.apis.Constants;

public class ChangePassword extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId;
    String currentPassword, newPassword, confirmPassword;
    CheckBox mCbShowPwdcurrnt, mCbShowPwdnew, mCbShowPwdconfirm;
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    String response_error, response_message;
    EditText mOldPasswordEditText, mNewPasswordEditText, mConfirmNewPasswordEditText;
    JSONObject objectlogout;
    Dialog alertDialog;
    TextInputLayout oldPwdTextinputLayout, newPwdTextinputLayout, ConfirmnewPwdTextinputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Shared Preferences to save the data
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        editor = sharedPreferences.edit();

        checkConnection();

        //UI elements to get the ID
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbarTitle.setText("Change Password");

        mOldPasswordEditText = (EditText) findViewById(R.id.etcurrentPassword);
        mNewPasswordEditText = (EditText) findViewById(R.id.etnewPassword);
        mConfirmNewPasswordEditText = (EditText) findViewById(R.id.etconfirmPassword);
        mCbShowPwdcurrnt = (CheckBox) findViewById(R.id.cbcurrentShowPwd);
        mCbShowPwdnew = (CheckBox) findViewById(R.id.cbnewShowPwd);
        mCbShowPwdconfirm = (CheckBox) findViewById(R.id.cbconfirmShowPwd);
        oldPwdTextinputLayout = findViewById(R.id.currentpwdTextinputlayout);
        newPwdTextinputLayout = findViewById(R.id.newpwdTextinputlayout);
        ConfirmnewPwdTextinputLayout = findViewById(R.id.confirmNewpwdTextinputlayout);

        // add onCheckedListener on checkbox
        // when user clicks on this checkbox, this is the handler.
        mCbShowPwdcurrnt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    mOldPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    mOldPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        mCbShowPwdnew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    mNewPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    mNewPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        mCbShowPwdconfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    mConfirmNewPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    mConfirmNewPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    //validating change password credentials
    public void setPassword(View view) {

        currentPassword = mOldPasswordEditText.getText().toString().trim();
        newPassword = mNewPasswordEditText.getText().toString().trim();
        confirmPassword = mConfirmNewPasswordEditText.getText().toString().trim();

       /* if (newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
            checkConnection();
            resetPassword();
        } else {
            Snackbar snackbar = Snackbar.make(mConfirmNewPasswordEditText, "Password do not Match", Snackbar.LENGTH_LONG);
            snackbar.show();
        }*/

        validate();
    }

    //validations for email and password
    public void validate() {

        if (currentPassword.equals("") || (currentPassword.equals(null))) {
            oldPwdTextinputLayout.setError("Enter Old password");
            return;

        } else {
            oldPwdTextinputLayout.setError(null);

        }

        if (newPassword.equals("") || (newPassword.equals(null))) {
            newPwdTextinputLayout.setError("Enter New Password");
            return;

        } else {
            newPwdTextinputLayout.setError(null);

        }


        if (confirmPassword.equals("") || (confirmPassword.equals(null))) {
            ConfirmnewPwdTextinputLayout.setError("Enter Confirm New Password");
            return;

        } else {
            ConfirmnewPwdTextinputLayout.setError(null);

        }

        if (currentPassword.length() < 5) {
            oldPwdTextinputLayout.setError("Password should be minimum 5 characters");
            Snackbar snackbar = Snackbar.make(mOldPasswordEditText, "Password should be minimum 5 characters", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        if (newPassword.length() < 5) {
            newPwdTextinputLayout.setError("Password should be minimum 5 characters");
            Snackbar snackbar = Snackbar.make(mNewPasswordEditText, "Password should be minimum 5 characters", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        if (confirmPassword.length() < 5) {
            ConfirmnewPwdTextinputLayout.setError("Password should be minimum 5 characters");
            Snackbar snackbar = Snackbar.make(mConfirmNewPasswordEditText, "Password should be minimum 5 characters", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        if (newPassword.equals(confirmPassword)) {
            // Toast.makeText(this, "Password matched", Toast.LENGTH_SHORT).show();
            checkConnection();
            resetPassword();
        } else {
            Snackbar snackbar = Snackbar.make(mConfirmNewPasswordEditText, "Password Didn't Match", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

    }


    //sending/save the reset password to server
    public void resetPassword() {

        StringRequest resetPassword = new StringRequest(Request.Method.POST, Constants.changepwd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            objectlogout = new JSONObject(response);
                            response_error = objectlogout.getString("Error");
                            String responseMessage = objectlogout.getString("Message");


                            if (response_error.equals("true")) {
                                Toast.makeText(ChangePassword.this, responseMessage, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
                                startActivity(intent);
                            }


                            if (response_error.equals("false")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                                builder.setIcon(R.mipmap.ic_launcher);
                                builder.setTitle("Loyalty program application");
                                builder.setMessage("password successfully changed");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        try {
                                            response_message = objectlogout.getString("Message");
                                            Toast.makeText(ChangePassword.this, response_message, Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChangePassword.this, LoginActivity.class).
                                                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            editor.clear();
                                            editor.commit();
                                            finish();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
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
                            } else {
                                response_message = objectlogout.getString("Message");
                                Toast.makeText(ChangePassword.this, response_message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ChangePassword.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    // Toast.makeText(ChangePassword.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(ChangePassword.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(ChangePassword.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(ChangePassword.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(ChangePassword.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ChangePassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uId);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                params.put("currentpassword", currentPassword);
                params.put("password", confirmPassword);
                return params;
            }
        };
        resetPassword.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(resetPassword);
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
