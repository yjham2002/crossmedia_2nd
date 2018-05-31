package bases;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class BaseApp extends Application {

    private boolean mBounded;

    public static final String ADMOB_AD_ID = "ca-app-pub-1846833106939117~4067137440";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("BaseApp", "onCreate");
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
    }

}
