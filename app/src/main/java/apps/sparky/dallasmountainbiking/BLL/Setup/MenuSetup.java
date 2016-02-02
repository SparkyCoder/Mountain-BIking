package apps.sparky.dallasmountainbiking.BLL.Setup;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import apps.sparky.dallasmountainbiking.BLL.Login.SignInActivity;
import apps.sparky.dallasmountainbiking.R;

/**
 * Created by david.kobuszewski on 1/18/2016.
 */
public class MenuSetup {
    private AppCompatActivity activity;
    private View header;
    private SignInActivity google;
    public DrawerLayout drawer;
    public NavigationView nav;

    public MenuSetup(AppCompatActivity activity, SignInActivity google, DrawerLayout drawer) {
        this.activity = activity;
        this.google = google;
        this.drawer = drawer;
        Initialize();
    }

    public void Initialize() {
        InstantiateHeader();
        SetDefaultImage();
    }

    private void SetDefaultImage() {
        ImageView profilePicture = (ImageView) header.findViewById(R.id.profile_image);
        Picasso.with(header.getContext()).load(R.drawable.unknown).transform(new CircleTransform()).into(profilePicture);
    }

    private void InstantiateHeader() {
        NavigationView navigationView = (NavigationView) activity.findViewById(R.id.navigation_view);
        header = navigationView.inflateHeaderView(R.layout.drawer_header);
        nav = (NavigationView) activity.findViewById(R.id.navigation_view);
    }

    public void UpdateName(String name) {
        if(name != null)
        ((TextView) header.findViewById(R.id.username)).setText(name);
    }

    public void UpdateEmail(String email) {
        if(email != null)
        ((TextView) header.findViewById(R.id.email)).setText(email);
    }

    public void UpdateProfilePicture(String imageUrl) {
        if(imageUrl != null) {
            ImageView profilePicture = (ImageView) header.findViewById(R.id.profile_image);
            Picasso.with(header.getContext()).load(imageUrl).transform(new CircleTransform()).into(profilePicture);
        }
    }

    public void LoadDefaultProfilePicture() {
        ImageView profilePicture = (ImageView) header.findViewById(R.id.profile_image);
        Picasso.with(header.getContext()).load(R.drawable.unknown).transform(new CircleTransform()).into(profilePicture);
    }

    public void HideLogin(){
        Menu menu = nav.getMenu();

        MenuItem login = menu.findItem(R.id.login);
        login.setVisible(false);

        MenuItem logout = menu.findItem(R.id.logout);
        logout.setVisible(true);
    }

    public void HideCustomTrails(){
        Menu menu = nav.getMenu();

        MenuItem login = menu.findItem(R.id.customTrails);
        login.setVisible(false);

        MenuItem logout = menu.findItem(R.id.trails);
        logout.setVisible(true);
    }

    public void ShowCustomTrails(){
        Menu menu = nav.getMenu();

        MenuItem login = menu.findItem(R.id.trails);
        login.setVisible(false);

        MenuItem logout = menu.findItem(R.id.customTrails);
        logout.setVisible(true);
    }

    public void SetLogoutDefaults(){
        UpdateName(activity.getResources().getString(R.string.guest));
        LoadDefaultProfilePicture();
        UpdateEmail(activity.getResources().getString(R.string.empty));
        ShowLogin();
        HideCustomTrails();
    }

    public void ShowLogin(){
        Menu menu = nav.getMenu();

        MenuItem login = menu.findItem(R.id.login);
        login.setVisible(true);

        MenuItem logout = menu.findItem(R.id.logout);
        logout.setVisible(false);
    }
}
