package in.siteurl.www.buyerloyalty.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.siteurl.www.buyerloyalty.R;

public class VendorMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    double latFromMap, lngFromMap;
    String getLat, getLong, markerAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_map);

        //getting data from other activity
        getLat = getIntent().getStringExtra("laty");
        getLong = getIntent().getStringExtra("longy");
        markerAddress = getIntent().getStringExtra("address");

        if (markerAddress.equals("Showing Mysore")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("GPS NOT UPDATED!!");
            alertDialogBuilder.setMessage("Please contact vendor by call..");
            alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    startActivity(new Intent(VendorMapActivity.this, HomePageActivity.class).
                            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.searchmap);
        mapFragment.getMapAsync((OnMapReadyCallback) this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        if (!getLat.equals(0.0) || !getLong.equals(0.0)) {
            lngFromMap = Double.parseDouble(getLat);
            latFromMap = Double.parseDouble(getLong);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            LatLng Mysore = new LatLng(lngFromMap, latFromMap);
            final LatLng markerLoc = new LatLng(lngFromMap, latFromMap);
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(markerLoc)
                    .zoom(16)
                    .bearing(0)
                    .tilt(45)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 3000, null);

            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lngFromMap, latFromMap))//12.2958, 76.6394))
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_store_black_36dp))
                    .title(markerAddress));
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        } else {

            LatLng sydney = new LatLng(12.2958, 76.6394);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Mysore"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            CameraPosition googlePlex = CameraPosition.builder()
                    .target(sydney)
                    .zoom(16)
                    .bearing(90)
                    .tilt(45)
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null);

        }

    }
}



