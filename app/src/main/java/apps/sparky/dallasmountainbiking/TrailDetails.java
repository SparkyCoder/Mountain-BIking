package apps.sparky.dallasmountainbiking;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class TrailDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trail_details);

        TextView txtDetails = (TextView)findViewById(R.id.txtDetails);

        txtDetails.setText(Html.fromHtml(getIntent().getExtras().getString("details")));
        txtDetails.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
