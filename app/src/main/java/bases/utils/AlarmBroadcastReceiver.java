package bases.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import bases.Constants;
import utils.PreferenceUtil;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static boolean isLaunched = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        isLaunched = true;
        // Stop Action

        final Intent activityIntent1 = new Intent(Constants.ACTIVITY_INTENT_FILTER);
        activityIntent1.putExtra("action", "refresh");
        activityIntent1.putExtra("second", "stopYT");
        context.sendBroadcast(activityIntent1);

        final Intent activityIntent2 = new Intent(Constants.INTENT_NOTIFICATION.REP_FILTER);
        activityIntent2.putExtra("action", Constants.INTENT_NOTIFICATION.ACTION_STOP);
        context.sendBroadcast(activityIntent2);

        PreferenceUtil.setBoolean(Constants.PREFERENCE.IS_ALARM_SET, false);
        Log.e("alarmCall", "Stopping media");
    }

    private void broadcastTime(Context context, Intent raw) {
        long currentTime = System.currentTimeMillis();
        Intent intent = new Intent(IntentConstants.INTENT_FILTER);
        Log.e("pIntents", raw.getExtras().toString());
        intent.putExtra(IntentConstants.CURRENT_TIME_KEY, currentTime);
        context.sendBroadcast(intent);
    }

    public interface IntentConstants {
        String INTENT_FILTER = "picklecode_intent_filter_alarm_crossmedia";
        String CURRENT_TIME_KEY = "picklecode_current_time_key_crossmedia";
    }
}