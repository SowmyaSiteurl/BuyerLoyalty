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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.apis.Constants;


public class LoginActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button login;
    TextInputLayout emailTextinputlayout, passwordTextinputlayout;
    EditText emailEdittext, passwordEdittext;
    String str_email, str_password;
    String response_error, response_message, forgotemail;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
    // "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})"
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    CheckBox showPassword;
    TextView forgotPassword;
    TextInputLayout mEmail_forgot;

    String sessionid, uid;
    private SpotsDialog dialog;
    String email;
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    boolean flag;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //UI Elements to get the Id
        //  login = (Button) findViewById(R.id.login);
        emailEdittext = (EditText) findViewById(R.id.emailEdittext);
        passwordEdittext = (EditText) findViewById(R.id.passwordEdittext);
        emailTextinputlayout = (TextInputLayout) findViewById(R.id.emailTextinputlayout);
        passwordTextinputlayout = (TextInputLayout) findViewById(R.id.passwordTextinputlayout);
        showPassword = (CheckBox) findViewById(R.id.showPassword);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);

        dialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        dialog.dismiss();

      /*  login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

                str_email = emailEdittext.getText().toString().trim();
                str_password = passwordEdittext.getText().toString().trim();

                if (str_email.equals("") || (str_email.equals(null) || !(str_email.matches(EMAIL_PATTERN)))) {

                    emailTextinputlayout.setError("Enter a valid email address");
                    dialog.dismiss();
                    return;

                } else {
                    emailTextinputlayout.setError(null);
                    dialog.dismiss();
                }

                if (str_password.equals("") || (str_password.equals(null))) {

                    passwordTextinputlayout.setError("Enter a valid password");
                    dialog.dismiss();
                    return;

                } else {
                    dialog.show();
                    passwordTextinputlayout.setError(null);
                      Sign_in();

                }

            }
        });
*/

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    passwordEdittext.setInputType(129);
                } else {

                    passwordEdittext.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        // shared preferences to save the data

        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        email = loginpref.getString("", null);
        editor = loginpref.edit();


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

      //  checkConnection();

    }

    public void Login(View view) {

        dialog.show();
        str_email = emailEdittext.getText().toString().trim();
        str_password = passwordEdittext.getText().toString().trim();

        validate();
    }

    //validations for email and password
    public void validate() {

        if (str_email.equals("") || (str_email.equals(null) || !(str_email.matches(EMAIL_PATTERN)))) {

            emailTextinputlayout.setError("Enter a valid email address");
            dialog.dismiss();
            return;

        } else {
            emailTextinputlayout.setError(null);
            dialog.dismiss();
        }

        if (str_password.equals("") || (str_password.equals(null))) {

            passwordTextinputlayout.setError("Enter a valid password");
            dialog.dismiss();
            return;

        } else {
            dialog.show();
            passwordTextinputlayout.setError(null);
          //  checkConnection();
            Sign_in();

        }

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


    //alert dialog to show the password
    public void showForgotPasswordDialog() {

        AlertDialog.Builder forgotPasswordDialog = new AlertDialog.Builder(this);
        forgotPasswordDialog.setIcon(R.mipmap.ic_launcher);
        forgotPasswordDialog.setTitle("Forgot Password");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View forgotView = layoutInflater.inflate(R.layout.forgotpassword, null);
        mEmail_forgot = forgotView.findViewById(R.id.email_forgotpassword);
        forgotPasswordDialog.setView(forgotView);
        forgotPasswordDialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                forgotemail = mEmail_forgot.getEditText().getText().toString();
                if (forgotemail.equals("null") || (forgotemail.equals(null) || !(forgotemail.matches(EMAIL_PATTERN)))) {
                    Toast.makeText(LoginActivity.this, "Enter a Valid Email", Toast.LENGTH_SHORT).show();
                    showForgotPasswordDialog();
                    return;

                }

                dialog.show();
                sendEmailToServer();
                dialogInterface.dismiss();
            }
        });

        forgotPasswordDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        forgotPasswordDialog.show();
    }

    public void emailAlert(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Loyalty Program application");
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        builder.setCancelable(false);
        builder.show();

    }


    //this is the method to send email to server(for forgot password)
    public void sendEmailToServer() {

        StringRequest cartData = new StringRequest(Request.Method.POST, Constants.forgotPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        dialog.dismiss();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");

                            if (Error.equals("false")) {
                                String alertMessage;
                                alertMessage = Message;
                                emailAlert(alertMessage);

                            }

                            if (Error.equals("true")) {
                                String alertMessage;
                                alertMessage = Message;
                                emailAlert(alertMessage);

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
                    //  Socket disconnection, server down, DNS issues might result in this error.
                     Log.d("NetworkError", "Please check your internet connection");
                    Toast.makeText(LoginActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                   // Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Log.d("AuthFailureError", "Authentication Error");
                    Toast.makeText(LoginActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Log.d("ParseError", "Parse Error");
                    Toast.makeText(LoginActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Log.d("NoConnectionError", "No connection");
                    Toast.makeText(LoginActivity.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Log.d("TimeoutError", "Timeout Error");
                    Toast.makeText(LoginActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", forgotemail);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };

        cartData.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(LoginActivity.this).addtorequestqueue(cartData);
    }


    //this is the Method to send login details to server
    public void Sign_in() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("email", str_email);
        hashMap.put("password", str_password);
        hashMap.put("api_key", Constants.APIKEY);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        JsonObjectRequest signIn = new JsonObjectRequest(Request.Method.POST, Constants.login, new JSONObject(hashMap), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    response_error = response.getString("Error");
                    if (response_error.equals("true")) {
                        response_message = response.getString("Message");
                        Toast.makeText(LoginActivity.this, response_message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    } else if (response_error.equals("false")) {

                        response_message = response.getString("Message");
                        Toast.makeText(LoginActivity.this, response_message, Toast.LENGTH_SHORT).show();
                        String role = response.getString("role");

                        if (role.equals("user")) {

                            String data = response.getString("data");
                            JSONObject jsonObject1 = new JSONObject(data);

                            String name = jsonObject1.getString("name");
                            String email = jsonObject1.getString("email");
                            String user_id = jsonObject1.getString("user_id");
                            String sid = jsonObject1.getString("sid");
                            String user_group_id = jsonObject1.getString("user_group_id");

                            // save changes to shared preferences.

                            editor.putString("loginname", name);
                            editor.putString("loginemail", email);
                            editor.putString("role", role);
                            editor.putString("sessionid", sid);
                            editor.putString("User-id", user_id);
                            editor.putString("user_group_id-id", user_group_id);

                            editor.putString("login_data", String.valueOf(jsonObject1));
                            editor.commit();

                            emailEdittext.getText().clear();
                            passwordEdittext.getText().clear();

                            startActivity(new Intent(LoginActivity.this, HomePageActivity.class).
                                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();


                        } else {
                            Toast.makeText(LoginActivity.this, "oops something went wrong", Toast.LENGTH_SHORT).show();
                            return;
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
                    //  Socket disconnection, server down, DNS issues might result in this error.
                    Toast.makeText(LoginActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                    Log.d("NetworkError", "Please check your internet connection");
                } else if (error instanceof ServerError) {
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                   // Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_LONG).show();
                    Log.d("ServerError", "ServerError");
                } else if (error instanceof AuthFailureError) {
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(LoginActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("AuthFailureError", "Authentication Error");
                } else if (error instanceof ParseError) {
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Log.d("ParseError", "Parse Error");
                    Toast.makeText(LoginActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Log.d("NoConnectionError", "No connection");
                    Toast.makeText(LoginActivity.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(LoginActivity.this, " Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("TimeoutError", "Timeout Error");
                } else {
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });

        int socketTimeout = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        signIn.setRetryPolicy(policy);
        queue.add(signIn);


    }

    public void getSignUpNow(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
