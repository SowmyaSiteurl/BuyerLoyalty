package in.siteurl.www.buyerloyalty.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.apis.Constants;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ShopVendorsProduct extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uId;
    private Toolbar mToolbar;
    private TextView toolbarTitle;
    ImageView prodImg;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChosenTask, proImage, strFileNameOne, profile, amount, description;
    public static final int RequestPermissionCode = 1;
    Bitmap thumbnail;
    String vendorHashData;
    TextInputLayout amountTextInputLayout, descriptionTextInputLayout;
    EditText amountEditText, descriptionEditText;
    Button claimRequest;

    TextView vndrName;
    String vendorname;
    Dialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_vendors_product);
        alertDialog = new Dialog(this);

        //getting data from other activity
        vendorHashData = getIntent().getStringExtra("venderhashdetails");
        vendorname = getIntent().getStringExtra("vendorName");
        vndrName = findViewById(R.id.vendorName);
        vndrName.setText(vendorname);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //sharedPreferences to get the data
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        editor = sharedPreferences.edit();

        checkConnection();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarTitle = mToolbar.findViewById(R.id.toolbartitle);
        toolbarTitle.setText("Claim Request");

        //UI elements to get the ID
        prodImg = (ImageView) findViewById(R.id.claimImage);
        amountEditText = findViewById(R.id.purchaseEdittext);
        amountTextInputLayout = findViewById(R.id.purchaseTextinputlayout);
        descriptionEditText = findViewById(R.id.descriptionEdittext);
        descriptionTextInputLayout = findViewById(R.id.descriptionTextinputlayout);
        claimRequest = findViewById(R.id.claimRequest);


        prodImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firstcheckPermission();
                selectImage();
            }
        });

        //validations
        claimRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                amount = amountEditText.getText().toString().trim();
                description = descriptionEditText.getText().toString().trim();

                if (amount.equals("") || (amount.equals(null))) {
                    amountTextInputLayout.setError("Enter a valid product price");
                    return;
                } else {
                    amountTextInputLayout.setError(null);
                }

                if (description.equals("") || (description.equals(null))) {
                    descriptionTextInputLayout.setError("Enter a valid product Description");
                    return;
                } else {
                    descriptionTextInputLayout.setError(null);
                    testforImage();
                }

            }
        });

        /*if (savedInstanceState != null) {

            thumbnail = savedInstanceState.getParcelable("prod_img");
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.header);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.fitCenter();

            Glide.with(ShopVendorsProduct.this).load(thumbnail)
                    .thumbnail(0.5f)
                    .apply(requestOptions)
                    .into(prodImg);
        }*/

    }

    //check Permission to take Images
    public void firstcheckPermission() {
        if (checkPermission()) {

        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions((Activity) this, new String[]
                {
                        CAMERA, ACCESS_FINE_LOCATION,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);

    }

    public boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(this, CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadLocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadContactsPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission && ReadLocationPermission && ReadContactsPermission) {
                        //  Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        // Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    //this is the method to select images from photo/gallery
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ShopVendorsProduct.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ShopVendorsProduct.this);

                if (items[item].equals("Take Photo")) {
                    userChosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(pickPhoto, "Select File"), SELECT_FILE);

    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    //this is the method to take images from camera
    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        prodImg.setImageBitmap(thumbnail);
        strFileNameOne = destination.getName();
        proImage = getStringImage(thumbnail);
    }

    //converting bitmap images to base64 format
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    //this is the method to take images from gallery
    private void onSelectFromGalleryResult(Intent data) {
        thumbnail = null;
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                Uri selectedImageUri = data.getData();
                String imageNameOne = getRealPathFromURI(selectedImageUri);

                File file = new File(imageNameOne);
                strFileNameOne = file.getName();
                //  Toast.makeText(getApplicationContext(), strFileNameOne + " " + " real ", Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        prodImg.setImageBitmap(thumbnail);
        proImage = getStringImage(thumbnail);
    }

    //Method to get Image path from gallery
    public String getRealPathFromURI(Uri contentUri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri,
                proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    //this is the method where to test the images while uploading
    public void testforImage() {
        if (thumbnail == null) {
            // Toast.makeText(ShopVendorsProduct.this, " Image not uploaded ", Toast.LENGTH_SHORT).show();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.addimage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapData = stream.toByteArray();

            String profiles = getStringImage(bitmap);

            profile = String.valueOf(profiles);
            checkConnection();
            claimRequestNow();
        } else if (thumbnail != null) {
            profile = getStringImage(thumbnail);
            checkConnection();
            claimRequestNow();
        }
    }

    //sending claim request to server
    public void claimRequestNow() {

        if (strFileNameOne.isEmpty()) {
            strFileNameOne = "filename not found";
        }

        StringRequest claimRequestNow = new StringRequest(Request.Method.POST, Constants.claimpointsBuyer,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject claimObject = new JSONObject(response);
                            String responseError = claimObject.getString("Error");
                            if (responseError.equals("false")) {
                                String responsemessage = claimObject.getString("Message");
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShopVendorsProduct.this);
                                alertDialogBuilder.setMessage("Your Claim request as been " + responsemessage);
                                alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        startActivity(new Intent(ShopVendorsProduct.this, HomePageActivity.class).
                                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                                String responseData = claimObject.getString("data");
                                JSONObject dataobj = new JSONObject(responseData);
                                String points_earned_id = dataobj.getString("points_earned_id");
                                String vendor_id = dataobj.getString("vendor_id");
                                String user_id = dataobj.getString("user_id");
                                String name = dataobj.getString("name");
                                String purchase_amount = dataobj.getString("purchase_amount");
                                String status = dataobj.getString("status");
                                String vendorName = dataobj.getString("vendor_name");
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
                    // Toast.makeText(ShopVendorsProduct.this, "Please check your internet connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("ServerError", "ServerError");
                    // The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
                    //  Toast.makeText(ShopVendorsProduct.this, "Server Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("AuthFailureError", "Authentication Error");
                    //If you are trying to do Http Basic authentication then this error is most likely to come.
                    Toast.makeText(ShopVendorsProduct.this, "Authentication Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("ParseError", "Parse Error");
                    //While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this exception will be generated. If you get this error then it is a problem that should be fixed instead of being handled.
                    Toast.makeText(ShopVendorsProduct.this, "Parse Error", Toast.LENGTH_LONG).show();
                } else if (error instanceof NoConnectionError) {
                    Log.d("NoConnectionError", "No connection");
                    // Similar to NetworkError, but fires when device does not have internet connection, your error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
                    Toast.makeText(ShopVendorsProduct.this, "No connection", Toast.LENGTH_LONG).show();
                } else if (error instanceof TimeoutError) {
                    Log.d("TimeoutError", "Timeout Error");
                    // Socket timeout, either server is too busy to handle the request or there is some network latency issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                    Toast.makeText(ShopVendorsProduct.this, " Timeout Error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShopVendorsProduct.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uId);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                params.put("hash", vendorHashData);
                params.put("description", description);
                params.put("prod_img", profile);
                params.put("filename", strFileNameOne);
                params.put("purchase_amount", amount);
                return params;
            }
        };
        claimRequestNow.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(claimRequestNow);
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

   /* @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        if (thumbnail != null) {
            state.putParcelable("prod_img", thumbnail);
        }

    }*/

}
