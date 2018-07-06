package com.RKclassichaeven.tube;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ccmheaven.tube.ads.AdHelper;

import bases.SimpleCallback;

public class IntroActivity extends AppCompatActivity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    private static final int INTRO_DEFAULT_DELAY = 2000;

    private Handler intentHandler;
    private Runnable exitRunnable = new Runnable() {
        public void run() {
            System.exit(0);
        }
    };
    private Handler failHandler = new Handler();
    private Runnable failRunnable = new Runnable() {
        @Override
        public void run() {
            if(!isLoaded){
//                Toast.makeText(getApplicationContext(), "Unable to load an AD (KEY Expired or Load Failed)", Toast.LENGTH_LONG).show();
                goMain();
            }
        }
    };
    private boolean isLoaded = false;

    private Runnable introRunnable = new Runnable() {
        public void run() {
            failHandler.postDelayed(failRunnable, 5000);
            new AdHelper(IntroActivity.this).loadInterstitialAdWithOnLoad(new SimpleCallback() {
                @Override
                public void callback() {
                    goMain();
                }
            }, new SimpleCallback() {
                @Override
                public void callback() {
                    isLoaded = true;
                    failHandler.removeCallbacks(failRunnable);
                }
            });
        }
    };

    private void goMain(){
        Intent intent = new Intent(IntroActivity.this, RankActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        finish();
    }

    protected boolean canDrawOverlaysTest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final boolean isPermitted = onPermissionActivityResult(requestCode);
        if(isPermitted) {
            this.intentHandler.postDelayed(introRunnable, INTRO_DEFAULT_DELAY);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.intentHandler.removeCallbacks(introRunnable);
    }

    protected boolean onPermissionActivityResult(int requestCode){
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    return true;
                }
            } else {
                Toast.makeText(getApplicationContext(), "권한을 얻을 수 없어 앱을 종료합니다.", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 4000);
            }
        }
        return false;
    }

    protected boolean isDrawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        this.intentHandler = new Handler();
        final boolean isDrawable = isDrawable();

        if(isDrawable) {
            this.intentHandler.postDelayed(introRunnable, INTRO_DEFAULT_DELAY);
        }else{
            Toast.makeText(getApplicationContext(), "앱 실행에 필요한 권한에 동의하시기 바랍니다.", Toast.LENGTH_LONG).show();
            canDrawOverlaysTest();
        }
    }
}
