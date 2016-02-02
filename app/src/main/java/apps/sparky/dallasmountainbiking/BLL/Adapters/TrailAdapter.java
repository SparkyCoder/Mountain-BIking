package apps.sparky.dallasmountainbiking.BLL.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Filters.TrailFilter;
import apps.sparky.dallasmountainbiking.DAL.DAO;
import apps.sparky.dallasmountainbiking.Objects.Favorites;
import apps.sparky.dallasmountainbiking.Objects.OnClickParameters;
import apps.sparky.dallasmountainbiking.Objects.Trail;
import apps.sparky.dallasmountainbiking.Objects.TrailHolder;
import apps.sparky.dallasmountainbiking.R;
import apps.sparky.dallasmountainbiking.TrailDetails;

/**
 * Created by david.kobuszewski on 1/18/2016.
 */
public class TrailAdapter extends ArrayAdapter<Trail> implements Filterable {

    public String userID;
    List<Favorites> favorites;
    private Context context;
    private int layoutResourceId;
    private Trail[] trails;
    private Trail trail;
    private Filter mFilter;
    ExceptionHandling exceptionHandling;
    private Location networkLocation;
    private Boolean isProviderEnabled;
    private Boolean hasDisplayedAlert;
    private DAO dao;
    private Map<Integer, Integer> imgs;
    public Boolean favoritesOnly;

    public TrailAdapter(Context context, int layoutResourceId, Trail[] trails) {
        super(context, layoutResourceId);
        this.context = context;
        this.exceptionHandling = new ExceptionHandling((Activity) context);
        this.layoutResourceId = layoutResourceId;
        this.trails = trails;
        dao = new DAO();
        imgs = new HashMap<>();

        SetFavorites();
        SetupLocation(context);
    }

    public void SetTrails(Trail[] newtrails) {
        this.trails = newtrails;
    }

    private void SetFavorites(){
        favorites = dao.GetFavorites();
    }

    @Override
    public int getCount() {
        return trails.length;
    }

    @Override
    public Trail getItem(int position) {
        return trails[position];
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new TrailFilter(trails, this, (Activity) context);
        }
        return mFilter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        trail = trails[position];

        if(favoritesOnly && !isFavorite(trail.getTrailId())){
            return new View(context);
        }

        View row = convertView;
        TrailHolder holder = new TrailHolder();

        if (row == null || row.getTag() == null) {

            LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder.txtTrailDescription = (TextView) row.findViewById(R.id.txtTrailDescription);
            holder.txtTitleTrail = (TextView) row.findViewById(R.id.txtTitleTrail);
            holder.txtLastUpdated = (TextView) row.findViewById(R.id.txtLastUpdated);
            holder.imgGps = (ImageView) row.findViewById(R.id.gps);
            holder.imgFavorite = (ImageView) row.findViewById(R.id.Favorite);

            row.setTag(holder);
        } else {
            holder = (TrailHolder) row.getTag();
        }

        holder.imgGps.setTag(position);

        if (trail.getCurrentStatus().equals("0")) {
            holder.txtTrailDescription.setTextColor(Color.parseColor("#FF0000"));
            holder.txtTrailDescription.setText(String.format("Trails is Closed (%s)", trail.getConditionDesc()));
        } else {
            holder.txtTrailDescription.setTextColor(Color.parseColor("#009933"));
            holder.txtTrailDescription.setText(String.format("Trails is Open (%s)", trail.getConditionDesc()));
        }

        holder.txtTitleTrail.setText(String.format("%s %n(%s, TX)", trail.getTrailName(), trail.getTrailCity()));
        holder.txtLastUpdated.setText(String.format("Updated: %s", trail.getUpdateFormatted()));

