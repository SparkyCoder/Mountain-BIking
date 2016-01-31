package apps.sparky.dallasmountainbiking.BLL.Repositories;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import java.util.List;
import java.util.Random;

import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Parsers.TrailsParser;
import apps.sparky.dallasmountainbiking.DAL.DAO;
import apps.sparky.dallasmountainbiking.Objects.DorbaUrls;
import apps.sparky.dallasmountainbiking.Objects.SugarTrail;
import apps.sparky.dallasmountainbiking.Objects.Trail;
import apps.sparky.dallasmountainbiking.R;

/**
 * Created by david.kobuszewski on 1/28/2016.
 */
public class BackgroundServiceRepository extends AsyncTask<Void, Void, Trail[]> {
    private TrailsParser trailsParser;
    private Context context;
    private DAO dao;

    public BackgroundServiceRepository(Context context){
        this.context = context;
        this.dao = new DAO();
        trailsParser = new TrailsParser();
    }

    @Override
    protected Trail[] doInBackground(Void... params) {
        try {
            String rawData = trailsParser.GetRawData(DorbaUrls.TrailsUrl);

            if (rawData.contains(":[]")) {
                return null;
            }

            return trailsParser.FromJson(rawData);
        }catch (Exception ex){
          return null;
        }
    }

    @Override
    protected void onPostExecute(Trail[] result) {

        if(result == null)
            return;

    List<SugarTrail> savedTrails = dao.GetTrails();

    if (savedTrails == null || dao.GetTrails().size() == 0)
        SaveTrails(result);
    else
        CheckForUpdateTrails(result);
    }

    private void SaveTrails(Trail[] result){
            for (Trail trail : result) {
                SugarTrail newTrail = new SugarTrail(trail.getTrailId(), trail.getCurrentStatus());
                newTrail.save();
            }
    }

    private void CheckForUpdateTrails(Trail[] result){
        for(Trail trail : result) {
            SugarTrail savedTrail = dao.GetTrailsByID(trail.getTrailId());

            if(savedTrail != null)
            {
                Random random = new Random();
                if(!savedTrail.currentStatus.equals(trail.getCurrentStatus())) {
                    SendUpdate(trail.getTrailName(), trail.getCurrentStatus(), (trail.getTrailId() == null)? Integer.parseInt(trail.getTrailId()) : random.nextInt());
                    UpdateTrail(trail, savedTrail);
                }
            }
        }
    }

    private void UpdateTrail(Trail trail, SugarTrail savedTrail){
        savedTrail.currentStatus = trail.getCurrentStatus();
        savedTrail.save();
    }

    private void SendUpdate(String trailName, String currentStatus, int trailID){
        String status;

        if(currentStatus.equals("1"))
            status = "Open";
        else
            status = "Closed";


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("Trail Update")
                        .setContentText(trailName+" is "+status)
                        .setSmallIcon(R.drawable.notification);

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(trailID, mBuilder.build());
    }
}
