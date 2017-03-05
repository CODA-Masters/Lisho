package com.codamasters.lisho.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;

public class SplashActivity extends AppCompatActivity {

    private final static String PREF_TAG = "Lisho";
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(loadUser())
            if(isInternetAvailable()){
                checkConnectionWithFirebase();
            }else{
                Toast.makeText(getApplicationContext(), "ERROR CONEXIÓN, NO INTERNET", Toast.LENGTH_LONG).show();
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


    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }

    private void checkConnectionWithFirebase() {
        FirebaseDatabase.getInstance().getReference().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    Toast.makeText(getApplicationContext(), "CONECTADO", Toast.LENGTH_LONG).show();
                };
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "ERROR CONEXIÓN", Toast.LENGTH_LONG).show();
            }
        });
    }



}
