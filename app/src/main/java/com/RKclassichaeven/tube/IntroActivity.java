package com.RKclassichaeven.tube;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ccmheaven.tube.ads.AdHelper;

import bases.SimpleCallback;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        new AdHelper(IntroActivity.this).loadInterstitialAd(new SimpleCallback() {
            @Override
            public void callback() {
                Intent intent = new Intent(IntroActivity.this, RankActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                finish();
            }
        });
    }
}
