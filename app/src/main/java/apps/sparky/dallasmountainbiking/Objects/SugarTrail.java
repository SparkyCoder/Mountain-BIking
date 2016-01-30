package apps.sparky.dallasmountainbiking.Objects;

import com.orm.SugarRecord;

/**
 * Created by david.kobuszewski on 1/28/2016.
 */
public class SugarTrail extends SugarRecord{
    public String trailId;
    public String currentStatus;

    public SugarTrail(){}
    public SugarTrail(String trailId, String currentStatus){
        this.trailId = trailId;
        this.currentStatus = currentStatus;
    }
}
