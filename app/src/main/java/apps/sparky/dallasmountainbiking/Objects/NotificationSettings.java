package apps.sparky.dallasmountainbiking.Objects;

import com.orm.SugarRecord;

/**
 * Created by David.Kobuszewski on 2/1/2016.
 */
public class NotificationSettings extends SugarRecord {
    public String preference;

    public NotificationSettings(){}
    public NotificationSettings(String preference){
        this.preference = preference;
    }
}
