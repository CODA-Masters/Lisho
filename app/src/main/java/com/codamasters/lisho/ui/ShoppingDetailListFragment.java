package com.codamasters.lisho.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codamasters.lisho.R;
import com.codamasters.lisho.model.ShoppingItem;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yalantis.beamazingtoday.interfaces.AnimationType;
import com.yalantis.beamazingtoday.interfaces.BatModel;
import com.yalantis.beamazingtoday.listeners.BatListener;
import com.yalantis.beamazingtoday.listeners.OnItemClickListener;
import com.yalantis.beamazingtoday.listeners.OnOutsideClickedListener;
import com.yalantis.beamazingtoday.ui.adapter.BatAdapter;
import com.yalantis.beamazingtoday.ui.animator.BatItemAnimator;
import com.yalantis.beamazingtoday.ui.callback.BatCallback;
import com.yalantis.beamazingtoday.ui.widget.BatRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingDetailListFragment extends Fragment implements BatListener, OnItemClickListener, OnOutsideClickedListener {

    private static final int REQUEST_CODE = 1;

    private BatRecyclerView mRecyclerView;
    private BatAdapter mAdapter;
    private List<BatModel> mGoals;
    private List<String> mKeys;

    private BatItemAnimator mAnimator;

    private FloatingActionButton fab;

    // Firebase
    private DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_shopping_detail_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBat();
        initFabButton();
        initFirebase();
    }


    private void initFirebase(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("detailLists").child("1");

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

                Log.d("FIREBASE", "MOVED + ");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("FIREBASE", "CANCELLED + ");


            }
        });


    }

    private void initFabButton(){
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        fab.setImageResource(R.drawable.add_user);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void initBat(){
        //((TextView) findViewById(R.id.text_title)).setTypeface(TypefaceUtil.getAvenirTypeface(this));

        mRecyclerView = (BatRecyclerView) getActivity().findViewById(R.id.bat_recycler_view);
        mAnimator = new BatItemAnimator();

        mRecyclerView.getView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mGoals = new ArrayList<>();
        mKeys = new ArrayList<>();
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
        databaseReference.push().setValue(shoppingList);
    }

    @Override
    public void delete(int position) {
        databaseReference.child(mKeys.get(position)).removeValue();
    }

    @Override
    public void move(int from, int to) {
        BatModel model = mGoals.get(from);
        model.setChecked(!model.isChecked());
        databaseReference.child(mKeys.get(from)).setValue(model);
    }

    @Override
    public void onClick(BatModel item, int position) {
        Toast.makeText(getActivity(), item.getText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOutsideClicked() {
        mRecyclerView.revertAnimation();
    }


}
