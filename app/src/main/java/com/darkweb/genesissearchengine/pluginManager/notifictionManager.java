package com.darkweb.genesissearchengine.pluginManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.darkweb.genesissearchengine.constants.status;
import com.darkweb.genesissearchengine.helperManager.eventObserver;
import com.darkweb.genesissearchengine.helperManager.userEngagementNotification;
import com.example.myapplication.R;
import java.util.List;
import static com.darkweb.genesissearchengine.constants.constants.*;

class notifictionManager
{
    /*Private Variables*/

    private AppCompatActivity mAppContext;

    /*Initializations*/

    notifictionManager(AppCompatActivity pAppContext, eventObserver.eventListener pEvent){
        this.mAppContext = pAppContext;
        onNotificationClear();
    }

    private void onNotificationClear(){
        NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(status.mNotificationID);
    }

    private void onCreateUserEngagementNotification(int pDelay){
        onSchedule(getNotification() , pDelay ) ;
    }

    private void onSchedule(Notification pNotification , int pDelay){
        Intent notificationIntent = new Intent( mAppContext, userEngagementNotification.class) ;
        notificationIntent.putExtra(CONST_NOTIFICATION_ID_NAME, CONST_NOTIFICATION_ID_VALUE) ;
        notificationIntent.putExtra(CONST_NOTIFICATION_ID_NAME, pNotification) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( mAppContext, CONST_NOTIFICATION_REQUEST_CODE, notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + pDelay ;
        AlarmManager alarmManager = (AlarmManager) mAppContext.getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
        alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;
    }

    private Notification getNotification () {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mAppContext, CONST_NOTIFICATION_ID_NAME) ;
        builder.setContentTitle(CONST_NOTIFICATION_TITLE) ;
        builder.setSmallIcon(R.drawable.notification_logo);
        builder.setAutoCancel(true) ;
        builder.setChannelId(CONST_NOTIFICATION_ID_NAME) ;
        return builder.build() ;
    }

    /*External Triggers*/

    Object onTrigger(List<Object> pData, pluginEnums.eNotificationManager pEventType) {
        if(pEventType.equals(pluginEnums.eNotificationManager.M_CREATE_NOTIFICATION))
        {
            onCreateUserEngagementNotification((int)pData.get(0));
        }
        else if(pEventType.equals(pluginEnums.eNotificationManager.M_CLEAR_NOTIFICATION))
        {
            onNotificationClear();
        }
        return null;
    }

}