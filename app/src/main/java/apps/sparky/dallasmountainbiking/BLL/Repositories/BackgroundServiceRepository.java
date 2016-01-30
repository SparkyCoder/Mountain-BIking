package apps.sparky.dallasmountainbiking.BLL.Repositories;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.List;

import apps.sparky.dallasmountainbiking.BLL.Parsers.TrailsParser;
import apps.sparky.dallasmountainbiking.DAL.DAO;
import apps.sparky.dallasmountainbiking.Objects.DorbaUrls;
import apps.sparky.dallasmountainbiking.Objects.Favorites;
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

        List<SugarTrail> savedTrails = dao.GetTrails();

       if(savedTrails == null || dao.GetTrails().size() == 0)
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
                if(!savedTrail.currentStatus.equals(trail.getCurrentStatus()))
                    SendUpdate(trail.getTrailName(), trail.getCurrentStatus());
            }
        }
    }

    private void SendUpdate(String trailName, String currentStatus){
        String status;

        if(currentStatus.equals("1"))
            status = "Open";
        else
            status = "Closed";


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle("Trail Update")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Service Started"))
                        .setContentText(trailName+" is "+status)
                        .setSmallIcon(R.drawable.ic_launcher);

        final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(001, mBuilder.build());
    }
}
