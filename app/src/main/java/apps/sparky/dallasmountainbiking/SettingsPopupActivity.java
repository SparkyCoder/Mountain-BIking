package apps.sparky.dallasmountainbiking;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Services.BackgroundService;
import apps.sparky.dallasmountainbiking.BLL.Services.RepeatingIntent;
import apps.sparky.dallasmountainbiking.DAL.DAO;
import apps.sparky.dallasmountainbiking.Objects.IntervalSetting;
import apps.sparky.dallasmountainbiking.Objects.NotificationSettingValues;
import apps.sparky.dallasmountainbiking.Objects.NotificationSettings;

public class SettingsPopupActivity extends Activity {
    private DAO dao;
private ExceptionHandling exceptionHandling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_popup);

        Initialize();
        SetupClicks();
    }

    private void Initialize() {
        dao = new DAO();
        exceptionHandling = new ExceptionHandling(this);
        SetupDefaultSelectedButton();
    }

    private void SetupDefaultSelectedButton() {
        RadioGroup settings = (RadioGroup) findViewById(R.id.settingsGroup);
        RadioGroup notificationsSettings = (RadioGroup) findViewById(R.id.notificationsGroup);

        IntervalSetting currentSetting = dao.GetIntervalSettings();
        NotificationSettings currentNotificationsSetting = dao.GetNotificationSettings();

        if (currentSetting == null) {
            settings.check(R.id.mediumInterval);
        }
        else if (currentSetting.intervalValue == 1000 * 60 * 60 * 10) {
            settings.check(R.id.lowInterval);
        }
        else if (currentSetting.intervalValue == 1000 * 60 * 60 * 1) {
            settings.check(R.id.highInterval);
        }
        else if (currentSetting.intervalValue == 1000 * 60 * 60 * 5) {
            settings.check(R.id.mediumInterval);
        }



        if (currentNotificationsSetting == null) {
            notificationsSettings.check(R.id.allTrailNotifications);
        }
        else if (currentNotificationsSetting.preference.equals(NotificationSettingValues.Preferences.ALL.toString())) {
            notificationsSettings.check(R.id.allTrailNotifications);
        }
        else if (currentNotificationsSetting.preference.equals(NotificationSettingValues.Preferences.FAVORITES.toString())) {
            notificationsSettings.check(R.id.favoritedTrailNotificaions);
        }
        else if (currentNotificationsSetting.preference.equals(NotificationSettingValues.Preferences.NEVER.toString())) {
            notificationsSettings.check(R.id.noPushNotifications);
        }
    }

    private void SetupClicks() {
        final RadioGroup settings = (RadioGroup) findViewById(R.id.settingsGroup);
        final RadioGroup pushNotifications = (RadioGroup) findViewById(R.id.notificationsGroup);

        settings.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    IntervalSetting currentSetting = dao.GetIntervalSettings();

                    long value = 1000 * 60 * 60 * 5;

                    if (checkedId == R.id.highInterval)
                        value = 1000 * 60 * 60 * 1;
                    if (checkedId == R.id.mediumInterval)
                        value = 1000 * 60 * 60 * 5;
                    if (checkedId == R.id.lowInterval)
                        value = 1000 * 60 * 60 * 10;


                    if (currentSetting == null) {
                        IntervalSetting newSetting = new IntervalSetting("BackgroundService", value);
                        newSetting.save();
                    } else {
                        currentSetting.intervalValue = value;
                        currentSetting.save();
                    }

                    CancelService();
                    startService(new Intent(getApplicationContext(), BackgroundService.class));
                } catch (Exception ex) {
                    exceptionHandling.ShowToast(ex.getMessage());
                }
            }
        });



        pushNotifications.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    NotificationSettings currentSetting = dao.GetNotificationSettings();

                    NotificationSettingValues.Preferences value = NotificationSettingValues.Preferences.FAVORITES;

                    if (checkedId == R.id.allTrailNotifications)
                        value = NotificationSettingValues.Preferences.ALL;
                        if (checkedId == R.id.favoritedTrailNotificaions)
                            value = NotificationSettingValues.Preferences.FAVORITES;
                            if (checkedId == R.id.noPushNotifications)
                                value = NotificationSettingValues.Preferences.NEVER;


                                if (currentSetting == null) {
                                    NotificationSettings newSetting = new NotificationSettings(value.toString());
                                    newSetting.save();
                                } else {
                                    currentSetting.preference = value.toString();
                                    currentSetting.save();
                                }

                    CancelService();
                    startService(new Intent(getApplicationContext(), BackgroundService.class));
                } catch (Exception ex) {
                    exceptionHandling.ShowToast(ex.getMessage());
                }
            }
        });
    }

    private void CancelService() {
        PendingIntent intent = PendingIntent.getBroadcast(this, 0,
                new Intent(this, RepeatingIntent.class),
                PendingIntent.FLAG_NO_CREATE);

        if (intent != null)
            intent.cancel();
    }
}
