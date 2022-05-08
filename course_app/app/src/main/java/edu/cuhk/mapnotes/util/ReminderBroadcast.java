package edu.cuhk.mapnotes.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationUtil util = new NotificationUtil(context);
        NotificationCompat.Builder builder = util.setNotification("Testing", "Testing notification system");
        util.getManager().notify(101, builder.build());
    }
}