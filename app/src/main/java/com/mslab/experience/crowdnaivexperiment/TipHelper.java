package com.mslab.experience.crowdnaivexperiment;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import java.util.Random;

/**
 * Created by IFantace on 2015/10/31.
 */
public class TipHelper {
    public static int PlaySound(final Context context) {
        NotificationManager mgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification nt = new Notification();
        nt.defaults = Notification.DEFAULT_SOUND;
        int soundId = new Random(System.currentTimeMillis())
                .nextInt(Integer.MAX_VALUE);
        mgr.notify(soundId, nt);
        return soundId;
    }
}
