package com.example.foodoo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private NotificationManager mManager;
    private Context mContext;
    private static final String CHANNEL_ID = "foodoo_notification_channel";
    private final int NOTIFICATION_ID = 0;

    public NotificationHandler(Context context) {
        this.mContext = context;
        this.mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foodoo Notification", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.RED);
        channel.setDescription("Notifications from Foodoo.");
        this.mManager.createNotificationChannel(channel);
    }

    public void send(String title, String message) {
        Intent intent = new Intent(mContext, FoodsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.baseline_food_bank_24);

        this.mManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancel() {
        this.mManager.cancel(NOTIFICATION_ID);
    }
}
