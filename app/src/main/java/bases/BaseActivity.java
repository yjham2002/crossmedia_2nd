package bases;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Base class for activities
 * @author EuiJin.Ham
 * @version 1.0.0
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    private InterstitialAd mInterstitialAd = null;

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

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

    protected boolean onPermissionActivityResult(int requestCode){
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    return true;
                }
            } else {
                showToast("권한을 얻을 수 없어 앱을 종료합니다.");
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

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view){
        Log.e(this.getClass().getSimpleName(), "Override onClick method in BaseActivity to use View.OnClickListener");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        commonInit();
    }

    private void commonInit(){
    }

    protected boolean isNetworkEnable(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    /**
     * Bind itself as a OnClickListener on parameters
     * @param views view objects to bind a listener
     */
    protected void setClick(View... views){
        for(View view : views) {
            if(view != null) view.setOnClickListener(this);
        }
    }

}
