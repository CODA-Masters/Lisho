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

import com.codamasters.lisho.R;
import com.codamasters.lisho.model.User;
import com.codamasters.lisho.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Juan on 21/09/2016.
 */

public class AuthActivity extends AppCompatActivity {

    private final static String TAG = "AuthActivity";
    private final static String PREF_TAG = "Lisho";

    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference firebaseRef;
    public String user_id;
    public User user;

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
        user = null;
        saveUser(user);
    }

    public void signIn(final String email, final String password) {
        showProgressDialog();

        doLogin(user, false);


        /*
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            hideProgressDialog();
                            Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            user_id = task.getResult().getUser().getUid();

                            firebaseRef = FirebaseDatabase.getInstance().getReference().child("user").child(user_id);


                            firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("Result", dataSnapshot.getValue().toString());

                                    user = dataSnapshot.getValue(User.class);
                                    user_id = dataSnapshot.getKey();
                                    saveUser(user);

                                    hideProgressDialog();
                                    doLogin(user, false);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                });
                */
    }

    public void doLogin(final User user, boolean auto) {

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

    private void saveUser(User user) {
        this.user = user;
        /*Gson gson = new Gson();
        String json = gson.toJson(user);

        SharedPreferences.Editor editor = getSharedPreferences(PREF_TAG, MODE_PRIVATE).edit();
        editor.putString("user_id", user_id);
        editor.putString("user", json);

        editor.commit();
        */
    }

    public boolean getUser() {

        SharedPreferences prefs = getSharedPreferences(PREF_TAG, MODE_PRIVATE);
        user_id = prefs.getString("user_id", null);

        /*
        Gson gson = new Gson();
        String json = prefs.getString("user", null);
        Type type = new TypeToken<User>() {
        }.getType();
        user = gson.fromJson(json, type);
        */

        if (user_id != null && user != null)
            return true;

        return false;
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

    protected void enterFromBottomAnimation(){
        overridePendingTransition(R.transition.activity_open_translate_from_bottom, R.transition.activity_no_animation);
    }

    protected void exitToBottomAnimation(){
        overridePendingTransition(R.transition.activity_no_animation, R.transition.activity_close_translate_to_bottom);
    }


}
