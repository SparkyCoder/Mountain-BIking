package apps.sparky.dallasmountainbiking;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;

import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Repositories.GetCheckInsRepository;
import apps.sparky.dallasmountainbiking.Objects.Favorites;

public class CheckIns extends Activity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapFragment mapFragment;
    private ExceptionHandling exceptionHandling;
    public apps.sparky.dallasmountainbiking.Objects.CheckIns[] CheckInResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_ins);
        exceptionHandling = new ExceptionHandling(this);

        SetupMaps();
    }

    private void GetCheckIns(){
         GetCheckInsRepository repo = new GetCheckInsRepository();
         repo.exceptionHandling = exceptionHandling;
        repo.mapActivity = this;
         repo.execute();
    }

    private void SetupMaps(){
        mapFragment = (MapFragment) getFragmentManager ()
                .findFragmentById(R.id.mapsFragment);
        mapFragment.getMapAsync(this);
    }

    private ArrayList<LatLng> MakeListFromResults() {

        ArrayList<LatLng> locList = new ArrayList<>();

        for (apps.sparky.dallasmountainbiking.Objects.CheckIns checkIns : CheckInResults) {
            locList.add(new LatLng(Double.parseDouble(checkIns.Lattitude), Double.parseDouble(checkIns.Longitude)));
        }

        return locList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        GetCheckIns();

        try {
            String lattitude = "32.7830600";
            String longitude = "-96.8066700";

            LatLng location = new LatLng(Double.parseDouble(lattitude), Double.parseDouble(longitude));

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 8));

        }
        catch (Throwable ex){
           exceptionHandling.ShowToast("Error Initializing Map.");
        }
    }

    public void SetupHeatMap(){
        try {
            ArrayList<LatLng> points = MakeListFromResults();

            if (points == null || !(points.size() > 0))
                return;
            else {

                TextView txtCount = (TextView)findViewById(R.id.txtCheckInNumberLabel);
                txtCount.setText(points.size()+" Check-Ins This Week");

                HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                        .data(points)
                        .gradient(GetGradient())
                        .build();

                mProvider.setOpacity(1);
                mProvider.setRadius(50);

                mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }
        }
        catch (Exception ex){
            exceptionHandling.ShowToast("Error Setting Up Check In Points.");
        }
    }

    private Gradient GetGradient(){
        // Create the gradient.
        int[] colors = {
                Color.rgb(255, 69, 0),
                Color.rgb(255, 0, 0)
        };

        float[] startPoints = {
                0.1f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        return gradient;
    }
}
