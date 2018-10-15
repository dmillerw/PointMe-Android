package dmillerw.me.pointmelistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static interface Callback {

        public void onNotification(boolean active, String distance, String text, String subtext, String icon);
    }

    private Callback callback;

    public NotificationBroadcastReceiver(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (callback != null) {
            callback.onNotification(
                    intent.getBooleanExtra("isNavigationActive", false),
                    intent.getStringExtra("stageDistance"),
                    intent.getStringExtra("stageText"),
                    intent.getStringExtra("stageSubtext"),
                    intent.getStringExtra("stageIcon"));
        }
    }
}
