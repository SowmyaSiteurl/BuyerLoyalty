package in.siteurl.www.buyerloyalty.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static android.Manifest.permission.CALL_PHONE;

/**
 * Created by siteurl on 26/12/17.
 */

public class CalltoVendor extends AppCompatActivity {


    String number;
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting data from other activity
        number = getIntent().getStringExtra("phonenumber");

        //implicit intent
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(callIntent);
    }

    //method to check the permission
    private void requestPermission() {

        ActivityCompat.requestPermissions(CalltoVendor.this, new String[]
                {
                        CALL_PHONE
                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission) {
                        Toast.makeText(CalltoVendor.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CalltoVendor.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

