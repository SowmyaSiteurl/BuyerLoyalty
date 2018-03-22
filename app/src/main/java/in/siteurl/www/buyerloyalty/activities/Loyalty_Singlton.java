package in.siteurl.www.buyerloyalty.activities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by siteurl on 22/12/17.
 */

public class Loyalty_Singlton {

    private static Loyalty_Singlton minstance;
    private static Context mctx;
    private RequestQueue requestQueue;


    private Loyalty_Singlton(Context context) {
        mctx = context;
        requestQueue = getRequestQueue();
    }


    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized Loyalty_Singlton getInstance(Context context) {
        if (minstance == null) {
            minstance = new Loyalty_Singlton(context);
        }
        return minstance;
    }

    public <T> void addtorequestqueue(Request<T> request) {
        requestQueue.add(request);
    }
}
