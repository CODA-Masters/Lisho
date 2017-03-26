package com.codamasters.lisho.ui;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.codamasters.lisho.R;
import com.codamasters.lisho.adapter.ShoppingListRecAdapter;
import com.codamasters.lisho.login.AuthActivity;
import com.codamasters.lisho.login.LoginActivity;
import com.codamasters.lisho.model.ShoppingList;
import com.codamasters.lisho.util.VerticalSpaceItemDecoration;
import com.github.fabtransitionactivity.SheetLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;

public class MainActivity extends AuthActivity implements SheetLayout.OnFabAnimationEndListener {

    private Toolbar toolbar;
    private static final int REQUEST_CODE = 1;

    // RecyclerView
    private ShoppingListRecAdapter shoppingListRecAdapter;
    private RecyclerView recyclerView;
    private ArrayList<ShoppingList> shoppingLists;

    private FloatingActionButton fab;
    private SheetLayout sheetLayout;

    // Firebase
    private DatabaseReference databaseReference;
    private String userId;
    private ArrayList<String> shoppingListKeys;


    // ConnectionBroadCaster
    private BroadcastReceiver networkStateReceiver;
    private Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
        initDrawer();
        initFabButton();
        initSheetLayout();
        initFirebase();

    }

    public void onResume() {
        super.onResume();
        initBroadCastReceiver();
    }

    public void onPause() {
        super.onPause();
        this.unregisterReceiver(this.networkStateReceiver);
    }


    private void initBroadCastReceiver() {

        snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "No internet connection!", Snackbar.LENGTH_INDEFINITE);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null) {
                    NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                        snackbar.dismiss();
                    }
                }
                if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    snackbar.show();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        this.registerReceiver(this.networkStateReceiver, filter);
    }


    @Override
    public void onBackPressed() {
        initFabButton();
        super.onBackPressed();
    }

    private void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        shoppingLists = new ArrayList<>();
        shoppingListKeys = new ArrayList<>();

        shoppingListRecAdapter = new ShoppingListRecAdapter(this, R.layout.item_shopping_list, shoppingLists, shoppingListKeys);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(shoppingListRecAdapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(24));


    }

    private void initDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header3)
                .addProfiles(
                        //new ProfileDrawerItem().withName(mAuth.getCurrentUser().getDisplayName()).withEmail(mAuth.getCurrentUser().getEmail()).withIcon(mAuth.getCurrentUser().getPhotoUrl())
                        new ProfileDrawerItem().withName("Captain Jacks").withEmail("captain@jacks.com").withIcon(R.drawable.jellyfish)
                )
                .withDividerBelowHeader(true)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        final Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Shopping Lists").withIcon(R.drawable.bat),
                        new PrimaryDrawerItem().withName("Remove ads").withIcon(R.drawable.bat),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("About").withIcon(R.drawable.dolphin),
                        new SecondaryDrawerItem().withName("Logout").withIcon(R.drawable.dolphin)
                ).build();


        drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                switch (position) {
                    case 1:
                        FragmentManager fm = getFragmentManager();
                        if (fm.getBackStackEntryCount() > 0) {
                            onBackPressed();
                            initFabButton();
                        }
                        break;
                    case 2:
                        break;
                    case 4:
                        break;
                    case 5:
                        signOut();
                        break;
                }

                drawer.closeDrawer();
                return true;
            }
        });
    }

    private void initFabButton() {
        fab.setVisibility(View.VISIBLE);

        fab.setImageResource(R.drawable.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sheetLayout.expandFab();
                showAddListDialog();
            }
        });
    }

    private void initSheetLayout() {

        sheetLayout = (SheetLayout) findViewById(R.id.bottom_sheet);
        sheetLayout.setFab(fab);
        sheetLayout.setFabAnimationEndListener(this);

    }


    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            sheetLayout.contractFab();
        }
    }



    /*
    private void initHamButton(){
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.Ham);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_3);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_3);

        HamButton.Builder builder = new HamButton.Builder()
                .normalImageRes(R.drawable.butterfly)
                .normalTextRes(R.string.text_ham_add_shopping_own_title)
                .subNormalTextRes(R.string.text_ham_add_shopping_own_subtitle);
        bmb.addBuilder(builder);


        builder = new HamButton.Builder()
                .normalImageRes(R.drawable.butterfly)
                .normalTextRes(R.string.text_ham_add_shopping_group_title)
                .subNormalTextRes(R.string.text_ham_add_shopping_group_subtitle);
        bmb.addBuilder(builder);

        builder = new HamButton.Builder()
                .normalImageRes(R.drawable.butterfly)
                .normalTextRes(R.string.text_ham_add_shopping_group_title)
                .subNormalTextRes(R.string.text_ham_add_shopping_group_subtitle);
        bmb.addBuilder(builder);

    }
    */


    private void showAddListDialog() {
        new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.md_deep_orange_800)
                .setTitle(R.string.text_input_title)
                .setMessage(R.string.text_input_message)
                .setIcon(R.drawable.bat)
                .setInputFilter(R.string.text_input_error_message, new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return text.matches("\\w+");
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        ShoppingList shoppingList = new ShoppingList("1", 1, text, userId);
                        addToOwnUser(shoppingList);
                    }
                })
                .show();
    }


    private void addToOwnUser(ShoppingList shoppingList) {

        // Añadimos el objeto a listas
        databaseReference = FirebaseDatabase.getInstance().getReference().child("lists").push();
        String key = databaseReference.getKey();
        databaseReference.setValue(shoppingList);

        // Añadimos la key de la lista al usuario
        FirebaseDatabase.getInstance().getReference().child("user").child(userId).child(key).setValue(key);
    }

    private void initFirebase() {

        //userId = "test";
        userId = "juan@gmail.com";

        userId = userId.replace(".", "_");

        // Cargamos las key del usuario propio y consultamos en base a esa key la lista
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user").child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String keyList = dataSnapshot.getValue(String.class);
                shoppingListKeys.add(keyList);

                FirebaseDatabase.getInstance().getReference().child("lists").child(keyList).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ShoppingList model = dataSnapshot.getValue(ShoppingList.class);
                        shoppingLists.add(model);
                        shoppingListRecAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
