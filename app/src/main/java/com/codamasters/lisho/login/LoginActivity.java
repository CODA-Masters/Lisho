package com.codamasters.lisho.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codamasters.lisho.R;

public class LoginActivity extends AuthActivity {

    private EditText mEmailField, mPasswordField;
    private Button loginButton;
    private FloatingActionButton fab;
    private CardView cardView;
    private Activity activity;
    private final static String PREF_TAG = "Lisho";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enterFromBottomAnimation();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpToolbarWithTitle("Login", true);


        activity = this;

        if(getUser()){
            doLogin(user, true);
        }

        initView();
        initListeners();
    }

    @Override
    protected void onPause() {
        exitToBottomAnimation();
        super.onPause();
    }


    private void initView(){
        cardView = (CardView) findViewById(R.id.cv);

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        loginButton = (Button) findViewById(R.id.email_sign_in_button);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void initListeners(){

        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, fab, fab.getTransitionName());
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

}
