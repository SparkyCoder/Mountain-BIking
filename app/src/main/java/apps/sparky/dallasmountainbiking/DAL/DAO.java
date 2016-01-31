package apps.sparky.dallasmountainbiking.DAL;

import android.database.sqlite.SQLiteException;

import java.util.List;

import apps.sparky.dallasmountainbiking.Objects.DmbUser;
import apps.sparky.dallasmountainbiking.Objects.Favorites;
import apps.sparky.dallasmountainbiking.Objects.IntervalSetting;
import apps.sparky.dallasmountainbiking.Objects.SugarTrail;
import apps.sparky.dallasmountainbiking.Objects.Trail;

/**
 * Created by david.kobuszewski on 1/18/2016.
 */
public class DAO {

    public void AddNewUser(String ID, String Name, String ProfilePictureUrl, String Email){
        DmbUser newUser = new DmbUser(ID, Name, ProfilePictureUrl, Email);
        newUser.save();
    }

    public List<DmbUser> GetUser() {
        try {
            return DmbUser.listAll(DmbUser.class);
        } catch (SQLiteException ex) {
            String message = ex.getMessage();
        }
        return null;
    }

    public IntervalSetting GetIntervalSettings() {
        try {
            List<IntervalSetting> results = IntervalSetting.find(IntervalSetting.class, "interval_setting = ?", "BackgroundService");

            if(results.size() == 1)
                return results.get(0);
            else
                return null;
        } catch (SQLiteException ex) {
            String message = ex.getMessage();
        }
        return null;
    }

    public SugarTrail GetTrailsByID(String trailID) {
        try {
            List<SugarTrail> results = SugarTrail.find(SugarTrail.class, "trail_id = ?", trailID);

            if(results.size() == 1)
                return results.get(0);
            else
                return null;

        } catch (SQLiteException ex) {
            String message = ex.getMessage();
        }
        return null;
    }

    public List<SugarTrail> GetTrails(){
        try {
            return SugarTrail.listAll(SugarTrail.class);
        } catch (SQLiteException ex) {
            String message = ex.getMessage();
        }
        return null;
    }

    public List<Favorites> GetFavorites(){
        try {
            return Favorites.listAll(Favorites.class);
        } catch (SQLiteException ex) {
            String message = ex.getMessage();
        }
        return null;
    }

    public void DeleteFavoriteByTrailIDAndUserID(String trailID, String userID) {
        Favorites.deleteAll(Favorites.class, "trail_id = ? and user_id = ?", trailID, userID);
    }


    public void DeleteUsers(){
        DmbUser.deleteAll(DmbUser.class);
    }
}
