package apps.sparky.dallasmountainbiking.BLL.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import apps.sparky.dallasmountainbiking.BLL.Repositories.BackgroundServiceRepository;
import apps.sparky.dallasmountainbiking.DAL.DAO;
import apps.sparky.dallasmountainbiking.MainActivity;
import apps.sparky.dallasmountainbiking.Objects.IntervalSetting;

/**
 * Created by david.kobuszewski on 1/28/2016.
 */
public class RepeatingIntent extends BroadcastReceiver {
    private DAO dao;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction() !=null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            SetAlarm(context);
        }

        CheckForUpdates(context);
    }

    public void SetAlarm(Context context)
    {
        dao = new DAO();

        IntervalSetting currentSetting = dao.GetIntervalSettings();

        long value = (1000 * 60 * 60 * 5);

        if(currentSetting != null)
            value = currentSetting.intervalValue;

Intent intent = new Intent(context.getApplicationContext(), RepeatingIntent.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 1000, value, pendingIntent);
    }

    private void CheckForUpdates(Context context){
        BackgroundServiceRepository serviceRepository = new BackgroundServiceRepository(context);
        serviceRepository.execute();
    }
}
