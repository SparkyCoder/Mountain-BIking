package apps.sparky.dallasmountainbiking.BLL.Repositories;

import android.app.Activity;
import android.os.AsyncTask;

import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Parsers.CheckInsParser;
import apps.sparky.dallasmountainbiking.Objects.CheckIns;
import apps.sparky.dallasmountainbiking.Objects.DorbaUrls;

/**
 * Created by David.Kobuszewski on 2/9/2016.
 */
public class GetCheckInsRepository extends AsyncTask<Void, Void, CheckIns[]> {
    public ExceptionHandling exceptionHandling;
    public Activity mapActivity;

    @Override
    protected CheckIns[] doInBackground(Void... params) {
        try {
            CheckInsParser parser = new CheckInsParser();
            return parser.FromJson(parser.GetRawData(DorbaUrls.GetCheckInsUrl));
        }catch (Exception ex){
            return null;
        }
    }

    @Override
    protected void onPostExecute(CheckIns[] result) {
        super.onPostExecute(result);

        if(result == null)
            exceptionHandling.ShowToast("Unable To Retrieve Check-In");
        else {
            ((apps.sparky.dallasmountainbiking.CheckIns)mapActivity).CheckInResults = result;
            ((apps.sparky.dallasmountainbiking.CheckIns)mapActivity).SetupHeatMap();
        }
    }
}
