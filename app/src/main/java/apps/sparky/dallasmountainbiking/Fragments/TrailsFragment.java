package apps.sparky.dallasmountainbiking.Fragments;

import android.app.Dialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
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
    private Dialog dialog;
    public Trail[] trails;
    public String userID;
    public Boolean favoritesOn;
    public Context mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trails, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        try {

            SetupDialog();

            dialog.show();

            Initialize();

            PopulateTrails(userID, favoritesOn, trails, mainActivity);

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
    public void PopulateTrails(String userID, Boolean favoritesOn, Trail[] trails, Context mainActivity)  {
        try {
            if (favoritesOn == null)
                favoritesOn = false;

            trailrep = new TrailRepository();
            trailrep.userID = userID;
            trailrep.favoritesOnly = favoritesOn;
            trailrep.mainActivity = mainActivity;
            trailrep.fragment = this;
            trailrep.activity = this.getActivity();
            trailrep.trailParser = new TrailsParser();
            trailrep.urls = new DorbaUrls();
            trailrep.error = new ExceptionHandling(getActivity());
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
