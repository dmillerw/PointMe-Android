package dmillerw.me.pointmelistener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {

    private static final String TAG = NotificationListener.class.getName();
    private static final String MAPS_PACKAGE = "com.google.android.apps.maps";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        // android.title
        // android.text
        // android.icon

        if (!sbn.getPackageName().contains(MAPS_PACKAGE))
            return;


        Intent intent = null;
        View notificationView;

        ArrayDeque<ViewGroup> groups = new ArrayDeque<>();

        Map<String, String> data = new HashMap<>();

        try {
            Context packageContext = getApplicationContext().createPackageContext(MAPS_PACKAGE, CONTEXT_IGNORE_SECURITY);
            notificationView = ((LayoutInflater)packageContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(sbn.getNotification().bigContentView.getLayoutId(), null);

            notificationView = sbn.getNotification().bigContentView.apply(getApplicationContext(), (ViewGroup) notificationView);

            for (int i = 0; i < ((ViewGroup) notificationView).getChildCount(); i++) {
                View child = ((ViewGroup)notificationView).getChildAt(i);
                if (child instanceof ViewGroup) {
                    groups.push((ViewGroup) child);
                } else {
                    String name;
                    try {
                        name = packageContext.getResources().getResourceName(child.getId());
                    } catch (Exception ex) {
                        continue;
                    }

                    if (child instanceof TextView) {
                        data.put(name, ((TextView) child).getText().toString());
                    } else if (child instanceof ImageView) {
                        data.put(name, getBase64FromDrawable(((ImageView) child).getDrawable()));
                    }
                }
            }

            while (!groups.isEmpty()) {
                ViewGroup group = groups.pop();
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    if (child instanceof ViewGroup) {
                        groups.push((ViewGroup) child);
                    } else {
                        String name;
                        try {
                            name = packageContext.getResources().getResourceName(child.getId());
                        } catch (Exception ex) {
                            continue;
                        }

                        if (child instanceof TextView) {
                            data.put(name, ((TextView) child).getText().toString());
                        } else if (child instanceof ImageView) {
                            data.put(name, getBase64FromDrawable(((ImageView) child).getDrawable()));
                        }
                    }
                }
            }

            intent = new Intent(WebServerService.INTENT);
            intent.putExtra("isNavigationActive", true);
            intent.putExtra("stageDistance", data.get("com.google.android.apps.maps:id/nav_title"));
            intent.putExtra("stageText", data.get("com.google.android.apps.maps:id/nav_description"));
            intent.putExtra("stageSubtext", data.get("com.google.android.apps.maps:id/nav_time"));
            intent.putExtra("stageIcon", data.get("com.google.android.apps.maps:id/nav_notification_icon"));

            sendBroadcast(intent);
            return;
        } catch (Exception ex) {
            Log.e(TAG, "Failed to get notification view", ex);
            intent = getFallbackIntent(sbn.getNotification().extras);
        }

        if (intent != null)
            sendBroadcast(intent);
    }

    private Intent getFallbackIntent(Bundle extras) {
        String icon = null;

        Intent intent = new Intent(WebServerService.INTENT);
        intent.putExtra("isNavigationActive", true);
        intent.putExtra("stageDistance", "-");
        intent.putExtra("stageText", extras.getString("android.title"));
        intent.putExtra("stageSubtext", extras.getString("android.text"));

        if (icon != null) {
            intent.putExtra("stageIcon", icon);
        }

        return intent;
    }

    private String getBase64FromDrawable(Drawable drawable) {
        String icon = "";

        try {
            if (drawable instanceof BitmapDrawable) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, baos);
                icon = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return icon;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        Intent intent = new Intent(WebServerService.INTENT);
        intent.putExtra("isNavigationActive", false);
    }
}
