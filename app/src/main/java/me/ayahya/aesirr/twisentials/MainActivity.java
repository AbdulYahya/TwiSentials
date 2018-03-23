package me.ayahya.aesirr.twisentials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crash.FirebaseCrash;

public class MainActivity extends AppCompatActivity {
    public final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.mobile_ads_APP_ID));

//        ## Firebase Crashalytics usage example ##
//        FirebaseCrash.log("Activity created");
//        FirebaseCrash.logcat(Log.ERROR, TAG, "NPE caught");
//        FirebaseCrash.report(ex);
    }
}
