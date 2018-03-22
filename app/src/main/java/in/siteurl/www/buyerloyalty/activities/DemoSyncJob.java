package in.siteurl.www.buyerloyalty.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import in.siteurl.www.buyerloyalty.R;
import in.siteurl.www.buyerloyalty.apis.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 11/1/18.
 */

public class DemoSyncJob extends Job {

    public static final String TAG = "job_demo_tag";

    NotificationManager CnNotificationManager;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid, res;
    ArrayList<String> ids = new ArrayList<>();
    String carryID, pointsIds;

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        // run your job here
        loginPref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("sessionid", null);
        uid = loginPref.getString("User-id", null);
        editor = loginPref.edit();

        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!sessionId.equals(null) && (isConnected == true)) {

            testNotification();
            declineNotification();
        }

        return Result.SUCCESS;
    }


    public static void scheduleJob() {
        new JobRequest.Builder(DemoSyncJob.TAG)
                .setPeriodic(setPeriod())
                .setRequiredNetworkType(JobRequest.NetworkType.ANY)
                .setPersisted(true)
                .build()
                .schedule();
    }

    public static long setPeriod() {
        int currentApi = Build.VERSION.SDK_INT;
        if (currentApi == Build.VERSION_CODES.M) {
            return 60000;
        }
        return 900000;
    }

    //for Nougat 15 min once it's calling

    public void declineNotification() {

        StringRequest declineNotification = new StringRequest(Request.Method.POST, Constants.declineNotification,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String error = jsonObject.getString("Error");
                            String message = jsonObject.getString("Message");
                            String data = jsonObject.getString("data");

                            JSONArray jsonArray = new JSONArray(data);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject pointsData = jsonArray.getJSONObject(i);

                                String status = pointsData.getString("status");

                                if (!status.equals("Declined")) {

                                    CounterNotification(getContext(), "Loyalty User", "Claim Declined", " purchase notification");
                                }
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("api_key", Constants.APIKEY);

                return params;
            }
        };
        declineNotification.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getContext()).addtorequestqueue(declineNotification);

    }


    public void testNotification() {

        StringRequest testNotification = new StringRequest(Request.Method.POST, Constants.notifyuser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ids.clear();
                        res = response;

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String data = jsonObject.getString("data");
                            JSONArray jsonArray = new JSONArray(data);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObject = jsonArray.getJSONObject(i);
                                String status = dataObject.getString("status");
                                pointsIds = dataObject.getString("points_earned_id");

                                if (status.equals("Approved") || status.equals("approved")) {
                                    ids.add(pointsIds);
                                }
                            }

                            CounterNotification(getContext(), "Loyalty User", "Claim Approved", "New purchase notification");
                            for (int j = 0; j < ids.size(); j++) {
                                if (ids.size() > 0) {
                                    carryID = ids.get(j);
                                    changeStatus();
                                }
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("api_key", Constants.APIKEY);

                return params;
            }
        };
        testNotification.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getContext()).addtorequestqueue(testNotification);
    }

    private void changeStatus() {
        StringRequest continue_register = new StringRequest(Request.Method.POST, Constants.updatestatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), "here", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                params.put("points_earned_id", carryID);
                params.put("status", "Completed");
                return params;
            }
        };
        continue_register.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Loyalty_Singlton.getInstance(getContext()).addtorequestqueue(continue_register);
    }

    public void CounterNotification(Context context, String msg, String msgtxt, String msgAlerts) {
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 1, new Intent(context, PointsHistory.class), 0);
        android.support.v4.app.NotificationCompat.Builder mcBuilder = new android.support.v4.app.NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(msgAlerts)
                .setContentTitle(msg)
                .setContentText(msgtxt)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent2);
        mcBuilder.setContentIntent(pendingIntent2);
        mcBuilder.setDefaults(android.support.v4.app.NotificationCompat.DEFAULT_SOUND);
        mcBuilder.setAutoCancel(true);
        CnNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CnNotificationManager.notify(1, mcBuilder.build());
    }

}