package apps.sparky.dallasmountainbiking.BLL.Repositories;

import android.os.AsyncTask;
import android.os.Looper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.InputStream;

import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.Objects.CheckIns;
import apps.sparky.dallasmountainbiking.Objects.DorbaUrls;

/**
 * Created by David.Kobuszewski on 2/9/2016.
 */
public class CheckInRepository extends AsyncTask<Void, Void, Boolean> {

    public CheckIns NewCheckIn;
    public ExceptionHandling exceptionHandling;

    public CheckInRepository(){

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
            HttpResponse response;
            JSONObject json = new JSONObject();

            HttpPost post = new HttpPost(DorbaUrls.CheckInUrl);
            json.put("UserID", NewCheckIn.UserID);
            json.put("CheckInDate", NewCheckIn.CheckInDate);
            json.put("Lattitude", NewCheckIn.Lattitude);
            json.put("Longitude", NewCheckIn.Longitude);
            json.put("Name", NewCheckIn.Name);
            json.put("PictureUrl", NewCheckIn.PictureUrl);
            json.put("TrailName", NewCheckIn.TrailName);
            StringEntity se = new StringEntity(json.toString());
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(se);
            response = client.execute(post);

                    /*Checking response */
            if (response != null) {
                InputStream in = response.getEntity().getContent(); //Get the data in the entity
            }
        } catch (Exception ex) {
return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (isCancelled()) return;

        if(!result)
            exceptionHandling.ShowToast("Check-In Failed. ");
        else
            exceptionHandling.ShowSuccessMessage("You Have Been Checked-In.");

    }
}
