package in.siteurl.www.buyerloyalty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class ConfirmPassword extends AppCompatActivity {

    EditText editTextConfirmPassword;
    CheckBox checkBox;
    String str_email, str_uName, str_phone, str_address, str_password, str_confirmPassword;
    String response_error, response_message;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);

        //UI elements to get the ID
        editTextConfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        //check box
        checkBox = (CheckBox) findViewById(R.id.cbShowPwd);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        str_email = getIntent().getStringExtra("email");
        str_uName = getIntent().getStringExtra("uname");
        str_phone = getIntent().getStringExtra("phone");
        str_address = getIntent().getStringExtra("address");
        str_password = getIntent().getStringExtra("password");
    }

    //this is the method to confirm the password
    public void Sign_up(View view) {
        str_confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (str_confirmPassword.equals(str_password)) {
            StringRequest confirmPassword = new StringRequest(Request.Method.POST, Constants.register, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        response_error = jsonObject.getString("Error");
                        if (response_error.equals("true")) {
                            response_message = jsonObject.getString("Message");
                            Toast.makeText(ConfirmPassword.this, response_message, Toast.LENGTH_SHORT).show();
                        } else if (response_error.equals("false")) {
                            response_message = jsonObject.getString("Message");
                            Toast.makeText(ConfirmPassword.this, response_message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ConfirmPassword.this, LoginActivity.class).
                                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
                        //Toast.makeText(ConfirmPassword.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ServerError) {
                        Log.d("ServerError", "ServerError");
                        // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                        // Toast.makeText(ConfirmPassword.this, "Server Error", Toast.LENGTH_LONG).show();
                    } else if (error instanceof AuthFailureError) {
                        Log.d("AuthFailureError", "Authentication Error");
                        //If you are trying to do Http Basic authentication then this error is most likely to come.
                        Toast.makeText(ConfirmPassword.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    } else if (error instanceof ParseError) {
                        Log.d("ParseError", "Parse Error");
                        //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                        Toast.makeText(ConfirmPassword.this, "Parse Error", Toast.LENGTH_LONG).show();
                    } else if (error instanceof NoConnectionError) {
                        Log.d("NoConnectionError", "No connection");
                        // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                        Toast.makeText(ConfirmPassword.this, "No connection", Toast.LENGTH_LONG).show();
                    } else if (error instanceof TimeoutError) {
                        Log.d("TimeoutError", "Timeout Error");
                        // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                        Toast.makeText(ConfirmPassword.this, " Timeout Error", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ConfirmPassword.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put("name", str_uName);
                    params.put("email", str_email);
                    params.put("password", str_password);
                    params.put("phone", str_phone);
                    params.put("address", str_address);
                    params.put("api_key", Constants.APIKEY);

                    return params;
                }
            };

            confirmPassword.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(confirmPassword);
        } else {
            snackbar = Snackbar.make(checkBox, "Password do not match", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }
}
