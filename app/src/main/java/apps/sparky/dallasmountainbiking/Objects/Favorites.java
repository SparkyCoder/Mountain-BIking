package apps.sparky.dallasmountainbiking.Objects;

import com.orm.SugarRecord;

/**
 * Created by David.Kobuszewski on 1/25/2016.
 */
public class Favorites extends SugarRecord {
    public String TrailID;
    public String UserID;

    public Favorites(){}

    public Favorites(String trailID, String userID){
        this.TrailID = trailID;
        this.UserID = userID;
    }
}
