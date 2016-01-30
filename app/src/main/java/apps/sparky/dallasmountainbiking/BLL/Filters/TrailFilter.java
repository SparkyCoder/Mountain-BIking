package apps.sparky.dallasmountainbiking.BLL.Filters;

import android.app.Activity;
import android.widget.Filter;
import java.util.ArrayList;
import apps.sparky.dallasmountainbiking.BLL.Adapters.TrailAdapter;
import apps.sparky.dallasmountainbiking.Objects.Trail;

/**
 * Created by david.kobuszewski on 1/21/2016.
 */
public class TrailFilter extends Filter {
    private Trail[] trails;
    private TrailAdapter adapter;
    private Activity trailActivity;

    public TrailFilter(Trail[] trails, TrailAdapter adapter, Activity trailActivity) {
        this.trails = trails;
        this.adapter = adapter;
        this.trailActivity = trailActivity;
    }

    @Override
    protected void publishResults(CharSequence constraint,
                                  FilterResults results) {

        Trail[] filteredSet = ConvertFilterResultToTrailArray(results);

        UpdateAdapterTrails(filteredSet);

        RefreshAdapter();

    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();


        if (constraint == null || constraint.length() == 0) {
            results.values = trails;
            results.count = trails.length;
        } else {
            ArrayList<Trail> newValues = new ArrayList<Trail>();
            for (int i = 0; i < trails.length; i++) {
                Trail item = trails[i];
                if (item.getTrailName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                    newValues.add(item);
                }
            }
            results.values = newValues.toArray();
            results.count = newValues.size();
        }

        return results;
    }

    private void UpdateAdapterTrails(Trail[] filteredSet) {
        adapter.SetTrails(filteredSet);
    }

    private Trail[] ConvertFilterResultToTrailArray(FilterResults results) {

        Trail[] filteredSet = new Trail[results.count];

        Object[] test = (Object[]) results.values;


        for (int index = 0; index < test.length; index++) {
            Trail currentTrail = (Trail) test[index];
            filteredSet[index] = currentTrail;
        }

        return filteredSet;
    }

    private void RefreshAdapter() {
        trailActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                adapter.notifyDataSetChanged();
            }
        });
    }
}