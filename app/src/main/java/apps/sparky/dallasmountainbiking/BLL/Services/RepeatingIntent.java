package apps.sparky.dallasmountainbiking.BLL.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import apps.sparky.dallasmountainbiking.BLL.Repositories.BackgroundServiceRepository;

/**
 * Created by david.kobuszewski on 1/28/2016.
 */
public class RepeatingIntent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction() !=null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            SetAlarm(context);
        }

        SetAlarm(context);
        CheckForUpdates(context);
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, RepeatingIntent.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, 5000, 1000 * 60 * 5, pi);
    }

    private void CheckForUpdates(Context context){
        Toast.makeText(context, "Dallas Mountain Biking - Checking For Updates...", Toast.LENGTH_LONG).show();

        BackgroundServiceRepository serviceRepository = new BackgroundServiceRepository(context);
        serviceRepository.execute();
    }
}
