package in.siteurl.www.buyerloyalty.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.adapters.VendorOffersAdapter;
import in.siteurl.www.buyerloyalty.adapters.viewPagerAdapter;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.VendorProduct;

public class IndivisualOffer extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    TextView pro_name;
    TextView pro_price;
    TextView pro_exp;
    TextView pro_terms,
            pro_desc;
    TextView nme, email, addrs;
    ImageView imageView, gps_showi, shop_nowi, imageNew, imageEndingSoon, imageExpired;
    SharedPreferences loginpref;
    SharedPreferences.Editor editor;
    String sessionid, uid, login_identity;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    VendorProduct vendorProduct;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indivisual_offer);
        alertDialog = new Dialog(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //sharedPreferences to store the values
        loginpref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionid = loginpref.getString("sessionid", null);
        uid = loginpref.getString("User-id", null);
        login_identity = loginpref.getString("loginname", null);
        editor = loginpref.edit();

        //UI elements to get the ID
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarTitle = toolbar.findViewById(R.id.toolbartitle);
        toolbarTitle.setText("Individual  Offer");

        pro_name = findViewById(R.id.Offername);
        pro_price = findViewById(R.id.OfferPrice);
        pro_exp = findViewById(R.id.OfferExpirydate);
        pro_terms = findViewById(R.id.OffertermsandconditionTV1);
        pro_desc = findViewById(R.id.Offerdescription);
        imageView = findViewById(R.id.Offerimage);
        shop_nowi = findViewById(R.id.Offershop_now1);
        gps_showi = findViewById(R.id.Offergps_show1);
        nme = findViewById(R.id.particularOfferName);
        email = findViewById(R.id.particularOfferEmail);
        addrs = findViewById(R.id.particularOfferAdderess);
        imageNew = findViewById(R.id.newOfferimage);
        imageEndingSoon = findViewById(R.id.OfferendingSoon);
        imageExpired = findViewById(R.id.Offerexpired);

        checkConnection();

        vendorProduct = (VendorProduct) getIntent().getSerializableExtra("key");

        //Setting offer and vendor details
        pro_name.setText(vendorProduct.getOffer_name());
        pro_price.setText("₹" + vendorProduct.getOffer_price());
        pro_exp.setText(vendorProduct.getExpiry_date());
        pro_terms.setText(vendorProduct.getTerms_and_condtion());
        pro_desc.setText(vendorProduct.getOffer_description());
        nme.setText(vendorProduct.getVendorName());
        email.setText(vendorProduct.getVendorEmail());
        addrs.setText(vendorProduct.getVendorAddress());


        //glide image
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.header);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();

        Glide.with(getApplicationContext()).load(vendorProduct.getOffer_image())
                .thumbnail(0.5f)
                .apply(requestOptions)
                .into(imageView);

        //to check the system date with user enter date

        try {

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formatDate = df.format(c.getTime());

            Date newDateStr = df.parse(formatDate);
            Date userEnteredDate = df.parse(vendorProduct.getExpiry_date());

            long NoOfDays = userEnteredDate.getTime() - newDateStr.getTime();
            NoOfDays = TimeUnit.DAYS.convert(NoOfDays, TimeUnit.MILLISECONDS);


            if (NoOfDays >= 5) {
                imageNew.setVisibility(View.VISIBLE);
                imageEndingSoon.setVisibility(View.GONE);
                imageExpired.setVisibility(View.GONE);
                Glide.with(getApplicationContext()).load(R.drawable.newoffer)
                        .thumbnail(0.5f)
                        .into(imageNew);

            }

            if (NoOfDays < 5) {
                imageEndingSoon.setVisibility(View.VISIBLE);
                imageNew.setVisibility(View.GONE);
                imageExpired.setVisibility(View.GONE);
                Glide.with(getApplicationContext()).load(R.drawable.ending)
                        .thumbnail(0.5f)
                        .into(imageEndingSoon);

            }

            if (NoOfDays <= 0) {
                imageExpired.setVisibility(View.VISIBLE);
                imageEndingSoon.setVisibility(View.GONE);
                imageNew.setVisibility(View.GONE);
                Glide.with(getApplicationContext()).load(R.drawable.expiry)
                        .thumbnail(0.5f)
                        .into(imageExpired);

            }

        } catch (java.text.ParseException e) {
            e.printStackTrace();

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
