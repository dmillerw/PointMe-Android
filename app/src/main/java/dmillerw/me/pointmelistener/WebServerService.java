package dmillerw.me.pointmelistener;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class WebServerService extends Service implements NotificationBroadcastReceiver.Callback {

    public static final String INTENT = "me.dmillerw.pointme";

    private WebServer webServer;
    private NotificationBroadcastReceiver notificationReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        webServer = new WebServer();
        try {
            webServer.start();
        } catch (Exception ex) {
            Log.e(WebServerService.class.getName(), "Failed to start webserver", ex);
        }

        notificationReceiver = new NotificationBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT);

        registerReceiver(notificationReceiver, filter);
    }

    @Override
    public void onDestroy() {
        webServer.stop();
        unregisterReceiver(notificationReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onNotification(boolean active, String distance, String text, String subtext, String icon) {
        if (webServer != null) {
            if (!active)
                webServer.update(false);
            else
                webServer.update(distance, text, subtext, icon);
        }
    }
}
