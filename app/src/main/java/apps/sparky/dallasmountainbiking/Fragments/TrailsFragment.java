package apps.sparky.dallasmountainbiking.Fragments;

import android.app.Dialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Parsers.TrailsParser;
import apps.sparky.dallasmountainbiking.BLL.Repositories.TrailRepository;
import apps.sparky.dallasmountainbiking.Objects.DorbaUrls;
import apps.sparky.dallasmountainbiking.Objects.Trail;
import apps.sparky.dallasmountainbiking.R;

public class TrailsFragment extends ListFragment  {
    private ExceptionHandling exceptionHandler;
    private TrailRepository trailrep;
    private String userID;
    public Trail[] trails;
    private Dialog dialog;
    private Boolean favoritesOn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey("trails"))
            trails = (Trail[])savedInstanceState.getParcelableArray("trails");
        if(savedInstanceState != null && savedInstanceState.containsKey("userID"))
            userID = savedInstanceState.getString("userID");
        if(savedInstanceState != null && savedInstanceState.containsKey("favorites"))
            favoritesOn = savedInstanceState.getBoolean("favorites");

        return inflater.inflate(R.layout.fragment_trails, container, false);
    }

    public void SetupVars(String userID, Boolean favoritesOn){
        this.userID = userID;
        this.favoritesOn = favoritesOn;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putParcelableArray("trails", trails);
        savedState.putString("userID", userID);
        savedState.putBoolean("favorites", favoritesOn);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        try {

            SetupDialog();

            dialog.show();

            Initialize();

            PopulateTrails();

            CloseDialog();

        } catch (Exception e) {
            dialog.dismiss();
            this.exceptionHandler.ShowToast(e.getMessage());
        }
    }

    public void CloseDialog() {
        if(trailrep != null && trailrep.isFinished) dialog.dismiss();
    }

    private void SetupDialog()
    {
        this.dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.dialog_title_trails));
    }

    private void Initialize() {
        this.exceptionHandler = new ExceptionHandling(getActivity());

    }

    @SuppressWarnings("deprecation")
    public void PopulateTrails()  {
        try {
            if (favoritesOn == null)
                favoritesOn = false;

            trailrep = new TrailRepository();
            trailrep.userID = userID;
            trailrep.favoritesOnly = favoritesOn;
            trailrep.fragment = this;
            trailrep.activity = getActivity();
            trailrep.trailParser = new TrailsParser();
            trailrep.urls = new DorbaUrls();
            trailrep.error = new ExceptionHandling(getActivity());

            if (trails != null)
                trailrep.result = trails;

            trailrep.execute();
        } catch (Exception ex) {
            exceptionHandler.ShowToast(ex.getMessage());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroy();


        trailrep.activity = null;

        if(getActivity().isFinishing())
        {
            trailrep.cancel(false);
        }
    }

}
