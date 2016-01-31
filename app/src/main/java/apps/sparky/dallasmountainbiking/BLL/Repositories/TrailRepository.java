package apps.sparky.dallasmountainbiking.BLL.Repositories;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.io.IOException;

import apps.sparky.dallasmountainbiking.BLL.Adapters.TrailAdapter;
import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Parsers.TrailsParser;
import apps.sparky.dallasmountainbiking.Fragments.TrailsFragment;
import apps.sparky.dallasmountainbiking.MainActivity;
import apps.sparky.dallasmountainbiking.Objects.DorbaUrls;
import apps.sparky.dallasmountainbiking.Objects.Trail;
import apps.sparky.dallasmountainbiking.R;

/**
 * Created by david.kobuszewski on 1/21/2016.
 */
public final class TrailRepository extends AsyncTask<Void, Void, Trail[]>{

    //Should Never Be Accessed Within doInBackground
    public String userID;
    public ListFragment fragment;
    public Context mainActivity;
    public Activity activity;
    public Boolean isFinished;
    public Trail[] result;
    public TrailsParser trailParser;
    public DorbaUrls urls;
    public TrailAdapter adapter;
    public ExceptionHandling error;
    public Boolean favoritesOnly;

    private Boolean hasError;
    private EditText filterText;
    private Boolean noResults;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.error = new ExceptionHandling(activity);
        this.noResults = false;
    }

    @Override
    protected void onPostExecute(Trail[] result) {
        super.onPostExecute(result);
        if (isCancelled()) return;

        this.isFinished = true;
        this.result = result;
        ((MainActivity)mainActivity).trails = result;

        if (CheckForErrors()) UpdateUI();

        closeDialog();
    }

    private Boolean CheckForErrors() {
        if (isCancelled()) return false;

        if (result == null && noResults) {
            error.ShowToast("No Trails Found");
            return false;
        } else if (result == null || hasError) {
            error.ShowToast("Error Retrieving Data");
            return false;
        }

        return true;
    }

    public TrailRepository() {
        if (isCancelled()) return;
        this.hasError = false;
        this.isFinished = false;
    }

    private Trail[] GetTrails() throws IOException {
        if (isCancelled()) return null;

        String rawData = trailParser.GetRawData(DorbaUrls.TrailsUrl);

        if (rawData.contains(":[]")) {
            noResults = true;
            return null;
        }

        return trailParser.FromJson(rawData);
    }

    @Override
    protected Trail[] doInBackground(Void... trailID) {

        if (isCancelled()) return null;
        if (result != null && result.length > 0) return result;

        try {
            return GetTrails();
        } catch (IOException e) {
            hasError = true;
            return null;
        }
    }

    public void UpdateUI() {
        try {
            if (this.activity != null && !isCancelled()) {


                filterText = (EditText) activity.findViewById(R.id.trail_search);
                filterText.addTextChangedListener(filterTextWatcher);

                ListView listView = (ListView) activity.findViewById(R.id.TrailsList);
                adapter = new TrailAdapter(activity, R.layout.custom_trail_listview_template, result);
                adapter.userID = userID;
                adapter.favoritesOnly = favoritesOnly;
                listView.setAdapter(adapter);
            }
        }
        catch (Exception ex){

        }
    }

    private void closeDialog(){
        ((TrailsFragment)fragment).CloseDialog();
    }

    public void OnDestroy() {
        filterText.removeTextChangedListener(filterTextWatcher);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter.getFilter().filter(s);
        }

    };
}
