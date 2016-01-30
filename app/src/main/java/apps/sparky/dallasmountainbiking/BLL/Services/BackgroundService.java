package apps.sparky.dallasmountainbiking.BLL.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by david.kobuszewski on 1/28/2016.
 */
public class BackgroundService extends Service {

    public BackgroundService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        new RepeatingIntent().SetAlarm(this);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
