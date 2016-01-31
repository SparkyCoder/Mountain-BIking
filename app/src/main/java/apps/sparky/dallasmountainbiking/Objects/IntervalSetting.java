package apps.sparky.dallasmountainbiking.Objects;

import com.orm.SugarRecord;

/**
 * Created by david.kobuszewski on 1/30/2016.
 */
public class IntervalSetting extends SugarRecord {
    public String intervalSetting;
    public long intervalValue;

    public IntervalSetting(){}
    public IntervalSetting(String intervalSetting, long intervalValue){
        this.intervalSetting = intervalSetting;
        this.intervalValue = intervalValue;
    }
}
