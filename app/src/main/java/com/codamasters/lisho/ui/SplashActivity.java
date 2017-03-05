package com.codamasters.lisho.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private final static String PREF_TAG = "Lisho";
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(loadUser())
            if(hasActiveInternetConnection()){
                checkConnectionWithFirebase();
            }else{
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        else{
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    private boolean loadUser(){
        SharedPreferences sharedPreferences = getSharedPreferences( PREF_TAG, MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "");

        userId="test";

        if(userId.equals(""))
            return false;

        return true;
    }


    private boolean hasActiveInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void checkConnectionWithFirebase() {
        FirebaseDatabase.getInstance().getReference().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
        });
    }



}
