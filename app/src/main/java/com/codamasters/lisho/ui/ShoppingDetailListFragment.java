package com.codamasters.lisho.ui;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codamasters.lisho.R;
import com.codamasters.lisho.login.LoginActivity;
import com.codamasters.lisho.model.ShoppingItem;
import com.codamasters.lisho.model.ShoppingList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.beamazingtoday.interfaces.AnimationType;
import com.yalantis.beamazingtoday.interfaces.BatModel;
import com.yalantis.beamazingtoday.listeners.BatListener;
import com.yalantis.beamazingtoday.listeners.OnItemClickListener;
import com.yalantis.beamazingtoday.listeners.OnOutsideClickedListener;
import com.yalantis.beamazingtoday.ui.adapter.BatAdapter;
import com.yalantis.beamazingtoday.ui.animator.BatItemAnimator;
import com.yalantis.beamazingtoday.ui.callback.BatCallback;
import com.yalantis.beamazingtoday.ui.widget.BatRecyclerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShoppingDetailListFragment extends Fragment implements BatListener, OnItemClickListener, OnOutsideClickedListener {

    private static final int REQUEST_CODE = 1;
    private final static String PREF_TAG = "Lisho";

    private BatRecyclerView mRecyclerView;
    private BatAdapter mAdapter;
    private List<BatModel> mGoals;
    private List<String> mKeys;

    private BatItemAnimator mAnimator;

    private FloatingActionButton fab;

    // Firebase
    private DatabaseReference databaseReference;

    private int typeList;
    private String idList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_shopping_detail_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            typeList = bundle.getInt("type", ShoppingList.OWN_LIST);
            idList = bundle.getString("id", "");
        }

        initFabButton();

        if(typeList == ShoppingList.GROUP_LIST) {
            mGoals = new ArrayList<>();
            mKeys = new ArrayList<>();
            initBat();
            initFirebase();
        }else {
            initWithPrefs();
            initBat();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.menu_detail_list, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.addUser){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.transition.animation_in_1,R.transition.animation_in_2).toBundle();
            startActivity(intent, bndlanimation);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initWithPrefs(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREF_TAG, getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(idList, "");
        Type type = new TypeToken<ArrayList<ShoppingItem>>(){}.getType();
        mGoals = gson.fromJson(json, type);
        if(mGoals==null){
            mGoals = new ArrayList<>();
        }
    }

    private void savePrefs(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREF_TAG, getActivity().MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mGoals);
        prefsEditor.putString(idList, json);
        prefsEditor.commit();

        Log.d("LOL", "SAVING ITEMS");

    }

    private String uploadListToFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("detailLists").push();
        databaseReference.setValue(mGoals);

        return databaseReference.getKey();
    }


    private void initFirebase(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("detailLists").child(idList);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ShoppingItem model = dataSnapshot.getValue(ShoppingItem.class);
                String key = dataSnapshot.getKey();

                mGoals.add(0, model);
                mKeys.add(0, key);

                mAdapter.notify(AnimationType.ADD, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){

                String key = dataSnapshot.getKey();
                ShoppingItem newModel = dataSnapshot.getValue(ShoppingItem.class);
                int index = mKeys.indexOf(key);

                mGoals.set(index, newModel);

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = mKeys.indexOf(key);

                mKeys.remove(index);
                mGoals.remove(index);

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void initFabButton(){
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);


        fab.setVisibility(View.INVISIBLE);

        fab.setImageResource(R.drawable.add_user);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getActivity(), R.transition.animation_in_1,R.transition.animation_in_2).toBundle();
                startActivity(intent, bndlanimation);

            }
        });
    }

    private void initBat(){
        //((TextView) findViewById(R.id.text_title)).setTypeface(TypefaceUtil.getAvenirTypeface(this));

        mRecyclerView = (BatRecyclerView) getActivity().findViewById(R.id.bat_recycler_view);
        mAnimator = new BatItemAnimator();

        mRecyclerView.getView().setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.d("LOL", "Creating arraylist");

        mRecyclerView.getView().setAdapter(mAdapter = new BatAdapter(mGoals, this, mAnimator).setOnItemClickListener(this).setOnOutsideClickListener(this));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new BatCallback(this));
        itemTouchHelper.attachToRecyclerView(mRecyclerView.getView());
        mRecyclerView.getView().setItemAnimator(mAnimator);
        mRecyclerView.setAddItemListener(this);

        getActivity().findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.revertAnimation();
            }
        });
    }

    @Override
    public void add(String string) {
        ShoppingItem shoppingList = new ShoppingItem(string);

        if(typeList == ShoppingList.GROUP_LIST) {
            Log.d("LOL", "ADDING ITEM ONLINE");

            databaseReference.push().setValue(shoppingList);
        }else{

            Log.d("LOL", "ADDING ITEM");

            mGoals.add(0, shoppingList);
            mAdapter.notify(AnimationType.ADD, 0);
            savePrefs();
        }
    }

    @Override
    public void delete(int position) {
        if(typeList == ShoppingList.GROUP_LIST)
            databaseReference.child(mKeys.get(position)).removeValue();
        else {

            Log.d("LOL", "DELETING ITEM");

            mGoals.remove(position);
            mAdapter.notify(AnimationType.REMOVE, position);
            savePrefs();
        }
    }

    @Override
    public void move(int from, int to) {
        BatModel model = mGoals.get(from);
        model.setChecked(!model.isChecked());

        if(typeList == ShoppingList.GROUP_LIST) {
            databaseReference.child(mKeys.get(from)).setValue(model);
        }else{
            Log.d("LOL", "MOVING ITEM");

            mGoals.set(from, model);
            mAdapter.notifyDataSetChanged();
            savePrefs();
        }
    }

    @Override
    public void onClick(BatModel item, int position) {
        Toast.makeText(getActivity(), item.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOutsideClicked() {
        Log.d("LOL", "OUTSIDE CLICKED");

        mRecyclerView.revertAnimation();
    }
    
}
