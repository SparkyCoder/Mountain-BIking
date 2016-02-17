package apps.sparky.dallasmountainbiking;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Repositories.CheckInRepository;
import apps.sparky.dallasmountainbiking.Objects.CheckIns;
import apps.sparky.dallasmountainbiking.Objects.Trail;


public class TrailDetails extends Activity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapFragment mapFragment;
    private ExceptionHandling exceptionHandling;
    public Trail trail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.exceptionHandling = new ExceptionHandling(this);
        setContentView(R.layout.activity_trail_details);

        TextView txtDetails = (TextView)findViewById(R.id.txtDetails);

        txtDetails.setText(Html.fromHtml(getIntent().getExtras().getString("details")));
        txtDetails.setMovementMethod(LinkMovementMethod.getInstance());

        SetupMaps();
        SetupCheckIn();
    }

    private void SetupCheckIn(){
        ImageButton trailCheckIn =(ImageButton)findViewById(R.id.imgCheckIn);

        trailCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInRepository repo = new CheckInRepository();
                repo.exceptionHandling = exceptionHandling;
                CheckIns newCheckIn = new CheckIns();
                newCheckIn.TrailName= getIntent().getExtras().getString("name");;
                newCheckIn.UserID=getIntent().getExtras().getString("userId");
                newCheckIn.Lattitude=getIntent().getExtras().getString("lat");
                newCheckIn.Longitude=getIntent().getExtras().getString("long");
                newCheckIn.Name="Anonymous";
                newCheckIn.PictureUrl="N/A";
                repo.NewCheckIn = newCheckIn;
                repo.execute();
            }
        });
    }

    private void SetupMaps(){
        mapFragment = (MapFragment) getFragmentManager ()
                .findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        try {
            String lattitude = getIntent().getExtras().getString("lat");
            String name = getIntent().getExtras().getString("name");
            String longitude = getIntent().getExtras().getString("long");



            if(name == null || name.trim().equals(""))
                name = "Trail";

            if (lattitude == null || longitude == null || lattitude.trim().equals("") || longitude.trim().equals("")) {
                mapFragment.getView().setVisibility(View.GONE);
            } else {

                LatLng location = new LatLng(Double.parseDouble(lattitude), Double.parseDouble(longitude));

                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(name));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
            }
        }
        catch (Throwable ex){
            mapFragment.getView().setVisibility(View.GONE);
        }
    }
}
