package com.codamasters.lisho.ui;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codamasters.lisho.R;

/**
 * Created by Juan on 21/01/2017.
 */

public class BaseActivity extends AppCompatActivity {

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