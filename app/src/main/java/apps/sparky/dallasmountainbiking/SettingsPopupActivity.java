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

        IntervalSetting currentSetting = dao.GetIntervalSettings();

        if (currentSetting == null) {
            settings.check(R.id.mediumInterval);
        }
        if (currentSetting.intervalValue == 1000 * 60 * 60 * 10) {
            settings.check(R.id.lowInterval);
        }
        if (currentSetting.intervalValue == 1000 * 60 * 60 * 1) {
            settings.check(R.id.highInterval);
        }
        if (currentSetting.intervalValue == 1000 * 60 * 60 * 5) {
            settings.check(R.id.mediumInterval);
        }
    }

    private void SetupClicks() {
        final RadioGroup settings = (RadioGroup) findViewById(R.id.settingsGroup);

        settings.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                try {
                    IntervalSetting currentSetting = dao.GetIntervalSettings();

                    long value = 1000 * 60 * 60 * 5;

                    RadioButton radiobutton = (RadioButton) findViewById(checkedId);
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
    }

    private void CancelService() {
        PendingIntent intent = PendingIntent.getBroadcast(this, 0,
                new Intent(this, RepeatingIntent.class),
                PendingIntent.FLAG_NO_CREATE);

        if (intent != null)
            intent.cancel();
    }
}
