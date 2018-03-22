package in.siteurl.www.buyerloyalty.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import in.siteurl.www.buyerloyalty.R;


public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String loginUserId, sessionId, uId;

    private static int SPLASH_TIME_OUT = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseMessaging.getInstance().subscribeToTopic("global");

        sharedPreferences = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = sharedPreferences.getString("sessionid", null);
        uId = sharedPreferences.getString("User-id", null);
        editor = sharedPreferences.edit();

        new Handler().postDelayed(new Runnable() {

			/*
             * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

            @Override
            public void run() {
                if (sharedPreferences.contains("User-id")) {
                    loginUserId = sharedPreferences.getString("User-id", null);
                    if (loginUserId.equals("") || loginUserId.equals(null)) {
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreen.this, HomePageActivity.class));
                    }
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
