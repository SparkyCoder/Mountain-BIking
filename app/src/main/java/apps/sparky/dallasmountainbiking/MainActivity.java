package apps.sparky.dallasmountainbiking;

import android.app.ActivityManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import java.util.List;
import apps.sparky.dallasmountainbiking.BLL.Exceptions.ExceptionHandling;
import apps.sparky.dallasmountainbiking.BLL.Login.SignInActivity;
import apps.sparky.dallasmountainbiking.BLL.Repositories.BackgroundServiceRepository;
import apps.sparky.dallasmountainbiking.BLL.Services.BackgroundService;
import apps.sparky.dallasmountainbiking.BLL.Services.RepeatingIntent;
import apps.sparky.dallasmountainbiking.BLL.Setup.MenuSetup;
import apps.sparky.dallasmountainbiking.BLL.Setup.ToolbarSetup;
import apps.sparky.dallasmountainbiking.DAL.DAO;
import apps.sparky.dallasmountainbiking.Fragments.TrailsFragment;
import apps.sparky.dallasmountainbiking.Objects.DmbUser;

public class MainActivity extends AppCompatActivity  {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null && savedInstanceState.containsKey("favorites"))
            favoritesOn = savedInstanceState.getBoolean("favorites");
        if(savedInstanceState != null && savedInstanceState.containsKey("userID"))
            userID = savedInstanceState.getString("userID");

        this.screenOrientationJustChanged = true;

        Initialize();
    }

    private void Initialize() {
        dao = new DAO();
        google = new SignInActivity(this);
        toolbar = new ToolbarSetup(this);
        exceptionHandling = new ExceptionHandling(this);
        menu = new MenuSetup(this, google, toolbar.drawerLayout);

        if(favoritesOn == null)
        favoritesOn = false;

        menu.HideCustomTrails();
        SetupMenuClickEvents();
        CheckForAuthenticatedUser();
        StartBackgroundService();
    }

    private void StartBackgroundService() {
       // BackgroundServiceRepository serviceRepository = new BackgroundServiceRepository(this);
       // serviceRepository.execute();
        //this.startService(new Intent(this, BackgroundService.class));
    }

    private void CheckForAuthenticatedUser(){
        List<DmbUser> users = dao.GetUser();

        if(users != null && users.size() > 0){
            RegisterUser(users.get(0));
        }
        else {
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

                if(acct!=null && acct.getPhotoUrl() != null)
                RegisterUser(new DmbUser(acct.getId(), acct.getDisplayName(), acct.getPhotoUrl().toString(), acct.getEmail()));
            }
            else
                UnRegisterUser();
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
        InitializeTrailsFragment();
        trailsFrag.PopulateTrails();
    }

    private void RegisterUser(DmbUser user) {
        this.userID = user.UserIdentification;

        favoritesOn = true;
        toggle.setChecked(true);

        menu.UpdateEmail(user.Email);
        menu.UpdateProfilePicture(user.ProfilePictureUrl);
        menu.UpdateName(user.Name);
        menu.HideLogin();
        menu.ShowCustomTrails();

            List<DmbUser> users = dao.GetUser();

            if(users == null || users.size() == 0) {
                dao.AddNewUser(user.UserIdentification, user.Name, user.ProfilePictureUrl, user.Email);
            }

        InitializeTrailsFragment();

        if(!screenOrientationJustChanged)
        trailsFrag.PopulateTrails();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        savedState.putBoolean("favorites", favoritesOn);

        if(userID !=null)
        savedState.putString("userID", userID);

        super.onSaveInstanceState(savedState);
    }

    private void InitializeTrailsFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        trailsFrag = (TrailsFragment)getFragmentManager().findFragmentByTag("trail");

        if(trailsFrag == null) {

            trailsFrag = new TrailsFragment();

            trailsFrag.SetupVars(userID, favoritesOn);

            transaction.replace(R.id.content, trailsFrag, "trail");
            transaction.addToBackStack("trail");

        }
        else {
            trailsFrag.SetupVars(userID, favoritesOn);

            transaction.replace(R.id.content, trailsFrag, "trail");;
        }

        transaction.commit();
    }

    private void SetupMenuClickEvents(){
        Menu sideMenu = menu.nav.getMenu();
        MenuItem menuItem = sideMenu.findItem(R.id.customTrails);

        View actionView = MenuItemCompat.getActionView(menuItem);
        toggle = (SwitchCompat)actionView.findViewById(R.id.favoritesSwitch);

        if(userID != null){
            favoritesOn = true;
            toggle.toggle();
        }

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle.isChecked()) {
                    favoritesOn = true;
                }
                else {
                    favoritesOn = false;
                }

                trailsFrag = (TrailsFragment)getFragmentManager().findFragmentByTag("trail");

                if(trailsFrag!= null) {
                    trailsFrag.SetupVars(userID, favoritesOn);
                    trailsFrag.PopulateTrails();
                }

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
                        return true;
                    case R.id.customTrails:
                        menu.drawer.closeDrawers();
                        return true;
                    case R.id.trails:
                    default:
                        menu.drawer.closeDrawers();
                }

                screenOrientationJustChanged = false;

                return true;
            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }
}
