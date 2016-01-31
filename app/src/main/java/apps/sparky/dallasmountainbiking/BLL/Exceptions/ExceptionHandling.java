package apps.sparky.dallasmountainbiking.BLL.Exceptions;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import apps.sparky.dallasmountainbiking.R;

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
        View view = toast.getView();
        view.setBackgroundResource(R.color.red);

        toast.show();
    }
}