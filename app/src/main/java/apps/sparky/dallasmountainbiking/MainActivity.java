package apps.sparky.dallasmountainbiking;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.util.Date;
import java.util.List;

import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Login.SignInActivity;
import apps.sparky.dallasmountainbiking.BLL.Repositories.CheckInRepository;
import apps.sparky.dallasmountainbiking.BLL.Repositories.GetCheckInsRepository;
import apps.sparky.dallasmountainbiking.BLL.Services.BackgroundService;
import apps.sparky.dallasmountainbiking.BLL.Services.RepeatingIntent;
import apps.sparky.dallasmountainbiking.BLL.Setup.MenuSetup;
import apps.sparky.dallasmountainbiking.BLL.Setup.ToolbarSetup;
import apps.sparky.dallasmountainbiking.DAL.DAO;
import apps.sparky.dallasmountainbiking.Fragments.TrailsFragment;
import apps.sparky.dallasmountainbiking.Objects.CheckIns;
import apps.sparky.dallasmountainbiking.Objects.DmbUser;
import apps.sparky.dallasmountainbiking.Objects.Favorites;
import apps.sparky.dallasmountainbiking.Objects.SugarTrail;
import apps.sparky.dallasmountainbiking.Objects.Trail;

public class MainActivity extends AppCompatActivity {
    SignInActivity google;
    String userID;
    ToolbarSetup toolbar;
    TrailsFragment trailsFrag;
    MenuSetup menu;
    DAO dao;
    Bundle fragmentBundle;
    Boolean favoritesOn;
    ExceptionHandling exceptionHandling;
    SwitchCompat toggle;
    Boolean screenOrientationJustChanged;
    public Trail[] trails;
    List<Favorites> favorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState != null && savedInstanceState.containsKey("trails"))
            trails = ParseParcelable(savedInstanceState.getParcelableArray("trails"));
        if (savedInstanceState != null && savedInstanceState.containsKey("favorites"))
            favoritesOn = savedInstanceState.getBoolean("favorites");
        if (savedInstanceState != null && savedInstanceState.containsKey("userID"))
            userID = savedInstanceState.getString("userID");

        this.screenOrientationJustChanged = true;

        Initialize();
    }

    private void Initialize() {
        favorites = null;
        dao = new DAO();
        google = new SignInActivity(this);
        toolbar = new ToolbarSetup(this);
        exceptionHandling = new ExceptionHandling(this);
        menu = new MenuSetup(this, google, toolbar.drawerLayout);

        if (favoritesOn == null)
            favoritesOn = false;

        menu.HideCustomTrails();
        SetupMenuClickEvents();
        CheckForAuthenticatedUser();
        StartBackgroundService();
    }

    private List<Favorites> GetFavorites(){
        if(dao != null)
            return dao.GetFavorites();

        return null;
    }

    private void StartBackgroundService() {
        if(!IsBackGroundServiceRunning())
            this.startService(new Intent(this, BackgroundService.class));
    }

    private void CheckForAuthenticatedUser() {
        List<DmbUser> users = dao.GetUser();

        if (users != null && users.size() > 0) {
            RegisterUser(users.get(0));
            InitializeTrailsFragment();
        } else {
            fragmentBundle = null;
            InitializeTrailsFragment();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == google.loginCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();

                if (acct != null && acct.getPhotoUrl() != null && userID == null)
                    RegisterUser(new DmbUser(acct.getId(), acct.getDisplayName(), acct.getPhotoUrl().toString(), acct.getEmail()));
                else if(acct != null && acct.getId() != null && acct.getDisplayName() != null && acct.getEmail() != null)
                    RegisterUser(new DmbUser(acct.getId(), acct.getDisplayName(), null, acct.getEmail()));
                else if (acct != null && acct.getId() != null && acct.getDisplayName() != null)
                    RegisterUser(new DmbUser(acct.getId(), acct.getDisplayName(), null, null));

            } else {
                UnRegisterUser();
                if(result != null && result.getStatus() != null) {
                    String errorMessage = result.getStatus().getStatusMessage();
                    Integer errorCode = result.getStatus().getStatusCode();

                    if(errorMessage != null && errorCode!=null && errorCode != 0)
                        exceptionHandling.ShowToast("Login Failed - Message: "+errorMessage+" Code: " + errorCode);
                    else if (errorMessage != null && (errorCode == null || errorCode == 0))
                        exceptionHandling.ShowToast("Login Failed - Message: "+errorMessage);
                    else if (errorMessage == null && (errorCode != null || errorCode != 0))
                        exceptionHandling.ShowToast("Login Failed - Code: " + errorCode);
                }
                else {
                    exceptionHandling.ShowToast("Login Failed");
                }
            }

            InitializeTrailsFragment();
        }
    }

    private void UnRegisterUser() {
        fragmentBundle = null;
        userID = null;
        favoritesOn = false;
        menu.SetLogoutDefaults();
        google.SignOut();
        dao.DeleteUsers();

        toggle.setChecked(false);

        trailsFrag = null;
    }

    private void RegisterUser(DmbUser user) {
        userID = user.UserIdentification;
        favorites = GetFavorites();

        if(favorites!=null && favorites.size() > 0) {
            favoritesOn = true;
            toggle.setChecked(true);
        }        else {
            favoritesOn = false;
            toggle.setChecked(false);
        }

        menu.UpdateEmail(user.Email);
        menu.UpdateProfilePicture(user.ProfilePictureUrl);
        menu.UpdateName(user.Name);
        menu.HideLogin();
        menu.ShowCustomTrails();

        List<DmbUser> users = dao.GetUser();

        if (users == null || users.size() == 0) {
            dao.AddNewUser(user.UserIdentification, user.Name, user.ProfilePictureUrl, user.Email);
        }
    }

    private Trail[] ParseParcelable(Parcelable[] parcelables){
        try {
            Trail[] trails = new Trail[parcelables.length];
            for (int i = 0; i < parcelables.length; i++) {
                trails[i] = (Trail) parcelables[i];
            }

            return trails;
        }
        catch (Exception ex){
            exceptionHandling.ShowToast("Error Converting Trails");
            return  null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putBoolean("favorites", favoritesOn);

        if (userID != null)
            savedState.putString("userID", userID);

        if (trails != null)
            savedState.putParcelableArray("trails", trails);

        super.onSaveInstanceState(savedState);

    }

    private void InitializeTrailsFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        trailsFrag = (TrailsFragment)getFragmentManager().findFragmentByTag("trail");

        if(trailsFrag == null) {

            trailsFrag = new TrailsFragment();

            transaction.replace(R.id.content, trailsFrag, "trail");
            transaction.addToBackStack("trail");
        }
        else {
            transaction.replace(R.id.content, trailsFrag, "trail");;
        }

        trailsFrag.userID = userID;
        trailsFrag.favoritesOn = favoritesOn;
        trailsFrag.trails = trails;
        trailsFrag.mainActivity = this;

        transaction.commit();

            if(trailsFrag.getView() != null)
            trailsFrag.PopulateTrails(userID, favoritesOn, trails, this);
    }

    private void SetupMenuClickEvents(){
        Menu sideMenu = menu.nav.getMenu();
        MenuItem menuItem = sideMenu.findItem(R.id.customTrails);

        View actionView = MenuItemCompat.getActionView(menuItem);
        toggle = (SwitchCompat)actionView.findViewById(R.id.favoritesSwitch);

        if(userID != null && favorites != null && favorites.size() > 0){
            favoritesOn = true;
            toggle.toggle();
        }

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(toggle.isChecked()) {
                    favoritesOn = true;
                }
                else {
                    favoritesOn = false;
                }

                InitializeTrailsFragment();

                screenOrientationJustChanged = false;
            }
        });

        menu.nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.login:
                        google.SignIn();
                        return true;
                    case R.id.logout:
                        UnRegisterUser();
                        InitializeTrailsFragment();
                        return true;
                    case R.id.customTrails:
                        menu.drawer.closeDrawers();
                        return true;
                    case R.id.trails:
                        menu.drawer.closeDrawers();
                        return true;
                    case R.id.CheckInsMenuItem:
                        Intent intent = new Intent(getApplicationContext(), apps.sparky.dallasmountainbiking.CheckIns.class);
                        startActivity(intent);
                        menu.drawer.closeDrawers();
                        return true;
                    default:
                        menu.drawer.closeDrawers();
                }

                screenOrientationJustChanged = false;

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsPopupActivity.class);
                startActivity(intent);
                return true;
            case R.id.refresh:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                trailsFrag = new TrailsFragment();
                trailsFrag.userID = userID;
                trailsFrag.favoritesOn = favoritesOn;
                trailsFrag.trails = null;
                trailsFrag.mainActivity = this;
                transaction.replace(R.id.content, trailsFrag, "trail");
                transaction.addToBackStack("trail");
                transaction.commit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Boolean IsBackGroundServiceRunning(){
        return  (PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(getApplicationContext(), RepeatingIntent.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }
}
