package apps.sparky.dallasmountainbiking.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

/**
 * Created by david.kobuszewski on 1/18/2016.
 */
public class Trail implements Parcelable {
    private String conditionDesc;
    private String currentCondition;
    private String currentStatus;
    private String facebook;
    private String geoLang;
    private String geoLat;
    private String landOwner;
    private String openClose;
    private String trailAddress;
    private String trailCity;
    private String trailDesc;
    private String trailId;
    private String trailName;
    private String twitter;
    private String updateFormatted;

    protected Trail(Parcel in) {
        conditionDesc = in.readString();
        currentCondition = in.readString();
        currentStatus = in.readString();
        facebook = in.readString();
        geoLang = in.readString();
        geoLat = in.readString();
        landOwner = in.readString();
        openClose = in.readString();
        trailAddress = in.readString();
        trailCity = in.readString();
        trailDesc = in.readString();
        trailId = in.readString();
        trailName = in.readString();
        twitter = in.readString();
        updateFormatted = in.readString();
    }

    public static final Creator<Trail> CREATOR = new Creator<Trail>() {
        @Override
        public Trail createFromParcel(Parcel in) {
            return new Trail(in);
        }

        @Override
        public Trail[] newArray(int size) {
            return new Trail[size];
        }
    };

    public String getConditionDesc(){
        return this.conditionDesc;
    }
    public void setConditionDesc(String conditionDesc){
        this.conditionDesc = conditionDesc;
    }
    public String getCurrentCondition(){
        return this.currentCondition;
    }
    public void setCurrentCondition(String currentCondition){
        this.currentCondition = currentCondition;
    }
    public String getCurrentStatus(){
        return this.currentStatus;
    }
    public void setCurrentStatus(String currentStatus){
        this.currentStatus = currentStatus;
    }
    public String getFacebook(){
        return this.facebook;
    }
    public void setFacebook(String facebook){
        this.facebook = facebook;
    }
    public String getGeoLang(){
        return this.geoLang;
    }
    public void setGeoLang(String geoLang){
        this.geoLang = geoLang;
    }
    public String getGeoLat(){
        return this.geoLat;
    }
    public void setGeoLat(String geoLat){
        this.geoLat = geoLat;
    }
    public String getLandOwner(){
        return this.landOwner;
    }
    public void setLandOwner(String landOwner){
        this.landOwner = landOwner;
    }
    public String getOpenClose(){
        return this.openClose;
    }
    public void setOpenClose(String openClose){
        this.openClose = openClose;
    }
    public String getTrailAddress(){
        return this.trailAddress;
    }
    public void setTrailAddress(String trailAddress){
        this.trailAddress = trailAddress;
    }
    public String getTrailCity(){
        return this.trailCity;
    }
    public void setTrailCity(String trailCity){
        this.trailCity = trailCity;
    }
    public String getTrailDesc(){
        return this.trailDesc;
    }
    public void setTrailDesc(String trailDesc){
        this.trailDesc = trailDesc;
    }
    public String getTrailId(){
        return this.trailId;
    }
    public void setTrailId(String trailId){
        this.trailId = trailId;
    }
    public String getTrailName(){
        return this.trailName;
    }
    public void setTrailName(String trailName){
        this.trailName = trailName;
    }
    public String getTwitter(){
        return this.twitter;
    }
    public void setTwitter(String twitter){
        this.twitter = twitter;
    }
    public String getUpdateFormatted(){
        return this.updateFormatted;
    }
    public void setUpdateFormatted(String updateFormatted){
        this.updateFormatted = updateFormatted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conditionDesc);
        dest.writeString(currentCondition);
        dest.writeString(currentStatus);
        dest.writeString(facebook);
        dest.writeString(geoLang);
        dest.writeString(geoLat);
        dest.writeString(landOwner);
        dest.writeString(openClose);
        dest.writeString(trailAddress);
        dest.writeString(trailCity);
        dest.writeString(trailDesc);
        dest.writeString(trailId);
        dest.writeString(trailName);
        dest.writeString(twitter);
        dest.writeString(updateFormatted);
    }
}
