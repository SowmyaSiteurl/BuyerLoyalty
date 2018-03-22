package in.siteurl.www.buyerloyalty.pushNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.activities.IndivisualVendorDataWithOffers;
import in.siteurl.www.buyerloyalty.activities.Loyalty_Singlton;
import in.siteurl.www.buyerloyalty.activities.SplashScreen;
import in.siteurl.www.buyerloyalty.apis.Constants;
import in.siteurl.www.buyerloyalty.core.Vendors;

/**
 * Created by siteurl on 13/1/18.
 */

public class buyerLoyaltyFirebaseMessaginfServices extends FirebaseMessagingService {

    String NameOfVendor;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    ArrayList<Vendors> venderarrayalllist = new ArrayList<Vendors>();
    ArrayList<String> vendorIds = new ArrayList<>();
    ArrayList<String> vendorNames = new ArrayList<>();
    ArrayList<JSONObject> rawvendorlist = new ArrayList<>();
    String Strng_vnderuser_id, Strng_name, Strng_reciveResponse;
    String img_url;

    String Vendor_User_id,Vendor_getName;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //shared preferences to save the data
        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uid = sharedPreferences.getString("User-id", null);
        editor = sharedPreferences.edit();

        if (remoteMessage.getData().size() > 0) {

            img_url = remoteMessage.getData().get("img_url");
            NameOfVendor = remoteMessage.getData().get("vendorName");
            vendorDetails();
        }
    }

    private void vendorDetails() {

        StringRequest vendorDetails = new StringRequest(Request.Method.POST, Constants.allvendors, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject jsonObject = null;
                Strng_reciveResponse = response;

                try {
                    jsonObject = new JSONObject(response);
                    String response_error = jsonObject.getString("Error");

                    if (response_error.equals("false")) {
                        JSONArray data = jsonObject.getJSONArray("data");

                        if (data.length() > 0) {

                            rawvendorlist.clear();
                            venderarrayalllist.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject data_object = data.getJSONObject(i);
                                Strng_vnderuser_id = data_object.getString("user_id");
                                Strng_name = data_object.getString("name");
                                String email = data_object.getString("email");
                                String phone = data_object.getString("phone");
                                String address = data_object.getString("address");
                                String hash = data_object.getString("hash");
                                String store_image = data_object.getString("store_image");
                                vendorIds.add(Strng_vnderuser_id);
                                vendorNames.add(Strng_name);

                                rawvendorlist.add(data_object);
                                venderarrayalllist.add(new Vendors(store_image, Strng_vnderuser_id, Strng_name, email, phone, address, hash));
                            }

                            for (int j = 0; j < vendorNames.size(); j++) {
                                if (vendorNames.get(j).equalsIgnoreCase(NameOfVendor)) {

                                    Vendor_User_id = String.valueOf(venderarrayalllist.get(j).getVnderuser_id());
                                    Vendor_getName =String.valueOf(venderarrayalllist.get(j).getName());
                                    Notification(Vendor_User_id,Vendor_getName);
                                    /*Intent intent = new Intent(buyerLoyaltyFirebaseMessaginfServices.this, IndivisualVendorDataWithOffers.class);
                                    intent.putExtra("individialvenderid", String.valueOf(venderarrayalllist.get(j).getVnderuser_id()));
                                    intent.putExtra("vendorName", String.valueOf(venderarrayalllist.get(j).getName()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    buyerLoyaltyFirebaseMessaginfServices.this.startActivity(intent);*/

                                }
                            }

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
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }

        };
        vendorDetails.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getApplicationContext()).addtorequestqueue(vendorDetails);
    }

    public void Notification(String Vendor_User_id,String Vendor_getName) {
        if (!Strng_reciveResponse.equals(null))
        {
            Intent intent = new Intent(buyerLoyaltyFirebaseMessaginfServices.this, IndivisualVendorDataWithOffers.class);
            intent.putExtra("individialvenderid",Vendor_User_id);// String.valueOf(venderarrayalllist.get(j).getVnderuser_id()));
            intent.putExtra("vendorName", Vendor_getName);//String.valueOf(venderarrayalllist.get(j).getName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(buyerLoyaltyFirebaseMessaginfServices.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(buyerLoyaltyFirebaseMessaginfServices.this);
            //  notificationBuilder.setContentTitle(remoteMessage.getData().get("title"));
            //  notificationBuilder.setContentText(remoteMessage.getData().get("body"));
            notificationBuilder.setContentTitle("Buyer Loyalty");
            notificationBuilder.setContentText("Offers");
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.drawable.ic_card_giftcard_white_24dp);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);

            ImageRequest imageRequest = new ImageRequest(img_url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {

                    notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());

                }
            }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            Loyalty_Singlton.getInstance(buyerLoyaltyFirebaseMessaginfServices.this).addtorequestqueue(imageRequest);

        }
        else
        {
            Intent intent = new Intent(buyerLoyaltyFirebaseMessaginfServices.this, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(buyerLoyaltyFirebaseMessaginfServices.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(buyerLoyaltyFirebaseMessaginfServices.this);
            // notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            //  notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
            notificationBuilder.setContentTitle("Buyer Loyalty");
            notificationBuilder.setContentText("Offers");
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.drawable.ic_card_giftcard_white_24dp);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());

        }

    }

}