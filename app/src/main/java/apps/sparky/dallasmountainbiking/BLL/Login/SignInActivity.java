package apps.sparky.dallasmountainbiking.BLL.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class SignInActivity implements GoogleApiClient.OnConnectionFailedListener {
    final public int loginCode = 9001;
    private AppCompatActivity view;
    private GoogleApiClient mGoogleApiClient;
    public Boolean isConnected;

    public SignInActivity(AppCompatActivity view){
        this.view = view;
        SetupGoogleServices();
        isConnected = false;
    }

    private void SetupGoogleServices() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(view)
                .enableAutoManage(view, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void SignOut(){
        if(mGoogleApiClient.isConnected()){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
        }
    }

    public void SignIn(){
        if(mGoogleApiClient.isConnected()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            view.startActivityForResult(signInIntent, loginCode);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(view.getApplicationContext(), connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }
}
