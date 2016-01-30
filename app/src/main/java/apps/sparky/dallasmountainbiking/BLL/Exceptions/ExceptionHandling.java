package apps.sparky.dallasmountainbiking.BLL.Exceptions;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by david.kobuszewski on 1/21/2016.
 */
public class ExceptionHandling {

    private Activity activity;

    public ExceptionHandling(Activity activity)
    {
        this.activity = activity;
    }

    public void ShowToast(String message)
    {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);;
        toast.show();
    }
}