        if(imgs.containsKey(position)){
                holder.imgFavorite.setImageResource(imgs.get(position));

                holder.imgFavorite.setTag(new OnClickParameters(imgs.get(position), trails[position].getTrailId()));
        }
        else {
            if (isFavorite(trail.getTrailId())) {
                holder.imgFavorite.setImageResource(R.drawable.fav);
                imgs.remove(position);
                imgs.put(position, R.drawable.fav);
                holder.imgFavorite.setTag(new OnClickParameters(R.drawable.fav, trails[position].getTrailId()));
            } else {
                holder.imgFavorite.setImageResource(R.drawable.nonfav);
                imgs.remove(position);
                imgs.put(position, R.drawable.nonfav);
                holder.imgFavorite.setTag(new OnClickParameters(R.drawable.nonfav, trails[position].getTrailId()));
            }
        }

        if(userID == null)
            holder.imgFavorite.setVisibility(View.GONE);
        else
            holder.imgFavorite.setVisibility(View.VISIBLE);


        row.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, TrailDetails.class);
                intent.putExtra("details", trails[position].getTrailDesc());
                context.startActivity(intent);
            }
        });

        holder.imgGps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer test = (Integer) v.getTag();
                GetLocation(trails[test].getGeoLat(), trails[test].getGeoLang());
            }
        });

        holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ImageView view = (ImageView)v;
                OnClickParameters parameters = (OnClickParameters)view.getTag();


                if(parameters.drawable == R.drawable.nonfav) {
                    Favorites newFavorite = new Favorites(trails[position].getTrailId(), userID);
                    newFavorite.save();
                    view.setImageResource(R.drawable.fav);

                    imgs.remove(position);
                    imgs.put(position, R.drawable.fav);

                    view.setTag(new OnClickParameters(R.drawable.fav, trails[position].getTrailId()));
                }
                else if(parameters.drawable == R.drawable.fav){
                    dao.DeleteFavoriteByTrailIDAndUserID(trails[position].getTrailId(), userID);

                    imgs.remove(position);
                    imgs.put(position, R.drawable.nonfav);

                    view.setImageResource(R.drawable.nonfav);
                    view.setTag(new OnClickParameters(R.drawable.nonfav, trails[position].getTrailId()));
                }
                else {
                    exceptionHandling.ShowToast("Error. Could Not Favorite/Un-Favorite. Please Contact SparkyApps For Help.");
                }
            }
        });

        return row;
    }

    private Boolean isFavorite(String trailID){
if(favorites == null || userID == null)
    return false;

        for(Favorites favorite : favorites){
            if(trailID.equals(favorite.TrailID) && userID.equals(favorite.UserID)){
                return true;
            }
        }
        return false;
    }

    private void GetLocation(String trailLat, String trailLong) {

        if (isProviderEnabled == null) {
            exceptionHandling.ShowToast("Error! Provider Not Initiated. Please Contact SparkyApps For Help.");
        } else if (!isProviderEnabled) {
            exceptionHandling.ShowToast("Please Enable GPS");
        } else if (isProviderEnabled && networkLocation != null) {
            String address = String.format("http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s",networkLocation.getLatitude(), networkLocation.getLongitude(), trailLat, trailLong);

            CheckForMapsPackageAndExecute(address);

        } else if (isProviderEnabled && networkLocation == null) {
            exceptionHandling.ShowToast("Waiting On Location...");
        } else {
            exceptionHandling.ShowToast("Error! Provider Not Initiated. Please Contact SparkyApps For Help.");
        }
    }

    private void CheckForMapsPackageAndExecute(String address){
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));

        if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(mapIntent);
        }
        else {
            exceptionHandling.ShowToast("No Maps App Is Not Installed. Please Install And Try Again.");
        }
    }


    private void SetupLocation(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        hasDisplayedAlert = false;

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
               networkLocation = location;

                if(!hasDisplayedAlert && networkLocation != null) {
                    hasDisplayedAlert = true;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                isProviderEnabled = true;
            }

            public void onProviderDisabled(String provider) {
                isProviderEnabled = false;
                hasDisplayedAlert = false;
            }
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            exceptionHandling.ShowToast("Permission Denied For Location");
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        isProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}

