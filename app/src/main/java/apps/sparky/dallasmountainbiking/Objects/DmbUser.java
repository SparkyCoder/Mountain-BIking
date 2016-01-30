package apps.sparky.dallasmountainbiking.Objects;

import com.orm.SugarRecord;

/**
 * Created by david.kobuszewski on 1/18/2016.
 */
public class DmbUser extends SugarRecord{
    public String UserIdentification;
    public String Name;
    public String ProfilePictureUrl;
    public String Email;

    public DmbUser(){}

    public DmbUser(String ID, String Name, String ProfilePictureUrl, String Email){
        this.UserIdentification = ID;
        this.Name = Name;
        this.ProfilePictureUrl = ProfilePictureUrl;
        this.Email = Email;
    }
}
