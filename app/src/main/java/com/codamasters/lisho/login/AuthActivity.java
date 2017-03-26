package com.codamasters.lisho.login;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.widget.Toast;

import com.codamasters.lisho.R;
import com.codamasters.lisho.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Juan on 21/09/2016.
 */

public class AuthActivity extends AppCompatActivity {

    private final static String TAG = "AuthActivity";
    private final static String PREF_TAG = "Lisho";

    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    public String user_id;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();
                if (userFirebase != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + userFirebase.getUid());
                    userFirebase.getToken(true);
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void signOut() {
        mAuth.signOut();
        user_id = null;
        saveUser();
    }

    public void signIn(final String email, final String password) {
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            hideProgressDialog();
                            Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            user_id = task.getResult().getUser().getEmail();
                            doLogin(false);
                        }
                    }
                });
    }

    public void doLogin(boolean auto) {

        Intent intent;

        intent = new Intent(AuthActivity.this, MainActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !auto) {
            loginAnimation(intent);
        } else {
            startActivity(intent);
            finish();
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void loginAnimation(Intent intent) {
        Explode explode = new Explode();
        explode.setDuration(500);

        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        startActivity(intent, oc2.toBundle());
        finish();
    }

    private void saveUser() {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_TAG, MODE_PRIVATE).edit();
        editor.putString("user_id", user_id);

        editor.commit();
    }

    public boolean getUser() {

        SharedPreferences prefs = getSharedPreferences(PREF_TAG, MODE_PRIVATE);
        user_id = prefs.getString("user_id", null);

        return user_id != null;

    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.wait));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    protected void setUpToolbarWithTitle(String title, boolean hasBackButton){
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayShowHomeEnabled(hasBackButton);
            getSupportActionBar().setDisplayHomeAsUpEnabled(hasBackButton);
        }
    }
}
