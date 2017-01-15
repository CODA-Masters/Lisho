package com.codamasters.lisho.ui;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codamasters.lisho.R;
import com.codamasters.lisho.adapter.ShoppingListRecAdapter;
import com.codamasters.lisho.model.ShoppingList;
import com.codamasters.lisho.util.VerticalSpaceItemDecoration;
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
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private BoomMenuButton bmb;


    // RecyclerView
    private ShoppingListRecAdapter shoppingListRecAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
        initDrawer();
        initHamButton();

    }

    private void initView(){
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
        ShoppingList aux = new ShoppingList("1", 0, "Own List");
        ShoppingList aux2 = new ShoppingList("2", 1, "Group List");
        shoppingLists.add(aux);
        shoppingLists.add(aux2);


        shoppingListRecAdapter = new ShoppingListRecAdapter(this, R.layout.item_shopping_list, shoppingLists);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(shoppingListRecAdapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(24));


    }


    private void initDrawer(){
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
                        new PrimaryDrawerItem().withName("Remove ads").withIcon(R.drawable.bat),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("About").withIcon(R.drawable.dolphin),
                        new SecondaryDrawerItem().withName("Logout").withIcon(R.drawable.dolphin)
                ).build();

        drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                switch (position){
                    case 1:
                        break;
                    case 3: ;
                        break;
                    case 4: ;
                        break;
                }

                drawer.closeDrawer();
                return true;
            }
        });
    }

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

}
