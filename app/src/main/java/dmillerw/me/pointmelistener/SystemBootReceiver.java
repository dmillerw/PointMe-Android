package dmillerw.me.pointmelistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SystemBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, WebServerService.class);
        context.startService(serviceIntent);
    }
}
